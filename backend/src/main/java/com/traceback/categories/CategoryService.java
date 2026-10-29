package com.traceback.categories;

import com.traceback.common.ConflictException;
import com.traceback.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    public Category createCategory(Category category) {
        String slug = category.getSlug() != null ? category.getSlug().trim().toLowerCase() :
                category.getName().trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");

        if (categoryRepository.existsBySlug(slug)) {
            throw new ConflictException("Category with slug '" + slug + "' already exists");
        }

        category.setSlug(slug);
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());
        return categoryRepository.save(category);
    }

    public Category updateCategory(String id, Category updated) {
        Category category = getCategoryById(id);
        category.setName(updated.getName());
        category.setDescription(updated.getDescription());
        category.setIcon(updated.getIcon());
        category.setActive(updated.isActive());
        category.setUpdatedAt(Instant.now());
        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        Category category = getCategoryById(id);
        category.setActive(false);
        category.setUpdatedAt(Instant.now());
        categoryRepository.save(category);
    }
}
