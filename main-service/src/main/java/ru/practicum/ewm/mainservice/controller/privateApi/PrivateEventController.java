package ru.practicum.ewm.mainservice.controller.privateApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.mainservice.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.ewm.mainservice.dto.event.NewEventDto;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.service.privateApi.PrivateEventsService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final PrivateEventsService privateEventsServiceImpl;
    @GetMapping
    public ResponseEntity<List<EventFullDto>> getAll(@PathVariable long userId, @RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/users/" + userId + "/events");
        List<EventFullDto> events = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        events = privateEventsServiceImpl.getAll(userId, pageable);
        log.debug("Получен список с размером: {}", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<EventFullDto> add(@PathVariable long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.debug("Обработка запроса POST/users/" + userId + "/events");
        EventFullDto event = privateEventsServiceImpl.add(userId, newEventDto);
        log.debug("Создано событие: {}", event);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> get(@PathVariable long userId, @PathVariable long eventId) {
        log.debug("Обработка запроса GET/users/" + userId + "/events/" + eventId);
        EventFullDto event = privateEventsServiceImpl.get(userId, eventId);
        log.debug("Получено событие: {}", event);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable long userId, @PathVariable long eventId,
                               @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.debug("Обработка запроса PATCH/users/" + userId + "/events/" + eventId);
        EventFullDto event = privateEventsServiceImpl.update(userId, eventId, updateEventUserRequest);
        log.debug("Изменено событие: {}", event);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable long userId, @PathVariable long eventId) {
        log.debug("Обработка запроса GET/users/" + userId + "/events/" + eventId + "/requests");
        List<ParticipationRequestDto> participationRequests = new ArrayList<>();
        participationRequests = privateEventsServiceImpl.getRequests(userId, eventId);
        log.debug("Получен список с размером: {}", participationRequests.size());
        return new ResponseEntity<>(participationRequests, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateState(@PathVariable long userId, @PathVariable long eventId,
                                                      @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.debug("Обработка запроса PATCH/users/" + userId + "/events/" + eventId + "/requests");
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = privateEventsServiceImpl
                .updateState(userId, eventId, eventRequestStatusUpdateRequest);
        log.debug("Количество подтвержденных событий: {}", eventRequestStatusUpdateResult.getConfirmedRequests().size());
        log.debug("Количество отклоненных событий: {}", eventRequestStatusUpdateResult.getRejectedRequests().size());
        return new ResponseEntity<>(eventRequestStatusUpdateResult, HttpStatus.OK);
    }
}
