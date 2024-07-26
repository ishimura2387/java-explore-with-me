package ru.practicum.ewm.mainservice.service.publicApi;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoriesService {
    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto get(Long id);
}
