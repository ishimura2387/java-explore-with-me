package ru.practicum.ewm.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class EndpointHitController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHitDto add(@RequestBody EndpointHitDto endpointHitDto) {
        log.debug("Обработка запроса POST/hit");
        EndpointHitDto endpointHit = statsService.add(endpointHitDto);
        log.debug("Сохранен запрос: {}", endpointHit);
        return endpointHit;
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Ошибка периода выгрузки: Дата и время начала диапазона не может быть " +
                    "позже даты и времени окончания диапазона.");
        }
        log.debug("Обработка запроса GET/stats");
        List<ViewStatsDto> endpointHits = statsService.get(start, end, uris, unique);
        log.debug("Получена статистика по посещениям: {}", endpointHits);
        return endpointHits;
    }
}
