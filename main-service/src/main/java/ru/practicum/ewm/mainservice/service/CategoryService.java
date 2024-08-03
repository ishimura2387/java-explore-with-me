package ru.practicum.ewm.mainservice.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.category.CategoryDto;
import ru.practicum.ewm.mainservice.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto);

    void delete(long categoryId);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto get(Long id);
}
