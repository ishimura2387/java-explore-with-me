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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.mainservice.dto.comment.CommentDto;
import ru.practicum.ewm.mainservice.dto.comment.CommentRequester;
import ru.practicum.ewm.mainservice.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.mainservice.service.CommentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentController {

    private final CommentService commentServiceImpl;

    @PatchMapping("/{comId}")
    public ResponseEntity<CommentDto> update(@PathVariable long comId, @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {
        log.debug("Обработка запроса PATCH/admin/comments/" + comId);
        CommentDto comment = commentServiceImpl.update(null, comId, updateCommentRequest, CommentRequester.Admin);
        log.debug("Изменен комментарий: {}, comId={}", comment, comId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/{comId}")
    public ResponseEntity<Void> delete(@PathVariable long comId) {
        log.debug("Обработка запроса DELETE/admin/comments/" + comId);
        commentServiceImpl.delete(null, comId, CommentRequester.Admin);
        log.debug("Комментарий удален: {}", comId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CommentDto>> getCommentsByEventId(@PathVariable long eventId,
                                                                 @RequestParam(defaultValue = "0") @Min(0) int from,
                                                                 @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/admin/comments/event/" + eventId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"));
        List<CommentDto> comments = commentServiceImpl.getAllbyEventId(eventId, pageable);
        log.debug("Получен список с размером: {}", comments.size());
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentDto>> getCommentsByUserId(@PathVariable long userId,
                                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                                @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/admin/comments/user/" + userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"));
        List<CommentDto> comments = commentServiceImpl.getAllbyUserId(userId, pageable);
        log.debug("Получен список с размером: {}", comments.size());
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
