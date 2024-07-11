package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> getAllByEventId(Long id);
    List<ParticipationRequest> getAllByRequesterId(Long id);
    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);
    List<ParticipationRequest> findAllByIdIn(List<Long> ids);
}
