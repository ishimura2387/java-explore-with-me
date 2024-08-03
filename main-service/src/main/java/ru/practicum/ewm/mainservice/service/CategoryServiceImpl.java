package ru.practicum.ewm.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.dto.category.CategoryDto;
import ru.practicum.ewm.mainservice.dto.category.NewCategoryDto;
import ru.practicum.ewm.mainservice.exception.NotFoundException;
import ru.practicum.ewm.mainservice.mapper.CategoryMapper;
import ru.practicum.ewm.mainservice.model.Category;
import ru.practicum.ewm.mainservice.repository.CategoryRepository;
import ru.practicum.ewm.mainservice.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.toCategory(newCategoryDto);
        return categoryMapper.fromCategory(categoryRepository.save(category));
    }

    public void delete(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки категории на наличие в Storage! " +
                        "Категория не найдена!"));
        if (!eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            throw new DataIntegrityViolationException("Нельзя удалить категорию в которой есть созданные события!");
        } else {
            categoryRepository.deleteById(categoryId);
        }
    }

    public CategoryDto update(CategoryDto categoryDto) {
        long id = categoryDto.getId();
        Category categoryOld = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки категории на наличие в Storage! " +
                        "Категория не найдена!"));
        Category category = categoryMapper.updateCategory(categoryDto, categoryOld);
        return categoryMapper.fromCategory(categoryRepository.save(category));
    }

    public List<CategoryDto> getAll(Pageable pageable) {
        List<CategoryDto> categories = categoryRepository.findAll(pageable).stream().map(category ->
                categoryMapper.fromCategory(category)).collect(Collectors.toList());
        return categories;
    }

    public CategoryDto get(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки категории на наличие в Storage! " +
                        "Категория не найдена!"));
        return categoryMapper.fromCategory(category);
    }
}
