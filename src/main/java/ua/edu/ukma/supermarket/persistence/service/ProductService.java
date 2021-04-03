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
                            "WHERE card_number = 1" +
                            ")" +
                        ")" +
                    ")";
        return getResponse(query);
    }

    private Response<List<Product>> getResponse(String query){
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<Product> productList = new LinkedList<>();

            while (resultSet.next()) productList.add(productFromResultSet(resultSet));

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    private Product productFromResultSet(ResultSet resultSet) throws SQLException {
        int productId = resultSet.getInt("id_product");
        String productName = resultSet.getString("product_name");
        String characteristics = resultSet.getString("characteristics");
        int categoryNumber = resultSet.getInt("category_number");
        Product product = new Product(productId,productName,characteristics,categoryNumber);
        return product;
    }


}
