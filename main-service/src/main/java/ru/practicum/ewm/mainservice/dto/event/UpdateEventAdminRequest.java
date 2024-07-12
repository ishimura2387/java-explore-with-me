package ru.practicum.ewm.mainservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.mainservice.dto.category.CategoryDto;
import ru.practicum.ewm.mainservice.model.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    @Size(min = 20, max = 7000)
    private String description;
    private Long category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private long participantLimit;
    private boolean requestModeration;
    @Size(min = 3, max = 120)
    private String title;
    private AdminEventAction stateAction;
}
