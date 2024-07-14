package ru.practicum.ewm.mainservice.service.publicApi;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.WebStatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.EventMapper;
import ru.practicum.ewm.mainservice.model.Event;
import ru.practicum.ewm.mainservice.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventsServiceImpl implements PublicEventsService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final WebStatsClient webStatsClient;
    private final LocalDateTime maxTimeStump = LocalDateTime.of(2038, 01, 19, 03, 14, 07);

    public List<EventFullDto> getAll(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, Pageable pageable, Boolean onlyAvailable) {
        List<EventFullDto> events = new ArrayList<>();
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = maxTimeStump;
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new IllegalArgumentException("Время начала диапазона не может быть позже времени конца!");
        }
        if (categoryIds != null && categoryIds.size() == 1 && categoryIds.get(0).equals(0L)) {
            categoryIds = null;
        }
       if (onlyAvailable) {
            events = eventRepository.getEventsAvailable(text, categoryIds, paid, rangeStart, rangeEnd,
                    EventState.PUBLISHED, pageable).stream().map(event -> eventMapper.toFullDto(event)).collect(Collectors.toList());
        } else {
            events = eventRepository.getEventsNotAvailable(text, categoryIds, paid, rangeStart, rangeEnd,
                    EventState.PUBLISHED, pageable).stream().map(event -> eventMapper.toFullDto(event)).collect(Collectors.toList());
        }
        return events;
    }

    public EventFullDto get(Long id, Long hits) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Ошибка проверки события на наличие в Storage! Событие не найдено!");
        }
        event.setViews(hits);
        eventRepository.save(event);
        return eventMapper.toFullDto(event);
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
