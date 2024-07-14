package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.mainservice.dto.compilation.CompilationDto;
import ru.practicum.ewm.mainservice.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findALlByPinned(Boolean pinned, Pageable pageable);

}
