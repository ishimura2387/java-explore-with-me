package ru.practicum.ewm.mainservice.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.ewm.mainservice.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.mainservice.service.adminApi.AdminEventService;

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
    private final AdminEventService adminEventServiceImpl;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(required = false) List<Long> users,
                                                        @RequestParam(required = false) List<EventState> states,
                                                        @RequestParam(required = false) List<Long> categories,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                        @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                                        @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/admin/events");
        List<EventFullDto> events = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        events = adminEventServiceImpl.getEvents(users, states, categories, rangeStart, rangeEnd, pageable);
        log.debug("Получен список с размером: {}", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable long eventId, @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.debug("Обработка запроса GET/admin/events/" + eventId);
        EventFullDto event = adminEventServiceImpl.update(eventId, updateEventAdminRequest);
        log.debug("Изменено событие: {}", event);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
}
