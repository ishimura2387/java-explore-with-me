package ru.practicum.ewm.mainservice.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewCompilationDto {
    @NotBlank
    @Size(min  = 1, max = 50)
    private String title;
    private Set<Long> events;
    private boolean pinned;
}
