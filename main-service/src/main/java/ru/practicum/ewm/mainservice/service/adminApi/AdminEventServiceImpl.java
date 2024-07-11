package ru.practicum.ewm.mainservice.service.adminApi;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.dto.event.AdminEventAction;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.EventMapper;
import ru.practicum.ewm.mainservice.model.Category;
import ru.practicum.ewm.mainservice.model.Event;
import ru.practicum.ewm.mainservice.repository.CategoryRepository;
import ru.practicum.ewm.mainservice.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;

    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        List<EventFullDto> events = new ArrayList<>();
        events = eventRepository.getEvents(users, states, categories, rangeStart, rangeEnd, pageable).stream()
                .map(event -> eventMapper.toFullDto(event)).collect(Collectors.toList());
        return events;
    }

    public EventFullDto update(Long id, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new DataIntegrityViolationException("Дата начала изменяемого события должна быть не ранее чем за час " +
                    "от даты публикации!");
        }
        EventState eventState = null;
            if (updateEventAdminRequest.getStateAction().equals(AdminEventAction.PUBLISH_EVENT)) {
                eventState = EventState.PUBLISHED;
            }
            if (updateEventAdminRequest.getStateAction().equals(AdminEventAction.REJECT_EVENT)) {
                eventState = EventState.CANCELED;
            }
            if (updateEventAdminRequest.getStateAction().equals(AdminEventAction.PUBLISH_EVENT) && !event.getState().equals(EventState.PENDING)) {
                throw new DataIntegrityViolationException("Событие можно публиковать, только если оно в состоянии ожидания " +
                        "публикации!");
            }
            if (updateEventAdminRequest.getStateAction().equals(AdminEventAction.REJECT_EVENT) && !event.getState().equals(EventState.PENDING)) {
                throw new DataIntegrityViolationException("Событие можно отклонить, только если оно еще не опубликовано!");
            }
        Category category = null;
         if (updateEventAdminRequest.getCategory() != null) {
            category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Ошибка проверки категории на наличие в Storage! " +
                            "Категория не найдена!"));
        }
        if (eventState.equals(EventState.CANCELED)) {
            event.setState(eventState);
            return eventMapper.toFullDto(eventRepository.save(event));
        } else {
            Event eventNew = eventMapper.eventAdminUpdate(updateEventAdminRequest, event, eventState, category);
            if (eventState.equals(EventState.PUBLISHED)) {
                eventNew.setPublishedOn(LocalDateTime.now());
            }
            return eventMapper.toFullDto(eventRepository.save(eventNew));
        }
    }
}
