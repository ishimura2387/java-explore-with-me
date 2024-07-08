package ru.practicum.ewm.mainservice.service.publicApi;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventsService {
    List<EventFullDto> getAll(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                              LocalDateTime rangeEnd, Pageable pageable, Boolean onlyAvailable);
    EventFullDto get(Long id);
}
