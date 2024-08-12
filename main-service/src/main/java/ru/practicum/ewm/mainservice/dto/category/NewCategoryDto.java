package ru.practicum.ewm.mainservice.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewCategoryDto {
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;
}
