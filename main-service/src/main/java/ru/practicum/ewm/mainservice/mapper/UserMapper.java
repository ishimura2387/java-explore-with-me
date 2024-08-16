package ru.practicum.ewm.mainservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.mainservice.dto.user.NewUserRequest;
import ru.practicum.ewm.mainservice.model.User;
import ru.practicum.ewm.mainservice.dto.user.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequest newUserRequest);

    UserDto fromUser(User user);

}