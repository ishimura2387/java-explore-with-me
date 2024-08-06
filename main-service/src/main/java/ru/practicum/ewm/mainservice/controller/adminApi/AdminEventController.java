package ru.practicum.ewm.mainservice.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.dto.event.RequestEventParam;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventRequest;
import ru.practicum.ewm.mainservice.dto.event.EventRequester;
import ru.practicum.ewm.mainservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {
    private final EventService eventServiceImpl;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(required = false) List<Long> users,
                                                        @RequestParam(required = false) List<EventState> states,
                                                        @RequestParam(required = false) List<Long> categories,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                                        @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/admin/events");
        List<EventFullDto> events = new ArrayList<>();
        RequestEventParam requestEventParam = RequestEventParam.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .pageable(PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id")))
                .eventRequester(EventRequester.Admin)
                .build();
        events = eventServiceImpl.getAllWithParam(requestEventParam);
        log.debug("Получен список с размером: {}", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable long eventId,
                                               @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        log.debug("Обработка запроса GET/admin/events/" + eventId);
        EventFullDto event = eventServiceImpl.update(null, eventId, updateEventRequest, EventRequester.Admin);
        log.debug("Изменено событие: {}", event);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
}
