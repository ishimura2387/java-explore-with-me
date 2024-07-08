package ru.practicum.ewm.mainservice.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.client.WebStatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.service.publicApi.PublicEventsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@ComponentScan(basePackages = "ru.practicum.ewm.client")
public class PublicEventController {
    private final PublicEventsService publicEventsServiceImpl;
    private final WebStatsClient webStatsClient;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getAll(@RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(defaultValue = "10") @Min(1) int size,
                                                     @RequestParam(required = false) String text,
                                                     @RequestParam(required = false) List<Long> categoryIds,
                                                     @RequestParam(required = false) Boolean paid,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                     @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                     HttpServletRequest request) {
        log.debug("Обработка запроса GET/events");
        List<EventFullDto> events = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        events = publicEventsServiceImpl.getAll(text, categoryIds, paid, rangeStart, rangeEnd, pageable, onlyAvailable);
        log.debug("Получен список с размером: {}", events.size());
        saveStats(request.getRemoteAddr(), "ewm-main-service", request.getRequestURI());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> get(@PathVariable long id, HttpServletRequest request) {
        log.debug("Обработка запроса GET/events/" + id);
        EventFullDto event = publicEventsServiceImpl.get(id);
        log.debug("Получено событие: {}", event);
        saveStats(request.getRemoteAddr(), "ewm-main-service", request.getRequestURI());
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
    private void saveStats(String ip, String app, String uri) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(app);
        endpointHitDto.setUri(uri);
        endpointHitDto.setIp(ip);
        endpointHitDto.setCreateTime(LocalDateTime.now());
        webStatsClient.addHit(endpointHitDto);
    }
}
