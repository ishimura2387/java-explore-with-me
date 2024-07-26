package ru.practicum.ewm.mainservice.dto.participationRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;
    private EventRequestStatusAction status;
}
