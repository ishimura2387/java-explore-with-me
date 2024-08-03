package ru.practicum.ewm.mainservice.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.compilation.CompilationDto;
import ru.practicum.ewm.mainservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.mainservice.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto add(NewCompilationDto newCompilationDto);

    void delete(Long compilationId);

    CompilationDto update(Long compilationId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    CompilationDto get(Long id);
}
