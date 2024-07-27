package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e where (:usersList) is NULL or e.initiator.id in :usersList and (:statesList) " +
            "is NULL or e.state in :statesList and (:categoriesList) is null or e.category.id in :categoriesList and " +
            "e.eventDate > :start and e.eventDate < :end")
    List<Event> getEvents(@Param("usersList") List<Long> users, @Param("statesList") List<EventState> states,
                          @Param("categoriesList") List<Long> categories, @Param("start")LocalDateTime rangeStart,
                          @Param("end")LocalDateTime rangeEnd, Pageable pageable);

    Set<Event> findAllByIdIn(Set<Long> ids);

    @Query("select e from Event e where (e.participantLimit > e.confirmedRequests and e.participantLimit != 0) and " +
            "((:state) is null or e.state = :state) and ((:text_value) is null or ((upper(e.annotation) like upper(concat('%', :text_value, '%')) " +
            "or (upper(e.description) like upper(concat('%', :text_value, '%')))))) and ((:categoryList) is null or " +
            "e.category.id in :categoryList) and ((:paid_value) is null or e.paid = :paid_value) and e.eventDate between :start and :end")
    List<Event> getEventsAvailable(@Param("text_value") String text, @Param("categoryList")List<Long> categoryIds,
                                     @Param("paid_value") Boolean paid, @Param("start") LocalDateTime rangeStart,
                                     @Param("end") LocalDateTime rangeEnd, @Param("state") EventState state, Pageable pageable);

    @Query("select e from Event e where ((:state) is null or e.state = :state) and ((upper(e.annotation) like " +
            "upper(concat('%', :text_value, '%')) or ((upper(e.description) like upper(concat('%', :text_value, '%'))))) or (:text_value) is null) " +
            "and (e.category.id in :categoryList or (:categoryList) is NULL ) and ((:paid_value) is null or " +
            "e.paid = :paid_value) and e.eventDate between :start and :end")
    List<Event> getEventsNotAvailable(@Param("text_value") String text, @Param("categoryList")List<Long> categoryIds,
                                   @Param("paid_value") Boolean paid, @Param("start") LocalDateTime rangeStart,
                                   @Param("end") LocalDateTime rangeEnd, @Param("state") EventState state, Pageable pageable);

    List<Event> findAllByInitiatorId(Long id, Pageable pageable);
}
