package ru.practicum.ewm.mainservice.service.publicApi;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.compilation.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    CompilationDto get(Long id);
}
