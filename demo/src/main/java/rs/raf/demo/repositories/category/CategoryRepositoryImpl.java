package rs.raf.demo.repositories.category;

import rs.raf.demo.entities.Category;
import rs.raf.demo.entities.dto.EventDto;
import rs.raf.demo.repositories.MySqlAbstractRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepositoryImpl extends MySqlAbstractRepository implements CategoryRepository {
    @Override
    public String getCategoryRepositoryById(int id) {
        String category = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            // koristi PreparedStatement da izbegneš SQL injection
            String sql = "SELECT name FROM categories WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                category = resultSet.getString("name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(statement);
            this.closeConnection(connection);
        }

        return category;
    }

    @Override
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT name FROM categories");

            // Dodavanje naziva kategorija u listu
            while (resultSet.next()) {
                String categoryName = resultSet.getString("name");
                categories.add(categoryName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(statement);
            this.closeConnection(connection);
        }

        return categories;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM categories");

            // Dodavanje naziva kategorija u listu
            while (resultSet.next()) {
                String categoryName = resultSet.getString("name");
                String categoryDescription = resultSet.getString("description");
                int categoryId = resultSet.getInt("id");
                categories.add(new Category(categoryId, categoryName, categoryDescription));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(statement);
            this.closeConnection(connection);
        }

        return categories;
    }

    @Override
    public Category addCategory(Category category) {
        Connection connection = null;
        PreparedStatement psCheck = null;
        PreparedStatement psInsert = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            // Provera da li kategorija sa istim nazivom već postoji
            String sqlCheck = "SELECT id FROM categories WHERE name = ?";
            psCheck = connection.prepareStatement(sqlCheck);
            psCheck.setString(1, category.getCategoryName());
            rs = psCheck.executeQuery();
            if (rs.next()) {
                throw new RuntimeException("Kategorija sa tim nazivom već postoji!");
            }

            // Ubacivanje nove kategorije
            String sqlInsert = "INSERT INTO categories (name, description) VALUES (?, ?)";
            psInsert = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            psInsert.setString(1, category.getCategoryName());
            psInsert.setString(2, category.getCategoryDescription());
            int affectedRows = psInsert.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Dodavanje kategorije nije uspelo!");
            }

            try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
            }

            return category;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(psCheck);
            this.closeStatement(psInsert);
            this.closeConnection(connection);
        }
    }

    @Override
    public Category updateCategory(Category category) {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = this.newConnection();

            // Update kategorije
            String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getCategoryDescription());
            ps.setInt(3, category.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Izmena kategorije nije uspela!");
            }

            return category;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
    }

    @Override
    public Category deleteCategory(int id) {
        Connection connection = null;
        PreparedStatement psCheck = null;
        PreparedStatement psDelete = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            // Provera da li postoje događaji u kategoriji
            String sqlCheck = "SELECT COUNT(*) AS count FROM events WHERE category_id = ?";
            psCheck = connection.prepareStatement(sqlCheck);
            psCheck.setInt(1, id);
            rs = psCheck.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                throw new RuntimeException("Ne možete obrisati kategoriju koja ima događaje!");
            }

            // Brisanje kategorije
            String sqlDelete = "DELETE FROM categories WHERE id = ?";
            psDelete = connection.prepareStatement(sqlDelete);
            psDelete.setInt(1, id);
            int affectedRows = psDelete.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Brisanje kategorije nije uspelo!");
            }

            return null; // ili možeš vratiti obrisanu kategoriju ako želiš

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(psCheck);
            this.closeStatement(psDelete);
            this.closeConnection(connection);
        }
    }

    @Override
    public int getCategoryIdByName(String name) {
        String sql = "SELECT id FROM categories WHERE name = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);
            System.out.println("Trazimo id kategorije za " + name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println(" -1- " + rs.getInt("id"));
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ako kategorija ne postoji, vraćamo 0 ili možeš baciti exception
        return 0;
    }



}
