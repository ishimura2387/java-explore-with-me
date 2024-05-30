package ru.practicum.ewm.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.server.model.EndpointHit;
import ru.practicum.ewm.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;


public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select s.app as app, s.uri as uri, count (distinct s.ip) as hits from EndpointHit s where s.uri in :uris " +
            "and s.createTime >= :start and s.createTime <= :end group by s.app, s.uri order by hits desc")
    List<ViewStats> findUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris")
    List<String> uris);

    @Query("select s.app as app, s.uri as uri, count (distinct s.ip) as hits from EndpointHit s where " +
            "s.createTime >= :start and s.createTime <= :end group by s.app, s.uri order by hits desc")
    List<ViewStats> findUniqueAllUri(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select s.app as app, s.uri as uri, s.ip as hits from EndpointHit s where s.uri in :uris " +
            "and s.createTime >= :start and s.createTime <= :end group by s.app, s.uri order by hits desc")
    List<ViewStats> findNotUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris")
    List<String> uris);

    @Query("select s.app as app, s.uri as uri, s.ip as hits from EndpointHit s where s.createTime >= :start and " +
            "s.createTime <= :end group by s.app, s.uri order by hits desc")
    List<ViewStats> findNotUniqueAllUri(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
