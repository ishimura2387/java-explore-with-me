package ru.practicum.ewm.mainservice.controller.privateApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.mainservice.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.service.privateApi.PrivateRequestsService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateRequestController {
    private final PrivateRequestsService privateRequestsServiceImpl;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getAll(@PathVariable long userId) {
        log.debug("Обработка запроса GET/users/{userId}" + userId + "/requests");
        List<ParticipationRequestDto> participationRequests = new ArrayList<>();
        participationRequests = privateRequestsServiceImpl.getAll(userId);
        log.debug("Получен список с размером: {}", participationRequests.size());
        return new ResponseEntity<>(participationRequests, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> add(@PathVariable long userId, @RequestParam long eventId) {
        log.debug("Обработка запроса POST/users/{userId}" + userId + "/requests/?eventId=" + eventId);
        ParticipationRequestDto participationRequest = privateRequestsServiceImpl.add(userId, eventId);
        log.debug("Создан запрос: {}", participationRequest);
        return new ResponseEntity<>(participationRequest, HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> delete(@PathVariable long userId, @PathVariable long requestId) {
        log.debug("Обработка запроса PATCH/users/{userId}" + userId + "/requests/" + requestId + "/cancel");
        ParticipationRequestDto participationRequest = privateRequestsServiceImpl.delete(userId, requestId);
        log.debug("Отменена заявка на участие: {}", participationRequest);
        return new ResponseEntity<>(participationRequest, HttpStatus.OK);
    }
}
