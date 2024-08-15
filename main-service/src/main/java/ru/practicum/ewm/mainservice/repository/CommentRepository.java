package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.mainservice.model.Comment;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long id, Pageable pageable);

    List<Comment> findAllByAuthorId(Long id, Pageable pageable);

    @Query("select count (c.id) from Event e left outer join Comment c on e.id = c.event.id where e.id in :listEventIds group by e.id")
    List<Long> getNumberOfComments(@Param("listEventIds") List<Long> eventsIds);
}
