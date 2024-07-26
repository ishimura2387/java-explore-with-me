package ru.practicum.ewm.mainservice.service.privateApi;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestState;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.mainservice.model.Event;
import ru.practicum.ewm.mainservice.model.ParticipationRequest;
import ru.practicum.ewm.mainservice.model.User;
import ru.practicum.ewm.mainservice.repository.EventRepository;
import ru.practicum.ewm.mainservice.repository.ParticipationRequestRepository;
import ru.practicum.ewm.mainservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateRequestsServiceImpl implements PrivateRequestsService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public List<ParticipationRequestDto> getAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<ParticipationRequest> participationRequests = participationRequestRepository.getAllByRequesterId(userId);
        return participationRequests.stream().map(participationRequest -> participationRequestMapper
                .fromParticipationRequest(participationRequest)).collect(Collectors.toList());
    }

    public ParticipationRequestDto add(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        Optional<ParticipationRequest> participationRequest = participationRequestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (!participationRequest.isEmpty()) {
            throw new DataIntegrityViolationException("Нельзя добавить повторный запрос!");
        }
        if (event.getInitiator().getId() == userId) {
            throw new DataIntegrityViolationException("Инициатор события не может добавить запрос на участие в своём событии!");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Нельзя участвовать в неопубликованном событии!");
        }
        if (event.getParticipantLimit() == event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new DataIntegrityViolationException("Достигнут лимит участников!");
        }
        ParticipationRequest participationRequestNew = new ParticipationRequest();
        participationRequestNew.setCreated(LocalDateTime.now());
        participationRequestNew.setEvent(event);
        participationRequestNew.setRequester(user);
        participationRequestNew.setStatus(ParticipationRequestState.PENDING);
        if (!event.isRequestModeration() && event.getParticipantLimit() == 0) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
            participationRequestNew.setStatus(ParticipationRequestState.CONFIRMED);
        }
        participationRequestRepository.save(participationRequestNew);
        return participationRequestMapper.fromParticipationRequest(participationRequestNew);
    }

    public ParticipationRequestDto delete(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки запроса на наличие в Storage! " +
                        "Запрос не найден!"));
        participationRequest.setStatus(ParticipationRequestState.CANCELED);
        Event event = participationRequest.getEvent();
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        eventRepository.save(event);
        return participationRequestMapper.fromParticipationRequest(participationRequestRepository.save(participationRequest));
    }
}
