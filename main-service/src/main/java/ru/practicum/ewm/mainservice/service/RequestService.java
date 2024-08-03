package ru.practicum.ewm.mainservice.service;

import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getAll(Long userId);

    ParticipationRequestDto add(Long userId, Long eventId);

    ParticipationRequestDto delete(Long userId, Long requestId);
}
