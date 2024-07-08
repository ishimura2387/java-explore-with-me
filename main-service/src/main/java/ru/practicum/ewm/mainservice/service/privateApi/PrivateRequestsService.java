package ru.practicum.ewm.mainservice.service.privateApi;

import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestsService {
    List<ParticipationRequestDto> getAll(Long userId);

    ParticipationRequestDto add(Long userId, Long eventId);

    ParticipationRequestDto delete(Long userId, Long requestId);
}
