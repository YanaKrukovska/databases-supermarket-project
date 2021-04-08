package ua.edu.ukma.supermarket.persistence.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.Receipt;
import ua.edu.ukma.supermarket.persistence.model.Response;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class ReceiptService {

    private final Connection connection;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    public ReceiptService(Connection connection, EmployeeService employeeService, CustomerService customerService) {
        this.connection = connection;
        this.employeeService = employeeService;
        this.customerService = customerService;
    }

    public Response<Receipt> findReceiptById(Integer id) {

        if (id == null) {
            return new Response<>(null, Collections.singletonList("Id can't be null"));
        }

        String query = "SELECT * FROM receipt WHERE check_number = ?";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Response<>(extractReceipt(resultSet), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Receipt> createReceipt(Receipt receipt) {

        List<String> receiptErrors = validateReceipt(receipt);
        if (!receiptErrors.isEmpty()) {
            return new Response<>(null, receiptErrors);
        }

        if (employeeService.findEmployeeById(receipt.getEmployeeId()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't create a receipt for nonexistent employee"));
        }

        if (customerService.findCustomerCardById(receipt.getCardNumber()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't create a receipt for nonexistent card"));
        }

        String query = "INSERT INTO receipt (check_number, print_date, sum_total, vat, id_employee, card_number) " +
                "VALUES (?,?,?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setNull(1, Types.NULL);
            statement.setDate(2, new Date(receipt.getPrintDate().getTime()));
            statement.setDouble(3, receipt.getSumTotal());
            statement.setDouble(4, receipt.getVat());
            statement.setString(5, receipt.getEmployeeId());
            statement.setInt(6, receipt.getCardNumber());
            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to save"));
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            return new Response<>(findReceiptById(generatedKeys.getInt("check_number")).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Receipt> updateReceipt(Receipt receipt) {
        if (employeeService.findEmployeeById(receipt.getEmployeeId()) == null) {
            return new Response<>(null, Collections.singletonList("Can't update a receipt for nonexistent employee"));
        }

        if (customerService.findCustomerCardById(receipt.getReceiptNumber()) == null) {
            return new Response<>(null, Collections.singletonList("Can't update a receipt for nonexistent card"));
        }

        List<String> receiptErrors = validateReceipt(receipt);
        if (!receiptErrors.isEmpty()) {
            return new Response<>(null, receiptErrors);
        }

        if (findReceiptById(receipt.getReceiptNumber()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't edit nonexistent receipt"));
        }

        String query = "UPDATE receipt SET print_date = ?, sum_total = ?, vat = ?, id_employee = ?, card_number = ? WHERE check_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, new Date(receipt.getPrintDate().getTime()));
            statement.setDouble(2, receipt.getSumTotal());
            statement.setDouble(3, receipt.getVat());
            statement.setString(4, receipt.getEmployeeId());
            statement.setInt(5, receipt.getCardNumber());
            statement.setInt(6, receipt.getReceiptNumber());

            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to update"));
            }

            return new Response<>(findReceiptById(receipt.getReceiptNumber()).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Receipt> deleteReceipt(int receiptId) {

        if (findReceiptById(receiptId).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't delete nonexistent receipt"));
        }

        String query = "DELETE FROM receipt WHERE check_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, receiptId);
            statement.execute();

            return new Response<>(null, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    private List<String> validateReceipt(Receipt receipt) {
        List<String> errors = new LinkedList<>();

        if (receipt.getEmployeeId() == null || receipt.getEmployeeId().isBlank()) {
            errors.add("Employee id can't be empty");
        }
        if (receipt.getPrintDate() == null) {
            errors.add("Print date can't be empty");
        }

        if (receipt.getSumTotal() < 0) {
            errors.add("Total sum can't be less than 0");
        }

        if (receipt.getVat() < 0) {
            errors.add("Vat can't be less than 0");
        }
        return errors;
    }

    @SneakyThrows
    private Receipt extractReceipt(ResultSet resultSet) {
        int id = resultSet.getInt("check_number");
        Date printDate = resultSet.getDate("print_date");
        double sumTotal = resultSet.getDouble("sum_total");
        double vat = resultSet.getDouble("vat");
        String employeeId = resultSet.getString("id_employee");
        Integer cardNumber = resultSet.getInt("card_number");
        return new Receipt(id, employeeId, cardNumber, printDate, sumTotal, vat);
    }
}
