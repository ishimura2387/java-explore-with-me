package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
