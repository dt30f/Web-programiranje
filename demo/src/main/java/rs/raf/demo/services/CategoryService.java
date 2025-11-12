package rs.raf.demo.services;

import rs.raf.demo.entities.Category;
import rs.raf.demo.repositories.category.CategoryRepository;

import javax.inject.Inject;
import java.util.List;

public class CategoryService {
    @Inject
    public CategoryRepository categoryRepository;

    public String getCategory(int categoryId) {return categoryRepository.getCategoryRepositoryById(categoryId);}
    public List<String> getAllCategories() {return categoryRepository.getCategories();}
    public List<Category> getAllCategoriesForClient(){return categoryRepository.getAllCategories();}
    public Category addCategory(Category category) {return categoryRepository.addCategory(category);}
    public Category updateCategory(Category category) {return categoryRepository.updateCategory(category);}
    public void deleteCategory(int id){categoryRepository.deleteCategory(id);}
}
