package ru.practicum.ewm.mainservice.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.mainservice.dto.user.NewUserRequest;
import ru.practicum.ewm.mainservice.dto.user.UserDto;
import ru.practicum.ewm.mainservice.service.adminApi.AdminUserService;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminUserController {

    private final AdminUserService adminUserServiceImpl;

    @GetMapping
    public ResponseEntity<List<UserDto>> get(@RequestParam(required = false) @Min(0) List<Long> ids,
                                                           @RequestParam(defaultValue = "0") @Min(0) int from,
                                                           @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/admin/users");
        List<UserDto> users = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        users = adminUserServiceImpl.get(ids, pageable);
        log.debug("Получен список с размером: {}", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> add(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.debug("Обработка запроса POST/admin/users");
        UserDto user = adminUserServiceImpl.add(newUserRequest);
        log.debug("Создан пользователь: {}", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable long userId) {
        log.debug("Обработка запроса DELETE/admin/users/" + userId);
        adminUserServiceImpl.delete(userId);
        log.debug("Пользователь удален: {}", userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
