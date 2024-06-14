package ru.practicum.ewm.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.mapper.EndpointHitMapper;
import ru.practicum.ewm.server.mapper.ViewStatsMapper;
import ru.practicum.ewm.server.model.EndpointHit;
import ru.practicum.ewm.server.model.ViewStats;
import ru.practicum.ewm.server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;
    private final EndpointHitRepository endpointHitRepository;

    public EndpointHitDto add(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointHitMapper.toEndpointHit(endpointHitDto);
        return endpointHitMapper.fromEndpointHit(endpointHitRepository.save(endpointHit));
    }

    public List<ViewStatsDto> get(LocalDateTime startTime, LocalDateTime endTime, List<String> uris, boolean unique) {
        if (unique) {
            if (uris != null) {
                List<ViewStats> viewStats = endpointHitRepository.findUnique(startTime, endTime, uris);
                return viewStats.stream().map(v -> viewStatsMapper.fromViewStats(v)).collect(Collectors.toList());
            } else {
                List<ViewStats> viewStats = endpointHitRepository.findUniqueAllUri(startTime, endTime);
                return viewStats.stream().map(v -> viewStatsMapper.fromViewStats(v)).collect(Collectors.toList());
            }
        } else {
            if (uris != null) {
                List<ViewStats> viewStats = endpointHitRepository.findNotUnique(startTime, endTime, uris);
                return viewStats.stream().map(v -> viewStatsMapper.fromViewStats(v)).collect(Collectors.toList());
            } else {
                List<ViewStats> viewStats = endpointHitRepository.findNotUniqueAllUri(startTime, endTime);
                return viewStats.stream().map(v -> viewStatsMapper.fromViewStats(v)).collect(Collectors.toList());
            }
        }
    }
}
