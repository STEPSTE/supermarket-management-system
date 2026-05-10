package com.boutique.service;

import com.boutique.dto.request.CreateCategoryRequest;
import com.boutique.exception.ResourceNotFoundException;
import com.boutique.model.Category;
import com.boutique.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(CreateCategoryRequest request) {
        Category category = new Category(request.name());
        return categoryRepository.save(category);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée: " + id));
    }

    public Category update(Long id, CreateCategoryRequest request) {
        Category category = findById(id);
        category.setName(request.name());
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Catégorie non trouvée: " + id);
        }
        categoryRepository.deleteById(id);
    }
}