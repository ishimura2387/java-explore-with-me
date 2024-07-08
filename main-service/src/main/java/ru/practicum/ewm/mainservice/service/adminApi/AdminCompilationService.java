package ru.practicum.ewm.mainservice.service.adminApi;

import ru.practicum.ewm.mainservice.dto.compilation.CompilationDto;
import ru.practicum.ewm.mainservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.mainservice.dto.compilation.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto add(NewCompilationDto newCompilationDto);
    void delete(Long compilationId);

    CompilationDto update(Long compilationId, UpdateCompilationRequest updateCompilationRequest);
}
