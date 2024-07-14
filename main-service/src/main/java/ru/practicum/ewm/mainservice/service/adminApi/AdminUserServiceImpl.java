package ru.practicum.ewm.mainservice.service.adminApi;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.dto.user.NewUserRequest;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.UserMapper;
import ru.practicum.ewm.mainservice.model.User;
import ru.practicum.ewm.mainservice.dto.user.UserDto;
import ru.practicum.ewm.mainservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> get(List<Long> ids, Pageable pageable) {
        List<User> users = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
             users = userRepository.findALlByIdIn(ids, pageable);
        } else {
            users = userRepository.findAll(pageable).toList();
        }
        return users.stream().map(user -> userMapper.fromUser(user)).collect(Collectors.toList());
    }

    public UserDto add(NewUserRequest newUserRequest) {
        User user = userMapper.toUser(newUserRequest);
        return userMapper.fromUser(userRepository.save(user));
    }

    public void delete(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        userRepository.deleteById(userId);
    }
}
