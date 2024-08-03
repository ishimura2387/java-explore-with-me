package ru.practicum.ewm.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    public CompilationDto add(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>();
        events = findEvents(newCompilationDto.getEvents());
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto, events);
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

    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        List<CompilationDto> compilations = new ArrayList<>();
        if (pinned != null) {
            compilations = compilationRepository.findALlByPinned(pinned, pageable).stream()
                    .map(compilation -> compilationMapper.fromCompilation(compilation)).collect(Collectors.toList());
        } else {
            compilations = compilationRepository.findAll(pageable).stream()
                    .map(compilation -> compilationMapper.fromCompilation(compilation)).collect(Collectors.toList());;
        }
        return compilations;
    }

    public CompilationDto get(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки подборки на наличие в Storage! " +
                        "Подборка не найдена!"));
        return compilationMapper.fromCompilation(compilation);
    }
}
