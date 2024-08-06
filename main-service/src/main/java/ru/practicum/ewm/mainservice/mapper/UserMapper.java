package ru.practicum.ewm.mainservice.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.mainservice.dto.user.NewUserRequest;
import ru.practicum.ewm.mainservice.model.User;
import ru.practicum.ewm.mainservice.dto.user.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(NewUserRequest newUserRequest);

    UserDto fromUser(User user);

}