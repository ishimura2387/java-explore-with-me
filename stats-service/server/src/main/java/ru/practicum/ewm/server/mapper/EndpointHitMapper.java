package ru.practicum.ewm.server.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.server.model.EndpointHit;

@Mapper(componentModel = "spring", uses = {EndpointHitMapper.class})
public interface EndpointHitMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    EndpointHitDto fromEndpointHit(EndpointHit endpointHit);
}
