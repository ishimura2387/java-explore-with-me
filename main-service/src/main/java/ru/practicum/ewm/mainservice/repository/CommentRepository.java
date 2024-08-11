package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long id, Pageable pageable);

    List<Comment> findAllByAuthorId(Long id, Pageable pageable);
}
