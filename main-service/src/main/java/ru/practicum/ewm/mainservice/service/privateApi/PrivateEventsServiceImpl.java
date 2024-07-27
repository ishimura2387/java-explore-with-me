package ru.practicum.ewm.mainservice.service.privateApi;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.dto.event.UserEventAction;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusAction;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.ewm.mainservice.dto.event.NewEventDto;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestState;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.EventMapper;
import ru.practicum.ewm.mainservice.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.mainservice.model.Category;
import ru.practicum.ewm.mainservice.model.Event;
import ru.practicum.ewm.mainservice.model.ParticipationRequest;
import ru.practicum.ewm.mainservice.model.User;
import ru.practicum.ewm.mainservice.repository.CategoryRepository;
import ru.practicum.ewm.mainservice.repository.EventRepository;
import ru.practicum.ewm.mainservice.repository.ParticipationRequestRepository;
import ru.practicum.ewm.mainservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateEventsServiceImpl implements PrivateEventsService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<EventFullDto> getAll(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return events.stream().map(event -> eventMapper.toFullDto(event)).collect(Collectors.toList());
    }

    public EventFullDto add(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Не корректная дата!");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Ошибка проверки категории на наличие в Storage! " +
                        "Категория не найдена!"));
        Event event = eventMapper.toEvent(newEventDto, category);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(user);
        event.setState(EventState.PENDING);
        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        }
        EventFullDto eventFullDto = eventMapper.toFullDto(eventRepository.save(event));
        return eventFullDto;
    }

    public EventFullDto get(Long userId, Long eventId, Long hits) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        event.setViews(hits);
        eventRepository.save(event);
        return eventMapper.toFullDto(event);
    }

    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        Event eventOld = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        if (eventOld.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DataIntegrityViolationException("Дата и время на которые намечено событие не может быть раньше, чем через два " +
                    "часа от текущего момента!");
        }
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Не корректная дата!");
        }
        Category category = null;
        if (updateEventUserRequest.getCategory() != null) {
            category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Ошибка проверки категории на наличие в Storage! " +
                            "Категория не найдена!"));
        }
        if (eventOld.getState().equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Нельзя изменять опубликованное событие!");
        }
        EventState eventState = null;
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(UserEventAction.SEND_TO_REVIEW)) {
                eventState = EventState.PENDING;
                eventOld.setState(EventState.PENDING);
            }
            if (updateEventUserRequest.getStateAction().equals(UserEventAction.CANCEL_REVIEW)) {
                eventState = EventState.CANCELED;
                eventOld.setState(EventState.CANCELED);
                return eventMapper.toFullDto(eventRepository.save(eventOld));
            }
        }
        Event eventNew = new Event();
        eventNew = eventMapper.fromRequestUser(updateEventUserRequest, eventNew, eventState, category);
        EventFullDto dto = eventMapper.toFullDto(eventOld);
        eventMapper.eventUpdate(eventOld, eventNew);
        eventRepository.save(eventOld);
        return dto;
    }

    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        List<ParticipationRequest> participationRequests = participationRequestRepository.getAllByEventId(eventId);
        return participationRequests.stream().map(participationRequest -> participationRequestMapper
                .fromParticipationRequest(participationRequest)).collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateState(Long userId, Long eventId,
                                                      EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        if (event.getParticipantLimit() == event.getConfirmedRequests()
                && eventRequestStatusUpdateRequest.getStatus().equals(EventRequestStatusAction.CONFIRMED)
                && event.getParticipantLimit() != 0) {
            throw new DataIntegrityViolationException("Нельзя подтвердить заявку, если уже достигнут лимит по заявкам " +
                    "на данное событие!");
        }
        List<ParticipationRequest> participationRequests = participationRequestRepository
                .findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        long eventParticipantLimit = event.getParticipantLimit();
        long eventConfirmedRequests = event.getConfirmedRequests();
        switch (eventRequestStatusUpdateRequest.getStatus()) {
            case CONFIRMED:
                for (ParticipationRequest participationRequest : participationRequests) {
                    if (!event.isRequestModeration() || eventParticipantLimit == 0) {
                        participationRequest.setStatus(ParticipationRequestState.CONFIRMED);
                        confirmedRequests.add(participationRequestMapper.fromParticipationRequest(participationRequest));
                    } else {
                        if (eventParticipantLimit > eventConfirmedRequests && eventParticipantLimit != 0) {
                            participationRequest.setStatus(ParticipationRequestState.CONFIRMED);
                            participationRequestRepository.save(participationRequest);
                            eventConfirmedRequests++;
                            confirmedRequests.add(participationRequestMapper.fromParticipationRequest(participationRequest));
                        } else {
                            if (participationRequest.getStatus().equals(ParticipationRequestState.CONFIRMED)) {
                                throw new DataIntegrityViolationException("Нельзя оменять уже принятую заявку!");
                            }
                            participationRequest.setStatus(ParticipationRequestState.REJECTED);
                            participationRequestRepository.save(participationRequest);
                            rejectedRequests.add(participationRequestMapper.fromParticipationRequest(participationRequest));
                        }
                    }
                }
                break;
            case REJECTED:
                for (ParticipationRequest participationRequest : participationRequests) {
                    if (participationRequest.getStatus().equals(ParticipationRequestState.CONFIRMED)) {
                        throw new DataIntegrityViolationException("Нельзя оменять уже принятую заявку!");
                    }
                    participationRequest.setStatus(ParticipationRequestState.REJECTED);
                    participationRequestRepository.save(participationRequest);
                    rejectedRequests.add(participationRequestMapper.fromParticipationRequest(participationRequest));
                }
                break;
        }
        event.setConfirmedRequests(eventConfirmedRequests);
        eventRepository.save(event);
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
