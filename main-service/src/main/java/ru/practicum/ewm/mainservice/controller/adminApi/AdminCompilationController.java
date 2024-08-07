package ru.practicum.ewm.mainservice.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.mainservice.dto.compilation.CompilationDto;
import ru.practicum.ewm.mainservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.mainservice.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.mainservice.service.CompilationService;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {
    private final CompilationService compilationServiceImpl;

    @PostMapping
    public ResponseEntity<CompilationDto> add(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.debug("Обработка запроса POST/admin/compilations");
        CompilationDto compilation = compilationServiceImpl.add(newCompilationDto);
        log.debug("Создана подборка: {}", compilation);
        return new ResponseEntity<>(compilation, HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> update(@PathVariable long compId,
                                                 @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.debug("Обработка запроса PATCH/admin/compilations/" + compId);
        CompilationDto compilation = compilationServiceImpl.update(compId, updateCompilationRequest);
        log.debug("Изменена подборка: {}, compId={}", compilation, compId);
        return new ResponseEntity<>(compilation, HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> delete(@PathVariable long compId) {
        log.debug("Обработка запроса DELETE/admin/compilations/" + compId);
        compilationServiceImpl.delete(compId);
        log.debug("Подборка удалена: {}", compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
