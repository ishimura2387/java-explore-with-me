package ru.practicum.ewm.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.dto.comment.CommentDto;
import ru.practicum.ewm.mainservice.dto.comment.CommentRequester;
import ru.practicum.ewm.mainservice.dto.comment.NewCommentDto;
import ru.practicum.ewm.mainservice.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.mainservice.dto.event.EventState;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.CommentMapper;
import ru.practicum.ewm.mainservice.model.Comment;
import ru.practicum.ewm.mainservice.model.Event;
import ru.practicum.ewm.mainservice.model.User;
import ru.practicum.ewm.mainservice.repository.CommentRepository;
import ru.practicum.ewm.mainservice.repository.EventRepository;
import ru.practicum.ewm.mainservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public CommentDto add(Long userId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        Event event = eventRepository.findById(newCommentDto.getEventId())
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Публиковать комментарии можно только к опубликованным событям!");
        }
        Comment comment = commentMapper.toComment(newCommentDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    public CommentDto update(Long userId, UpdateCommentRequest updateCommentRequest, CommentRequester commentRequester) {
        Comment comment = commentRepository.findById(updateCommentRequest.getCommentId())
                .orElseThrow(() -> new NotFoundException("Ошибка проверки комментария на наличие в Storage! " +
                        "Комментарий не найден!"));
        if (commentRequester.equals(CommentRequester.USER)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                            "Пользователь не найден!"));
            if (comment.getAuthor().getId().longValue() != userId.longValue()) {
                throw new DataIntegrityViolationException("Комментарии может изменять только автор или админ!");
            }
        }
        comment.setText(updateCommentRequest.getText());
        comment.setChanged(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    public void  delete(Long userId, Long commentId, CommentRequester commentRequester) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки комментария на наличие в Storage! " +
                        "Комментарий не найден!"));
        if (commentRequester.equals(CommentRequester.USER)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                            "Пользователь не найден!"));
            if (comment.getAuthor().getId().longValue() != userId.longValue()) {
                throw new DataIntegrityViolationException("Комментарии может удалять только автор или админ!");
            }
        }
        commentRepository.deleteById(commentId);
    }

    public List<CommentDto> getAllbyEventId(Long eventId, Pageable pageable) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки события на наличие в Storage! " +
                        "Событие не найдено!"));
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageable);
        return comments.stream().map(comment -> commentMapper.toCommentDto(comment)).collect(Collectors.toList());
    }

    public List<CommentDto> getAllbyUserId(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, pageable);
        return comments.stream().map(comment -> commentMapper.toCommentDto(comment)).collect(Collectors.toList());
    }
}
