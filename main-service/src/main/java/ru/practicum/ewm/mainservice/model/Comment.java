package ru.practicum.ewm.mainservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne // здесь ленивая загрузка не нужна вообще считаю, ошибся
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne // здесь ленивая загрузка не нужна вообще считаю, ошибся
    @JoinColumn(name = "user_id")
    private User author;
    private LocalDateTime created;
    private LocalDateTime changed;
}
