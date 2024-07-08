package ru.practicum.ewm.mainservice.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.ewm.mainservice.service.adminApi.AdminCompilationService;


@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {
    private final AdminCompilationService adminCompilationServiceImpl;

    @PostMapping
    public ResponseEntity<CompilationDto> add(@RequestBody NewCompilationDto newCompilationDto) {
        log.debug("Обработка запроса POST/admin/compilations");
        CompilationDto compilation= adminCompilationServiceImpl.add(newCompilationDto);
        log.debug("Создана подборка: {}", compilation);
        return new ResponseEntity<>(compilation, HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> update(@PathVariable long compId, @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.debug("Обработка запроса PATCH/admin/compilations/" + compId);
        CompilationDto compilation = adminCompilationServiceImpl.update(compId, updateCompilationRequest);
        log.debug("Изменена подборка: {}, compId={}", compilation, compId);
        return new ResponseEntity<>(compilation, HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> delete(@PathVariable long compId) {
        log.debug("Обработка запроса DELETE/admin/compilations/" + compId);
        adminCompilationServiceImpl.delete(compId);
        log.debug("Подборка удалена: {}", compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
