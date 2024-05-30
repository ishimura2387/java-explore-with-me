package ru.practicum.ewm.server.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.model.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ViewStats toViewStats(ViewStatsDto viewStatsDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ViewStatsDto fromViewStats(ViewStats viewStats);
}
