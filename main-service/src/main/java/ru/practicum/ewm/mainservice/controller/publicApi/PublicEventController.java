package ru.practicum.ewm.mainservice.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.client.WebStatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.event.EventRequester;
import ru.practicum.ewm.mainservice.dto.event.RequestEventParam;
import ru.practicum.ewm.mainservice.service.EventService;

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
@Validated
public class PublicEventController {

    private final EventService eventServiceImpl;
    private final WebStatsClient webStatsClient;
    private final LocalDateTime maxTimeStump = LocalDateTime.of(2038, 01, 19, 03, 14,
            07);
    private final LocalDateTime minTimeStump = LocalDateTime.of(1970, 01, 01, 00, 00,
            00);

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getAll(@RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(defaultValue = "10") @Min(1) int size,
                                                     @RequestParam(required = false) String text,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) Boolean paid,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                     @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                     @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                     HttpServletRequest request) {
        log.debug("Обработка запроса GET/events");
        List<EventFullDto> events = new ArrayList<>();
        Sort methodSort;
        if (sort.equals("VIEWS")) {
            methodSort = Sort.by(Sort.Direction.ASC, "views");
        } else {
            methodSort = Sort.by(Sort.Direction.ASC, "eventDate");
        }
        RequestEventParam requestEventParam = RequestEventParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .pageable(PageRequest.of(from / size, size, methodSort))
                .eventRequester(EventRequester.PublicRequester)
                .build();
        events = eventServiceImpl.getAllWithParam(requestEventParam);
        log.debug("Получен список с размером: {}", events.size());
        saveStats(request.getRemoteAddr(), "ewm-main-service", request.getRequestURI());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> get(@PathVariable long id, HttpServletRequest request) {
        log.debug("Обработка запроса GET/events/" + id);
        saveStats(request.getRemoteAddr(), "ewm-main-service", request.getRequestURI());
        List<String> uris = new ArrayList<>();
        uris.add(request.getRequestURI());
        EventFullDto event = eventServiceImpl.get(null, id, EventRequester.PublicRequester);
        log.debug("Получено событие: {}", event);
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
