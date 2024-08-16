package ru.practicum.ewm.mainservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.mainservice.dto.comment.CommentDto;
import ru.practicum.ewm.mainservice.dto.comment.NewCommentDto;
import ru.practicum.ewm.mainservice.model.Comment;


@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public interface CommentMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment toComment(NewCommentDto newCommentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CommentDto toCommentDto(Comment comment);
}
