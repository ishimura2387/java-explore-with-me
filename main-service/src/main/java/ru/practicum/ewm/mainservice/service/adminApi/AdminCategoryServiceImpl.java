package ru.practicum.ewm.mainservice.service.adminApi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.dto.category.CategoryDto;
import ru.practicum.ewm.mainservice.dto.category.NewCategoryDto;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.CategoryMapper;
import ru.practicum.ewm.mainservice.model.Category;
import ru.practicum.ewm.mainservice.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.toCategory(newCategoryDto);
        return categoryMapper.fromCategory(categoryRepository.save(category));
    }

    public void delete(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки категории на наличие в Storage! " +
                        "Категория не найдена!"));
        categoryRepository.deleteById(categoryId);
    }

    public CategoryDto update(CategoryDto categoryDto) {
        long id = categoryDto.getId();
        Category categoryOld = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пкатегории на наличие в Storage! " +
                        "Категория не найдена!"));
        Category category = categoryMapper.updateCategory(categoryDto, categoryOld);
        return categoryMapper.fromCategory(categoryRepository.save(category));
    }
}
