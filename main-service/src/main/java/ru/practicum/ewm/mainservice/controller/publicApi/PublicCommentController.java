package ru.practicum.ewm.mainservice.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.mainservice.dto.comment.CommentDto;
import ru.practicum.ewm.mainservice.service.CommentService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCommentController {

    private final CommentService commentServiceImpl;

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CommentDto>> getCommentsByEventId(@PathVariable long eventId,
                                                                 @RequestParam(defaultValue = "0") @Min(0) int from,
                                                                 @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/users/event/" + eventId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"));
        List<CommentDto> comments = commentServiceImpl.getAllbyEventId(eventId, pageable);
        log.debug("Получен список с размером: {}", comments.size());
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
