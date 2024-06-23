package ru.practicum.ewm.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;


public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ewm.dto.ViewStatsDto(s.app, s.uri, count (distinct s.ip)) from EndpointHit s " +
            "where s.uri in :uris and s.createTime >= :start and s.createTime <= :end group by s.app, s.uri, s.id")
    List<ViewStatsDto> findUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris")
    List<String> uris);

    @Query("select new ru.practicum.ewm.dto.ViewStatsDto(s.app, s.uri, count (distinct s.ip)) from EndpointHit s " +
            "where s.createTime >= :start and s.createTime <= :end group by s.app, s.uri, s.id")
    List<ViewStatsDto> findUniqueAllUri(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.ewm.dto.ViewStatsDto(s.app, s.uri, count (s.ip)) from " +
            "EndpointHit s where s.uri in :uris and s.createTime >= :start and s.createTime <= :end group by s.app, " +
            "s.uri")
    List<ViewStatsDto> findNotUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris")
    List<String> uris);

    @Query("select new ru.practicum.ewm.dto.ViewStatsDto(s.app, s.uri, count (s.ip)) from EndpointHit s where " +
            "s.createTime >= :start and s.createTime <= :end group by s.app, s.uri, s.id")
    List<ViewStatsDto> findNotUniqueAllUri(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
