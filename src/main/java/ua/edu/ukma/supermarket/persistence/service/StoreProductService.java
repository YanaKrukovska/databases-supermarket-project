package ua.edu.ukma.supermarket.persistence.service;

import lombok.SneakyThrows;
import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class StoreProductService {

    private final Connection connection;

    @Autowired
    public StoreProductService(Connection connection) {
        this.connection = connection;
    }

    public Response<List<StoreProduct>> findAllStoreProductsNotInCategory(String category) {

        if (category.isBlank()) {
            return new Response<>(null, Collections.singletonList("Category can't be null"));
        }

        PreparedStatement statement;
        try {
            String query = "SELECT * FROM store_product sp WHERE sp.id_product IN (SELECT id_product FROM Product " +
                    "WHERE category_number NOT IN (SELECT category_number FROM category WHERE category_name = ?))";
            statement = connection.prepareStatement(query);
            statement.setString(1, category);
            ResultSet resultSet = statement.executeQuery();

            List<StoreProduct> storeProducts = new LinkedList<>();
            while (resultSet.next()) {
                storeProducts.add(extractStoreProduct(resultSet));
            }

            return new Response<>(storeProducts, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public List<StoreProduct> findAll() {
        String query =
                "SELECT * FROM store_product";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            List<StoreProduct> storeProductList = new LinkedList<>();
            while (resultSet.next()) storeProductList.add(extractStoreProduct(resultSet));
            return storeProductList;
        } catch (SQLException e) {
            return new LinkedList<>();
        }
    }

    @SneakyThrows
    private StoreProduct extractStoreProduct(ResultSet resultSet) {
        String upc = resultSet.getString("upc");
        String upcPromo = resultSet.getString("upc_prom");
        int productId = resultSet.getInt("id_product");
        double sellingPrice = resultSet.getDouble("selling_price");
        int productsNumber = resultSet.getInt("products_number");
        boolean isPromotionalProduct = resultSet.getBoolean("promotional_product");

        return new StoreProduct(upc, upcPromo, productId, sellingPrice, productsNumber, isPromotionalProduct);
    }

}
