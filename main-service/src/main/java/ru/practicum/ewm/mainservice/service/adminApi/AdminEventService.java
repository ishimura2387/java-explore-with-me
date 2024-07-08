package ru.practicum.ewm.mainservice.service.adminApi;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
   List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
   EventFullDto update(Long id, UpdateEventAdminRequest updateEventAdminRequest);

}
