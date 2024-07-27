package ru.practicum.ewm.mainservice.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    private String title;
}
