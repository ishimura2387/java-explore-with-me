package ru.practicum.ewm.mainservice.service.adminApi;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.user.NewUserRequest;
import ru.practicum.ewm.mainservice.dto.user.UserDto;

import java.util.List;

public interface AdminUserService {

    List<UserDto> get(List<Long> ids, Pageable pageable);

    UserDto add(NewUserRequest newUserRequest);

    void delete(long userId);

}
