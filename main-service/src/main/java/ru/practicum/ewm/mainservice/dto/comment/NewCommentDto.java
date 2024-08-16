package ru.practicum.ewm.mainservice.dto.comment;

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
public class NewCommentDto {
    @NotBlank
    @Size(max = 6000)
    private String text;
    long eventId;
}
