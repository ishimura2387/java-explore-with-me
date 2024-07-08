package ru.practicum.ewm.mainservice.dto.participationRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipationRequestDto {
    @NotNull
    private LocalDateTime created;
    @PositiveOrZero
    private long event;
    private long id;
    @PositiveOrZero
    private long requester;
    private ParticipationRequestState status;
}
