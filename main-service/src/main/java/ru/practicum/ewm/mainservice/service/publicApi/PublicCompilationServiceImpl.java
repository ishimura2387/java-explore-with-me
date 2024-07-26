package ru.practicum.ewm.mainservice.service.publicApi;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.dto.compilation.CompilationDto;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.CompilationMapper;
import ru.practicum.ewm.mainservice.model.Compilation;
import ru.practicum.ewm.mainservice.repository.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        List<CompilationDto> compilations = compilationRepository.findALlByPinned(pinned, pageable).stream()
                .map(compilation -> compilationMapper.fromCompilation(compilation)).collect(Collectors.toList());
        return compilations;
    }

    public CompilationDto get(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки подборки на наличие в Storage! " +
                        "Подборка не найдена!"));
        return compilationMapper.fromCompilation(compilation);
    }
}
