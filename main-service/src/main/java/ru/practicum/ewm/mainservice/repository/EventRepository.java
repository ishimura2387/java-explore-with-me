package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e where e.initiator.id in :usersList and e.state in :statesList and e.category.id in " +
            ":categoriesList and e.eventDate > :start and e.eventDate < :end")
    List<Event> getEvents(@Param("usersList") List<Long> users, @Param("statesList") List<EventState> states,
                          @Param("categoriesList") List<Long> categories, @Param("start")LocalDateTime rangeStart,
                          @Param("end")LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> ids);
    @Query("select e from Event e where e.state = :state and (upper(e.annotation) like upper(concat('%', :text_value, '%')) or upper(e.description) " +
            "like upper(concat('%', :text_value, '%'))) and e.category.id in :categoryList and e.paid = :paid_value and " +
            "e.eventDate > :start and e.eventDate < :end")
    List<Event> getEventsWithFilters(@Param("text_value") String text, @Param("categoryList")List<Long> categoryIds,
                                     @Param("paid_value") Boolean paid, @Param("start") LocalDateTime rangeStart,
                                     @Param("end") LocalDateTime rangeEnd, @Param("state") EventState state, Pageable pageable);
    List<Event> findAllByInitiatorId(Long id);
}
