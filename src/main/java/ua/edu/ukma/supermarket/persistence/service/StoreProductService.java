package ua.edu.ukma.supermarket.persistence.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.*;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreProductService {

    private final Connection connection;

    private final ProductService productService;

    @Autowired
    public StoreProductService(Connection connection, ProductService productService) {
        this.connection = connection;
        this.productService = productService;
    }

    public Response<StoreProduct> findStoreProductByUpc(String upc) {
        String query = "SELECT * FROM store_product WHERE upc=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, upc);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Response<>(extractStoreProduct(resultSet), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<StoreProduct> createStoreProduct(StoreProduct storeProduct) {

        List<String> storeProductsErrors = validateStoreProduct(storeProduct);
        if (!storeProductsErrors.isEmpty()) {
            return new Response<>(null, storeProductsErrors);
        }

        if (findStoreProductByUpc(storeProduct.getUpc()).getObject() != null) {
            return new Response<>(null, Collections.singletonList("Store product with such upc already exists"));
        }

        if (productService.findProductById(storeProduct.getProductId()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't add store product with nonexistent product"));
        }

        String query = "INSERT INTO store_product (upc, selling_price, products_number, promotional_product, upc_prom, id_product)" +
                " VALUES (?,?,?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, storeProduct.getUpc());
            statement.setDouble(2, storeProduct.getSellingPrice());
            statement.setInt(3, storeProduct.getProductsNumber());
            statement.setBoolean(4, storeProduct.isPromotionalProduct());
            statement.setString(5, storeProduct.getUpcPromo());
            statement.setInt(6, storeProduct.getProductId());

            int rows = statement.executeUpdate();
            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to save"));
            }

            return new Response<>(findStoreProductByUpc(storeProduct.getUpc()).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<StoreProduct> updateStoreProduct(StoreProduct product) {

        List<String> productErrors = validateStoreProduct(product);
        if (!productErrors.isEmpty()) {
            return new Response<>(null, productErrors);
        }

        if (findStoreProductByUpc(product.getUpc()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't edit nonexistent product"));
        }

        if (productService.findProductById(product.getProductId()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't change product id to nonexistent"));
        }

        String query = "UPDATE store_product SET selling_price = ?, products_number = ?, promotional_product = ?, upc_prom = ?, " +
                "id_product = ? WHERE upc = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, product.getSellingPrice());
            statement.setInt(2, product.getProductsNumber());
            statement.setBoolean(3, product.isPromotionalProduct());
            statement.setString(4, product.getUpcPromo());
            statement.setInt(5, product.getProductId());
            statement.setString(6, product.getUpc());

            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to update"));
            }

            return new Response<>(findStoreProductByUpc(product.getUpc()).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<StoreProduct> deleteStoreProduct(String productUPC) {

        if (findStoreProductByUpc(productUPC).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't delete nonexistent product"));
        }

        String query = "DELETE FROM store_product WHERE upc = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, productUPC);
            statement.execute();
            return new Response<>(null, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
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

    public Response<BasicStoredProduct> getBasicStoredProductInfo(String upc) {

        if (upc.isBlank()) {
            return new Response<>(null, Collections.singletonList("Category can't be null"));
        }

        PreparedStatement statement;
        try {
            String query = "SELECT selling_price,products_number FROM store_product WHERE upc=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, upc);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Response<>(new BasicStoredProduct(resultSet.getDouble("selling_price"), resultSet.getInt("products_number")), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<AdvancedStoreProduct>> getAdvancedStoredProductInfo(String upc) {

        if (upc.isBlank()) {
            return new Response<>(null, Collections.singletonList("Category can't be null"));
        }
        PreparedStatement statement;
        try {
            String query =
                    "SELECT product_name,characteristics,selling_price,products_number " +
                            "FROM store_product,product " +
                            "WHERE store_product.upc=? AND product.id_product=store_product.id_product";
            statement = connection.prepareStatement(query);
            statement.setString(1, upc);
            ResultSet resultSet = statement.executeQuery();
            List<AdvancedStoreProduct> storeProducts = new LinkedList<>();
            while (resultSet.next()) {
                storeProducts.add(new AdvancedStoreProduct(resultSet.getString("product_name"), resultSet.getString("characteristics"), resultSet.getDouble("selling_price"), resultSet.getInt("products_number")));
            }

            return new Response<>(storeProducts, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<StoreProduct>> findAll() {
        String query =
                "SELECT * FROM store_product";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            List<StoreProduct> storeProducts = new LinkedList<>();
            while (resultSet.next()) storeProducts.add(extractStoreProduct(resultSet));
            return new Response<>(storeProducts, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<StoreProduct>> getAllStoreProductsFromProduct(int productId) {
        String query = "SELECT * FROM store_product WHERE id_product = ? ";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();

            List<StoreProduct> productList = new LinkedList<>();
            while (resultSet.next()) {
                productList.add(extractStoreProduct(resultSet));
            }

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<StoreProductWithName>> findAllWithName() {
        String query = "SELECT p.product_name, sp.* FROM Store_product sp " +
                "INNER JOIN Product p ON p.id_product = sp.id_product ";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            List<StoreProductWithName> productList = new LinkedList<>();
            while (resultSet.next()) {
                productList.add(extractStoreProductWithName(resultSet));
            }

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<StoreProductWithName>> findAllPromotionalSortedByAmount(boolean isPromo) {
        String query = "SELECT p.product_name, sp.* FROM Store_product sp " +
                "INNER JOIN Product p ON p.id_product = sp.id_product WHERE promotional_product = ? ORDER BY sp.products_number";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBoolean(1, isPromo);
            ResultSet resultSet = statement.executeQuery();

            List<StoreProductWithName> productList = new LinkedList<>();
            while (resultSet.next()) {
                productList.add(extractStoreProductWithName(resultSet));
            }

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<StoreProductWithName>> findAllSortedByName(boolean isPromo) {
        String query = "SELECT p.product_name, sp.* FROM Store_product sp " +
                "INNER JOIN Product p ON p.id_product = sp.id_product WHERE promotional_product = ? ORDER BY p.product_name";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBoolean(1, isPromo);
            ResultSet resultSet = statement.executeQuery();

            List<StoreProductWithName> productList = new LinkedList<>();
            while (resultSet.next()) {
                productList.add(extractStoreProductWithName(resultSet));
            }

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    private List<String> validateStoreProduct(StoreProduct product) {
        List<String> errors = new LinkedList<>();

        if (product.getUpc() == null || product.getUpc().isBlank()) {
            errors.add("UPC can't be empty");
        }
        if (product.getSellingPrice() < 0) {
            errors.add("Selling price can't be less than 0");
        }
        if (product.getProductsNumber() < 0) {
            errors.add("Amount of products can't be less than 0");
        }
        return errors;
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

    @SneakyThrows
    private StoreProductWithName extractStoreProductWithName(ResultSet resultSet) {
        String upc = resultSet.getString("upc");
        String upcPromo = resultSet.getString("upc_prom");
        int productId = resultSet.getInt("id_product");
        double sellingPrice = resultSet.getDouble("selling_price");
        int productsNumber = resultSet.getInt("products_number");
        boolean isPromotionalProduct = resultSet.getBoolean("promotional_product");
        String productName = resultSet.getString("product_name");

        return new StoreProductWithName(upc, upcPromo, productId, sellingPrice, productsNumber, isPromotionalProduct, productName);
    }


}
