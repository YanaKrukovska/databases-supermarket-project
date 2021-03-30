package ua.edu.ukma.supermarket.persistence;

import lombok.SneakyThrows;

import java.sql.*;

public class CategoryRepository {

    private static final String DB_URL = "jdbc:h2:mem:supermarketDB";

    private Connection connection;

    @SneakyThrows
    public CategoryRepository()  {
        this.connection = DriverManager.getConnection(DB_URL, "sa", "");
    }

    public Category findProductById(int id) {
        PreparedStatement statement;
        try {
            String query = "SELECT * FROM category WHERE category_number=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Category(id, resultSet.getString("category_name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
