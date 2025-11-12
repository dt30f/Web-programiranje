package rs.raf.demo.repositories.category;

import rs.raf.demo.entities.Category;

import java.util.List;

public interface CategoryRepository {
    public String getCategoryRepositoryById(int id);
    public List<String> getCategories();
    public List<Category> getAllCategories();
    public Category addCategory(Category category);
    public Category updateCategory(Category category);
    public Category deleteCategory(int id);
    public int getCategoryIdByName(String name);
}
