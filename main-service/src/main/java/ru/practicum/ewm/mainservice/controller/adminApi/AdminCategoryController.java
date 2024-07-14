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
import ru.practicum.ewm.mainservice.dto.category.CategoryDto;
import ru.practicum.ewm.mainservice.dto.category.NewCategoryDto;
import ru.practicum.ewm.mainservice.service.adminApi.AdminCategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryServiceImpl;

    @PostMapping
    public ResponseEntity<CategoryDto> add(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.debug("Обработка запроса POST/admin/categories");
        CategoryDto category = adminCategoryServiceImpl.add(newCategoryDto);
        log.debug("Создана категория: {}", category);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> delete(@PathVariable long catId) {
        log.debug("Обработка запроса DELETE/admin/categories/" + catId);
        adminCategoryServiceImpl.delete(catId);
        log.debug("Категория удалена: {}", catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> update(@PathVariable long catId, @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(catId);
        log.debug("Обработка запроса PATCH/admin/categories/" + catId);
        CategoryDto category = adminCategoryServiceImpl.update(categoryDto);
        log.debug("Изменена категория: {}, catId={}", category, catId);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
}
