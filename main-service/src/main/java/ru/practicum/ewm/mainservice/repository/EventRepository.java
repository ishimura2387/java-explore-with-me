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

    Set<Event> findAllByIdIn(Set<Long> ids);


    List<Event> findAllByInitiatorId(Long id, Pageable pageable);

    List<Event> findAllByCategoryId(long id);

    @Query("select e from Event e where ((:usersList) is null or e.initiator.id in :usersList) and ((:statesList) " +
            "is NULL or e.state in :statesList) and ((:categoriesList) is null or e.category.id in :categoriesList) and " +
            "e.eventDate between :start and :end and ((upper(e.annotation) like upper(concat('%', :text_value, '%')) or " +
            "((upper(e.description) like upper(concat('%', :text_value, '%'))))) or (:text_value) is null) " +
            "and ((:paid_value) is null or e.paid = :paid_value)")
    List<Event> getEventsWithParam(@Param("usersList") List<Long> users, @Param("statesList") List<EventState> states,
                          @Param("categoriesList") List<Long> categories, @Param("start")LocalDateTime rangeStart,
                          @Param("end")LocalDateTime rangeEnd, Pageable pageable, @Param("text_value") String text,
                          @Param("paid_value") Boolean paid);

    @Query("select e from Event e where ((:usersList) is null or e.initiator.id in :usersList) and ((:statesList) " +
            "is NULL or e.state in :statesList) and ((:categoriesList) is null or e.category.id in :categoriesList) and " +
            "e.eventDate between :start and :end and ((upper(e.annotation) like upper(concat('%', :text_value, '%')) or " +
            "((upper(e.description) like upper(concat('%', :text_value, '%'))))) or (:text_value) is null) " +
            "and ((:paid_value) is null or e.paid = :paid_value) and (e.participantLimit > e.confirmedRequests and e.participantLimit != 0)")
    List<Event> getEventsWithParamAvailable(@Param("usersList") List<Long> users, @Param("statesList") List<EventState> states,
                                   @Param("categoriesList") List<Long> categories, @Param("start")LocalDateTime rangeStart,
                                   @Param("end")LocalDateTime rangeEnd, Pageable pageable, @Param("text_value") String text,
                                   @Param("paid_value") Boolean paid);
}
