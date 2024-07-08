package ru.practicum.ewm.mainservice.service.adminApi;

import ru.practicum.ewm.mainservice.dto.category.CategoryDto;
import ru.practicum.ewm.mainservice.dto.category.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto);

    void delete(long categoryId);

    CategoryDto update(CategoryDto categoryDto);

}
