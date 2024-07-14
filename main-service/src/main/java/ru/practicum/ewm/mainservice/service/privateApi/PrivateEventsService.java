package ru.practicum.ewm.mainservice.service.privateApi;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.ewm.mainservice.dto.event.NewEventDto;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventsService {
    List<EventFullDto> getAll(Long userId, Pageable pageable);
    EventFullDto add(Long userId, NewEventDto newEventDto);

    EventFullDto get(Long userId, Long eventId, Long hits);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventsId);

    EventRequestStatusUpdateResult updateState(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
