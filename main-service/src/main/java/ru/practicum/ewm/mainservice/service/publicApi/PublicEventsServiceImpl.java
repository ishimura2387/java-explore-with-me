package ru.practicum.ewm.mainservice.service.publicApi;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    public List<EventFullDto> getAll(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, Pageable pageable, Boolean onlyAvailable) {
        if (rangeEnd == null && rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        List<EventFullDto> events = new ArrayList<>();
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.MAX;
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

    public EventFullDto get(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Ошибка проверки события на наличие в Storage! Событие не найдено!");
        }
        return eventMapper.toFullDto(event);
    }
}
