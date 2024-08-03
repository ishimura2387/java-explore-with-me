package ru.practicum.ewm.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.WebStatsClient;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.mainservice.dto.event.EventAction;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.event.EventRequester;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.dto.event.NewEventDto;
import ru.practicum.ewm.mainservice.dto.event.RequestEventParam;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventRequest;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusAction;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateResult;
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
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final UserRepository userRepository;
    private final WebStatsClient webStatsClient;
    private final LocalDateTime minTimeStump = LocalDateTime.of(1970, 01, 01, 8, 00, 00);
    private final LocalDateTime maxTimeStump = LocalDateTime.of(2038, 01, 19, 3, 14, 07);

    public List<EventFullDto> getAllWithParam(RequestEventParam requestEventParam) {
        List<EventFullDto> events = new ArrayList<>();
        if (requestEventParam.getRangeStart() == null) {
            requestEventParam.setRangeStart(LocalDateTime.now());
        }
        if (requestEventParam.getRangeEnd() == null) {
            requestEventParam.setRangeEnd(maxTimeStump);
        }
        if (requestEventParam.getRangeStart() != null && requestEventParam.getRangeEnd() != null) {
            if (requestEventParam.getRangeStart().isAfter(requestEventParam.getRangeEnd())) {
                throw new IllegalArgumentException("Время начала диапазона не может быть позже времени конца!");
            }
        }
        if (requestEventParam.getCategories() != null && requestEventParam.getCategories().size() == 1
                && requestEventParam.getCategories().get(0).equals(0L)) {
            requestEventParam.setCategories(null);
        }
        if (requestEventParam.getEventRequester().equals(EventRequester.Admin)) {
            if (requestEventParam.getUsers() != null && requestEventParam.getUsers().size() == 1
                    && requestEventParam.getUsers().get(0).equals(0L)) {
                requestEventParam.setUsers(null);
            }
            events = eventRepository.getEventsWithParam(requestEventParam.getUsers(), requestEventParam.getStates(),
                            requestEventParam.getCategories(), requestEventParam.getRangeStart(),
                            requestEventParam.getRangeEnd(), requestEventParam.getPageable(), null, null).stream()
                    .map(event -> eventMapper.toFullDto(event)).collect(Collectors.toList());
        }
        if (requestEventParam.getEventRequester().equals(EventRequester.PublicRequester)) {
            if (requestEventParam.isOnlyAvailable()) {
                List<EventState> states = new ArrayList<>();
                states.add(EventState.PUBLISHED);
                events = eventRepository.getEventsWithParamAvailable(null, states,
                                requestEventParam.getCategories(), requestEventParam.getRangeStart(),
                                requestEventParam.getRangeEnd(), requestEventParam.getPageable(),
                                requestEventParam.getText(), requestEventParam.getPaid()).stream()
                        .map(event -> eventMapper.toFullDto(event)).collect(Collectors.toList());
            } else {
                events = eventRepository.getEventsWithParam(null, null,
                                requestEventParam.getCategories(), requestEventParam.getRangeStart(),
                                requestEventParam.getRangeEnd(), requestEventParam.getPageable(),
                                requestEventParam.getText(), requestEventParam.getPaid()).stream()
                        .map(event -> eventMapper.toFullDto(event)).collect(Collectors.toList());
            }
        }

        return setViews(events);
    }

    public EventFullDto update(Long userId, Long eventId, UpdateEventRequest updateEventRequest, EventRequester eventRequester) {
        if (eventRequester.equals(EventRequester.User)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                            "Пользователь не найден!"));
        }
        Event eventOld = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        if (eventOld.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new DataIntegrityViolationException("Дата начала изменяемого события должна быть не ранее чем за час " +
                    "от даты публикации!");
        }
        if (updateEventRequest.getEventDate() != null && updateEventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Не корректная дата!");
        }
        Category category = null;
        if (updateEventRequest.getCategory() != null) {
            category = categoryRepository.findById(updateEventRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Ошибка проверки категории на наличие в Storage! " +
                            "Категория не найдена!"));
        }
        if (eventOld.getState().equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Нельзя изменять опубликованное событие!");
        }
        EventState eventState = null;
        if (updateEventRequest.getStateAction() != null) {
            if (updateEventRequest.getStateAction().equals(EventAction.PUBLISH_EVENT) && !eventOld.getState().equals(EventState.PENDING)) {
                throw new DataIntegrityViolationException("Событие можно публиковать, только если оно в состоянии ожидания " +
                        "публикации!");
            }
            if (updateEventRequest.getStateAction().equals(EventAction.REJECT_EVENT) && !eventOld.getState().equals(EventState.PENDING)) {
                throw new DataIntegrityViolationException("Событие можно отклонить, только если оно еще не опубликовано!");
            }
            if (updateEventRequest.getStateAction().equals(EventAction.PUBLISH_EVENT)) {
                eventState = EventState.PUBLISHED;
                eventOld.setPublishedOn(LocalDateTime.now());
            }
            if (updateEventRequest.getStateAction().equals(EventAction.REJECT_EVENT)) {
                eventState = EventState.CANCELED;
                eventOld.setState(EventState.CANCELED);
                return eventMapper.toFullDto(eventRepository.save(eventOld));
            }
            if (updateEventRequest.getStateAction().equals(EventAction.SEND_TO_REVIEW)) {
                eventState = EventState.PENDING;
                eventOld.setState(EventState.PENDING);
            }
            if (updateEventRequest.getStateAction().equals(EventAction.CANCEL_REVIEW)) {
                eventState = EventState.CANCELED;
                eventOld.setState(EventState.CANCELED);
                return eventMapper.toFullDto(eventRepository.save(eventOld));
            }
        }
        Event eventNew = new Event();
        EventFullDto dto = new EventFullDto();
        if (eventRequester.equals(EventRequester.Admin)) {
            eventNew = eventMapper.fromRequest(updateEventRequest, eventOld, eventState, category);
            eventNew = eventMapper.eventUpdate(eventNew, eventOld);
            dto = eventMapper.toFullDto(eventRepository.save(eventNew));
        }
        if (eventRequester.equals(EventRequester.User)) {
            eventNew = eventMapper.fromRequest(updateEventRequest, eventNew, eventState, category);
            dto = eventMapper.toFullDto(eventOld);
            eventMapper.eventUpdate(eventOld, eventNew);
            eventRepository.save(eventOld);
        }
        return dto;
    }

    public List<EventFullDto> getAllByInitiator(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        List<EventFullDto> eventFullDtos = events.stream().map(event -> eventMapper.toFullDto(event)).collect(Collectors.toList());
        return setViews(eventFullDtos);
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
        Event event = eventMapper.toEvent(newEventDto);
        event.setCategory(category);
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
        return eventMapper.toFullDto(eventRepository.save(event));
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

    public EventFullDto get(Long userId, Long eventId, EventRequester eventRequester) {
        if (eventRequester.equals(EventRequester.User)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                            "Пользователь не найден!"));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        if (eventRequester.equals(EventRequester.PublicRequester) && !event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Ошибка проверки события на наличие в Storage! Событие не найдено!");
        }
        eventRepository.save(event);
        List<EventFullDto> events = new ArrayList<>();
        events.add(eventMapper.toFullDto(event));
        return setViews(events).get(0);
    }

    private List<EventFullDto> setViews(List<EventFullDto> events) {
        List<String> uris = new ArrayList<>();
        for (EventFullDto event : events) {
            String uri = "/events/" + event.getId().toString();
            System.out.println(uri);
            uris.add(uri);
        }

        List<ViewStatsDto> stats = webStatsClient.getStats(minTimeStump, maxTimeStump, uris, true);

        for (EventFullDto event : events) {
            long views;
             List<ViewStatsDto> statsWithFilter = stats.stream().filter(e -> e.getUri().equals("/events/" + event.getId().toString()))
                    .collect(Collectors.toList());
             if (statsWithFilter.size() == 0) {
                 views = 0;
             } else {
                 views = statsWithFilter.get(0).getHits();
             }
            event.setViews(views);
        }
        return events;
    }
}
