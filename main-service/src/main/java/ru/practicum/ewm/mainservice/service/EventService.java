package ru.practicum.ewm.mainservice.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.event.RequestEventParam;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.event.NewEventDto;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventRequest;
import ru.practicum.ewm.mainservice.dto.event.EventRequester;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    List<EventFullDto> getAllWithParam(RequestEventParam requestEventParam);

    EventFullDto update(Long userId, Long eventId, UpdateEventRequest updateEventRequest, EventRequester eventRequester);

    List<EventFullDto> getAllByInitiator(Long userId, Pageable pageable);

    EventFullDto add(Long userId, NewEventDto newEventDto);

    EventFullDto get(Long userId, Long eventId, EventRequester eventRequester);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventsId);

    EventRequestStatusUpdateResult updateState(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
