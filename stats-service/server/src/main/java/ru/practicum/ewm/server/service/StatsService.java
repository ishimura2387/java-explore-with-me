package ru.practicum.ewm.server.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDto add(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> get(LocalDateTime startTime, LocalDateTime endTime, List<String> uris, boolean unique);
}
