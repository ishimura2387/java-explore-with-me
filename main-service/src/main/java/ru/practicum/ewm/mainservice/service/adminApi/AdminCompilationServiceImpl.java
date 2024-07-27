package ru.practicum.ewm.mainservice.service.adminApi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.dto.compilation.CompilationDto;
import ru.practicum.ewm.mainservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.mainservice.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.CompilationMapper;
import ru.practicum.ewm.mainservice.model.Compilation;
import ru.practicum.ewm.mainservice.model.Event;
import ru.practicum.ewm.mainservice.repository.CompilationRepository;
import ru.practicum.ewm.mainservice.repository.EventRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    public CompilationDto add(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>();
        events = findEvents(newCompilationDto.getEvents());
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto, events);
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        CompilationDto compilationDto = compilationMapper.fromCompilation(compilationRepository.save(compilation));
        return compilationDto;
    }

    public CompilationDto update(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки подборки на наличие в Storage! " +
                        "Подборка не найдена!"));
        Set<Event> events = new HashSet<>();
        events = findEvents(updateCompilationRequest.getEvents());
        Compilation compilationNew = compilationMapper.compilationUpdate(updateCompilationRequest, compilation, events);
        return compilationMapper.fromCompilation(compilationRepository.save(compilationNew));
    }

    public void delete(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки подборки на наличие в Storage! " +
                        "Подборка не найдена!"));
        compilationRepository.delete(compilation);
    }

    private Set<Event> findEvents(Set<Long> eventsId) {
        if (eventsId == null) {
            return Set.of();
        }
        return eventRepository.findAllByIdIn(eventsId);
    }
}
