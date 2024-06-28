package ru.practicum.ewm.client;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BaseClient {
    public EndpointHitDto addHit(EndpointHitDto endpointHitDto);

    public List<ViewStatsDto> getStats(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime, List<String> uris, boolean unique);

}
