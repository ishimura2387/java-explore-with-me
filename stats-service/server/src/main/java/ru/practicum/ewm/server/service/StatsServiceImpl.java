package ru.practicum.ewm.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.mapper.EndpointHitMapper;
import ru.practicum.ewm.server.model.EndpointHit;
import ru.practicum.ewm.server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final EndpointHitMapper endpointHitMapper;
    private final EndpointHitRepository endpointHitRepository;

    public EndpointHitDto add(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointHitMapper.toEndpointHit(endpointHitDto);
        return endpointHitMapper.fromEndpointHit(endpointHitRepository.save(endpointHit));
    }

    public List<ViewStatsDto> get(LocalDateTime startTime, LocalDateTime endTime, List<String> uris, boolean unique) {
        if (unique) {
            if (uris != null) {
                return endpointHitRepository.findUnique(startTime, endTime, uris);
            } else {
                return endpointHitRepository.findUniqueAllUri(startTime, endTime);
            }
        } else {
            if (uris != null) {
                return endpointHitRepository.findNotUnique(startTime, endTime, uris);
            } else {
                return endpointHitRepository.findNotUniqueAllUri(startTime, endTime);
            }
        }
    }
}
