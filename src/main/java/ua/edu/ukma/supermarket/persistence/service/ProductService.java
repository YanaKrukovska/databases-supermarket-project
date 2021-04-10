package ua.edu.ukma.supermarket.persistence.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.Product;
import ua.edu.ukma.supermarket.persistence.model.Response;

import java.sql.*;
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

    public Response<Product> findProductById(int id) {
        String query = "SELECT * FROM product WHERE id_product=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Response<>(extractProduct(resultSet), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Product> createProduct(Product product) {

        List<String> productErrors = validateProduct(product);
        if (!productErrors.isEmpty()) {
            return new Response<>(null, productErrors);
        }

        String query = "INSERT INTO product (id_product, product_name, characteristics, category_number) VALUES (?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setNull(1, Types.NULL);
            statement.setString(2, product.getProductName());
            statement.setString(3, product.getCharacteristics());
            statement.setInt(4, product.getCategoryNumber());
            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to save"));
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int newProductId = generatedKeys.getInt("id_product");

            return new Response<>(findProductById(newProductId).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Product> updateProduct(Product product) {

        List<String> productErrors = validateProduct(product);
        if (!productErrors.isEmpty()) {
            return new Response<>(null, productErrors);
        }

        if (findProductById(product.getProductId()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't edit nonexistent product"));
        }

        String query = "UPDATE product SET product_name = ?, characteristics = ?, category_number = ? WHERE id_product = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, product.getProductName());
            statement.setString(2, product.getCharacteristics());
            statement.setInt(3, product.getCategoryNumber());
            statement.setInt(4, product.getProductId());

            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to update"));
            }

            return new Response<>(findProductById(product.getProductId()).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }



    public Response<Product> deleteProduct(Integer productId) {

        if (productId == null){
            return new Response<>(null, Collections.singletonList("Receipt id can't be null"));
        }

        if (findProductById(productId).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't delete nonexistent product"));
        }

        String query = "DELETE FROM product WHERE id_product = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, productId);
            statement.execute();

            return new Response<>(null, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Product>> getAllProductsSortedByName() {
        String query = "SELECT * FROM product ORDER BY product_name ASC";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            List<Product> productList = new LinkedList<>();
            while (resultSet.next()) {
                productList.add(extractProduct(resultSet));
            }

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
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
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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

        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
        String query = "SELECT * FROM product";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<Product> productList = new LinkedList<>();
            while (resultSet.next()) productList.add(productFromResultSet(resultSet));
            return productList;
        } catch (SQLException e) {
            return new LinkedList<>();
        }
    }

    public Response<List<Product>> getAllProductsFromCategorySortedByName(int categoryId) {
        String query = "SELECT * FROM product WHERE category_number = ? ORDER BY product_name ASC";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();

            List<Product> productList = new LinkedList<>();
            while (resultSet.next()) {
                productList.add(extractProduct(resultSet));
            }

            return new Response<>(productList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Integer>  getAmountOfSalesForPeriodByProductId(int prodId, java.util.Date startDate, java.util.Date endDate) {
        String query =
                "SELECT SUM(product_number)" +
                "FROM sale AS S " +
                "INNER JOIN receipt AS R ON R.check_number=S.check_number " +
                "INNER JOIN store_product AS SP ON S.upc=SP.upc " +
                "WHERE SP.id_product = ? AND R.print_date BETWEEN ? AND ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, prodId);
            statement.setDate(2, new Date(startDate.getTime()));
            statement.setDate(3, new Date(endDate.getTime()));
            ResultSet resultSet = statement.executeQuery();
            return new Response<>(Integer.valueOf(resultSet.getString(1)), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    private Product productFromResultSet(ResultSet resultSet) throws SQLException {
        int productId = resultSet.getInt("id_product");
        String productName = resultSet.getString("product_name");
        String characteristics = resultSet.getString("characteristics");
        int categoryNumber = resultSet.getInt("category_number");
        return new Product(productId, productName, characteristics, categoryNumber);
    }

    private List<String> validateProduct(Product product) {
        List<String> errors = new LinkedList<>();

        if (product.getProductName() == null || product.getProductName().isBlank()) {
            errors.add("Product name can't be empty");
        }
        if (product.getCharacteristics() == null || product.getCharacteristics().isBlank()) {
            errors.add("Product characteristics can't be empty");
        }
        return errors;
    }

    @SneakyThrows
    private Product extractProduct(ResultSet resultSet) {
        int id = resultSet.getInt("id_product");
        String productName = resultSet.getString("product_name");
        String characteristics = resultSet.getString("characteristics");
        int categoryNumber = resultSet.getInt("category_number");

        return new Product(id, productName, characteristics, categoryNumber);
    }


}
