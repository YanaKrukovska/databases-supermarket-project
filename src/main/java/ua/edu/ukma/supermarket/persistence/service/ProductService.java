package ua.edu.ukma.supermarket.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.CustomerCard;
import ua.edu.ukma.supermarket.persistence.model.Product;
import ua.edu.ukma.supermarket.persistence.model.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class ProductService {

    private final Connection connection;

    @Autowired
    public ProductService(Connection connection) {
        this.connection = connection;
    }

    public Response<List<Product>> productsByCustomer(int id) {
        String query = "SELECT * " +
                "FROM product " +
                "WHERE id_product IN ( " +
                "SELECT id_product " +
                "FROM store_product " +
                "WHERE upc IN ( " +
                "SELECT upc " +
                "FROM sale " +
                "WHERE check_number IN ( " +
                "SELECT check_number " +
                "FROM receipt " +
                "WHERE card_number = ?" +
                ")" +
                ")" +
                ")";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Product> productList = new LinkedList<>();

            while (resultSet.next()) productList.add(productFromResultSet(resultSet));

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Product>> productsBoughtWith(int id) {
        String query =
                "SELECT * " +
                        "FROM product " +
                        "WHERE id_product<>? AND id_product IN (" +
                        "SELECT SP.id_product " +
                        "FROM sale AS S " +
                        "INNER JOIN receipt AS R ON S.check_number = R.check_number " +
                        "INNER JOIN store_product AS SP ON SP.upc = S.upc " +
                        "WHERE S.check_number IN ( " +
                        "SELECT S1.check_number " +
                        "FROM sale AS S1 " +
                        "INNER JOIN receipt AS R1 ON S1.check_number = R1.check_number " +
                        "INNER JOIN store_product AS SP1 ON SP1.upc = S1.upc " +
                        "WHERE SP1.id_product = ? " +
                        ")" +
                        ")";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setInt(2, id);
            ResultSet resultSet = statement.executeQuery();
            List<Product> productList = new LinkedList<>();

            while (resultSet.next()) productList.add(productFromResultSet(resultSet));

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Product>> mostMoneyEarned(int n) {
        String query =
                "SELECT R1.id_product, R1.product_name,R1.characteristics,R1.category_number " +
                        "FROM product AS R1 " +
                        "INNER JOIN ( " +
                        "SELECT SP.id_product, SUM(S.product_number*S.selling_price) AS sum " +
                        "FROM store_product AS SP " +
                        "INNER JOIN sale AS S ON S.upc=SP.upc " +
                        "GROUP BY SP.id_product " +
                        ") AS R2 ON R1.id_product = R2.id_product " +
                        "ORDER BY R2.sum DESC " +
                        "LIMIT ? ";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, n);
            ResultSet resultSet = statement.executeQuery();
            List<Product> productList = new LinkedList<>();

            while (resultSet.next()) productList.add(productFromResultSet(resultSet));

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public List<Product> findAll() {
        String query =
                "SELECT * FROM product";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            List<Product> productList = new LinkedList<>();
            while (resultSet.next()) productList.add(productFromResultSet(resultSet));
            return productList;
        } catch (SQLException e) {
            return new LinkedList<>();
        }
    }

    private Product productFromResultSet(ResultSet resultSet) throws SQLException {
        int productId = resultSet.getInt("id_product");
        String productName = resultSet.getString("product_name");
        String characteristics = resultSet.getString("characteristics");
        int categoryNumber = resultSet.getInt("category_number");
        Product product = new Product(productId, productName, characteristics, categoryNumber);
        return product;
    }


}
