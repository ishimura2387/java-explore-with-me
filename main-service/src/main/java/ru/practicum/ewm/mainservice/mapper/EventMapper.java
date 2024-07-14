package ru.practicum.ewm.mainservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.mainservice.dto.event.EventFullDto;
import ru.practicum.ewm.mainservice.dto.event.EventShortDto;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.dto.event.NewEventDto;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.mainservice.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.mainservice.model.Category;
import ru.practicum.ewm.mainservice.model.Event;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface EventMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", source = "category")
    Event toEvent(NewEventDto newEventDto, Category category);
    EventFullDto toFullDto(Event event);
    EventShortDto toShortDto(Event event);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "event.state", source = "eventState")
    @Mapping(target = "category", source = "cat")
    @Mapping(target = "id", ignore = true)
    Event fromRequestAdmin(UpdateEventAdminRequest updateEventAdminRequest, @MappingTarget Event event, EventState eventState, Category cat);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "event.state", source = "eventState")
    @Mapping(target = "category", source = "cat")
    @Mapping(target = "id", ignore = true)
    Event fromRequestUser(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event, EventState eventState, Category cat);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event eventUpdate(@MappingTarget Event eventNew, Event Old);
}
