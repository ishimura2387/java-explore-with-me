package ru.practicum.ewm.mainservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestState;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id")
    @Lazy
    private Event event;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Lazy
    private User requester;
    @Enumerated(EnumType.STRING)
    private ParticipationRequestState status;
}
