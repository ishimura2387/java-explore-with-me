package ru.practicum.ewm.mainservice.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.comment.CommentDto;
import ru.practicum.ewm.mainservice.dto.comment.CommentRequester;
import ru.practicum.ewm.mainservice.dto.comment.NewCommentDto;
import ru.practicum.ewm.mainservice.dto.comment.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDto add(Long userId, NewCommentDto newCommentDto);

    CommentDto update(Long userId, UpdateCommentRequest updateCommentRequest, CommentRequester commentRequester);

    void  delete(Long userId, Long commentId, CommentRequester commentRequester);

    List<CommentDto> getAllbyEventId(Long eventId, Pageable pageable);

    List<CommentDto> getAllbyUserId(Long userId, Pageable pageable);
}
