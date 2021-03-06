package ua.edu.ukma.supermarket.persistence.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.Response;
import ua.edu.ukma.supermarket.persistence.model.Sale;
import ua.edu.ukma.supermarket.persistence.model.StoreProduct;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class SaleService {

    private final Connection connection;
    private final StoreProductService storeProductService;

    public SaleService(Connection connection, StoreProductService storeProductService) {
        this.connection = connection;
        this.storeProductService = storeProductService;
    }

    public Response<Sale> createSale(Sale sale) {

        List<String> saleErrors = validateSale(sale);
        if (!saleErrors.isEmpty()) {
            return new Response<>(null, saleErrors);
        }

        StoreProduct storeProduct = storeProductService.findStoreProductByUpc(sale.getUpc()).getObject();
        if (storeProduct == null) {
            return new Response<>(null, Collections.singletonList("No store product with such UPC"));
        }
        int initialAmount = storeProduct.getProductsNumber();
        if (initialAmount < sale.getProductNumber()) {
            return new Response<>(null, Collections.singletonList("You can't sell more than you have"));
        }

        storeProduct.setProductsNumber(initialAmount - storeProduct.getProductsNumber());
        storeProductService.updateStoreProduct(storeProduct);

        sale.setSellingPrice(storeProduct.getSellingPrice());

        String query = "INSERT INTO sale (upc, check_number, product_number, selling_price) VALUES (?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, sale.getUpc());
            statement.setString(2, sale.getReceiptNumber());
            statement.setInt(3, sale.getProductNumber());
            statement.setDouble(4, sale.getSellingPrice());

            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to save"));
            }

            return new Response<>(sale, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Sale>> findAll() {

        String query = "SELECT * FROM sale";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ResultSet resultSet = statement.executeQuery();
            List<Sale> saleList = new LinkedList<>();
            while (resultSet.next()) {
                saleList.add(extractSale(resultSet));
            }
            return new Response<>(saleList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Double> getReceiptSum(Integer receiptId) {

        if (receiptId == null) {
            return new Response<>(null, Collections.singletonList("Receipt id can't be null"));
        }

        String query = "SELECT SUM(product_number * selling_price) FROM sale WHERE check_number = ?";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, receiptId);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return new Response<>(resultSet.getDouble(1), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    private List<String> validateSale(Sale sale) {
        List<String> errors = new LinkedList<>();

        if (sale.getUpc() == null || sale.getUpc().isBlank()) {
            errors.add("UPC can't be empty");
        }
        if (sale.getReceiptNumber() == null) {
            errors.add("Receipt number can't be empty");
        }
        if (sale.getProductNumber() < 0) {
            errors.add("Amount can't be less than 0");
        }

        return errors;
    }

    @SneakyThrows
    private Sale extractSale(ResultSet resultSet) {
        String upc = resultSet.getString("upc");
        String checkNumber = resultSet.getString("check_number");
        int productNumber = resultSet.getInt("product_number");
        double sellingPrice = resultSet.getDouble("selling_price");
        return new Sale(upc, checkNumber, productNumber, sellingPrice);
    }

}
