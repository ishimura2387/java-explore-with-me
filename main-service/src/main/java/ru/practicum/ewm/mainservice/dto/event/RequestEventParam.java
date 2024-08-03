package ru.practicum.ewm.mainservice.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class RequestEventParam {
    List<Long> users;
    List<EventState> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Pageable pageable;
    String text;
    Boolean paid;
    boolean onlyAvailable;
    EventRequester eventRequester;
}
