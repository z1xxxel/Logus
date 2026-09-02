package com.logus.tms_backend.controller;

import com.logus.tms_backend.model.CargoCategory;
import com.logus.tms_backend.repository.CargoCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CargoCategoryController {

    private final CargoCategoryRepository categoryRepository;

    /**
     * 🆕 Получить все категории грузов
     */
    @GetMapping
    public ResponseEntity<List<CargoCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    /**
     * 🆕 Получить категорию по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CargoCategory> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена")));
    }

    /**
     * 🆕 Создать новую категорию (только Admin)
     */
    @PostMapping
    public ResponseEntity<CargoCategory> createCategory(@RequestBody CargoCategory category,
                                                        @RequestParam Long adminId) {
        // Проверка админа (упрощенная)
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Категория с таким названием уже существует");
        }
        return new ResponseEntity<>(categoryRepository.save(category), HttpStatus.CREATED);
    }

    /**
     * 🆕 Удалить категорию (только Admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id,
                                               @RequestParam Long adminId) {
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}