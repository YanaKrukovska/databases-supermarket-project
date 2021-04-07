package ua.edu.ukma.supermarket.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.Category;
import ua.edu.ukma.supermarket.persistence.model.CategoryStatistic;
import ua.edu.ukma.supermarket.persistence.model.Product;
import ua.edu.ukma.supermarket.persistence.model.Response;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class CategoryService {

    private final Connection connection;

    @Autowired
    public CategoryService(Connection connection) {
        this.connection = connection;
    }


    public Response<Category> createCategory(String categoryName) {

        if (categoryName.isBlank()) {
            return new Response<>(null, Collections.singletonList("Category name can't be empty"));
        }

        PreparedStatement statement;
        try {
            String query = "INSERT INTO category (category_number, category_name) VALUES (?,?)";

            statement = connection.prepareStatement(query);
            statement.setNull(1, Types.NULL);
            statement.setString(2, categoryName);
            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to save"));
            }

            return new Response<>(findCategoryByName(categoryName), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Category> updateCategory(int categoryId, String newCategoryName) {

        if (categoryId < 0) {
            return new Response<>(null, Collections.singletonList("Category with such id doesn't exist"));
        }
        if (newCategoryName.isBlank()) {
            return new Response<>(null, Collections.singletonList("Category name can't be empty"));
        }

        PreparedStatement statement;
        try {
            String query = "UPDATE category SET category_name = ? WHERE category_number = ?";

            statement = connection.prepareStatement(query);
            statement.setString(1, newCategoryName);
            statement.setInt(2, categoryId);
            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to update"));
            }

            return new Response<>(findCategoryById(categoryId).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Category> deleteCategory(int categoryId) {

        if (categoryId < 0) {
            return new Response<>(null, Collections.singletonList("Category with such id doesn't exist"));
        }

        PreparedStatement statement;
        try {
            String query = "DELETE FROM category WHERE category_number = ?";

            statement = connection.prepareStatement(query);
            statement.setInt(1, categoryId);
            statement.execute();

            return new Response<>(null, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public List<Category> findAll() {
        String query =
                "SELECT * FROM category";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            List<Category> categoryList = new LinkedList<>();
            while (resultSet.next()) categoryList.add(categoryFromResultSet(resultSet));
            return categoryList;
        } catch (SQLException e) {
            return new LinkedList<>();
        }
    }

    private Category categoryFromResultSet(ResultSet resultSet) throws SQLException {
        int number = resultSet.getInt("category_number");
        String name = resultSet.getString("category_name");

        Category category = new Category(number, name);
        return category;
    }

    public Category findCategoryByName(String categoryName) {
        PreparedStatement statement;
        try {
            String query = "SELECT * FROM category WHERE category_name=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, categoryName);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Category(resultSet.getInt("category_number"), resultSet.getString("category_name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response<Category> findCategoryById(int id) {
        PreparedStatement statement;
        try {
            String query = "SELECT * FROM category WHERE category_number=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Response<>(new Category(id, resultSet.getString("category_name")), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Category>> getCategoriesSortedByName() {


        PreparedStatement statement;
        try {
            String query = "SELECT * FROM category ORDER BY category_name ASC";

            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<Category> categoryList = new LinkedList<>();

            while (resultSet.next()) {
                int number = resultSet.getInt("category_number");
                String name = resultSet.getString("category_name");

                Category category = new Category(number, name);

                categoryList.add(category);
            }

            return new Response<>(categoryList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<CategoryStatistic>> popularity() {
        String query =
                "SELECT C.category_number,C.category_name, SUM(S.product_number) AS sum " +
                        "FROM store_product AS SP " +
                        "INNER JOIN sale AS S ON S.upc=SP.upc " +
                        "INNER JOIN product AS P ON P.id_product=SP.id_product " +
                        "INNER JOIN category AS C ON C.category_number=P.category_number " +
                        "GROUP BY C.category_number " +
                        "ORDER BY sum DESC";;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            List<CategoryStatistic> categoryList = new LinkedList<>();

            while (resultSet.next()) categoryList.add(new CategoryStatistic(resultSet.getInt("category_number"),resultSet.getString("category_name"),resultSet.getInt("sum")));

            return new Response<>(categoryList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }
}
