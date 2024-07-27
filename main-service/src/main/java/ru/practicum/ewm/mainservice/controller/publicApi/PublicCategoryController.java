package ru.practicum.ewm.mainservice.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.mainservice.dto.category.CategoryDto;
import ru.practicum.ewm.mainservice.service.publicApi.PublicCategoriesService;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCategoryController {
    private final PublicCategoriesService publicCategoriesServiceImpl;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll(@RequestParam(defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/categories");
        List<CategoryDto> categories = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        categories = publicCategoriesServiceImpl.getAll(pageable);
        log.debug("Получен список с размером: {}", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> get(@PathVariable long catId) {
        log.debug("Обработка запроса GET/categories/" + catId);
        CategoryDto category = publicCategoriesServiceImpl.get(catId);
        log.debug("Получена категория: {}", category);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
}
