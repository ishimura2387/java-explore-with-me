package ru.practicum.ewm.mainservice.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.mainservice.dto.compilation.CompilationDto;
import ru.practicum.ewm.mainservice.service.publicApi.PublicCompilationService;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {
    private final PublicCompilationService compilationServiceImpl;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAll(@RequestParam(defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(defaultValue = "10") @Min(1) int size,
                                                       @RequestParam(required = false) Boolean pinned) {
        log.debug("Обработка запроса GET/compilations");
        List<CompilationDto> compilations = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        compilations = compilationServiceImpl.getAll(pinned, pageable);
        log.debug("Получен список с размером: {}", compilations.size());
        return new ResponseEntity<>(compilations, HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> get(@PathVariable long compId) {
        log.debug("Обработка запроса GET/compilations/" + compId);
        CompilationDto compilation = compilationServiceImpl.get(compId);
        log.debug("Получена подборка: {}", compilation);
        return new ResponseEntity<>(compilation, HttpStatus.OK);
    }
}
