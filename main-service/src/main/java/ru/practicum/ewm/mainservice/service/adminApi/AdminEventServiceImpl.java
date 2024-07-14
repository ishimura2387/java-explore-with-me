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
    private final LocalDateTime maxTimeStump = LocalDateTime.of(2038, 01, 19, 03, 14, 07);

    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        List<EventFullDto> events = new ArrayList<>();
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = maxTimeStump;
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new IllegalArgumentException("Время начала диапазона не может быть позже времени конца!");
        }
        if (categories != null && categories.size() == 1 && categories.get(0).equals(0L)) {
            categories = null;
        }
        events = eventRepository.getEvents(users, states, categories, rangeStart, rangeEnd, pageable).stream()
                .map(event -> eventMapper.toFullDto(event)).collect(Collectors.toList());
        return events;
    }

    public EventFullDto update(Long id, UpdateEventAdminRequest updateEventAdminRequest) {
        Event eventOld = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        if (eventOld.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
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
        if (updateEventAdminRequest.getStateAction().equals(AdminEventAction.PUBLISH_EVENT) && !eventOld.getState().equals(EventState.PENDING)) {
            throw new DataIntegrityViolationException("Событие можно публиковать, только если оно в состоянии ожидания " +
                    "публикации!");
        }
        if (updateEventAdminRequest.getStateAction().equals(AdminEventAction.REJECT_EVENT) && !eventOld.getState().equals(EventState.PENDING)) {
            throw new DataIntegrityViolationException("Событие можно отклонить, только если оно еще не опубликовано!");
        }
        Category category = null;
        if (updateEventAdminRequest.getCategory() != null) {
            category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Ошибка проверки категории на наличие в Storage! " +
                            "Категория не найдена!"));
        }
        EventFullDto eventFullDto = new EventFullDto();
        eventOld.setState(eventState);
        if (eventState.equals(EventState.CANCELED)) {
            eventRepository.save(eventOld);
            eventFullDto = eventMapper.toFullDto(eventOld);
        } else {
            eventOld.setPublishedOn(LocalDateTime.now());
            Event eventNew = new Event();
            eventNew = eventMapper.fromRequestAdmin(updateEventAdminRequest, eventOld, eventState, category);
            //eventNew = eventMapper.eventUpdate(eventNew, eventOld);
            eventRepository.save(eventNew);
            eventFullDto = eventMapper.toFullDto(eventNew);
        }
        return eventFullDto;
    }
}
