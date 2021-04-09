package ua.edu.ukma.supermarket.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.CustomerCard;
import ua.edu.ukma.supermarket.persistence.model.Response;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class CustomerService {

    private static final String PHONE_NUMBER_REGEX = "^\\+[\\d]{12}$";
    private final Connection connection;

    @Autowired
    public CustomerService(Connection connection) {
        this.connection = connection;
    }

    public Response<CustomerCard> findCustomerCardById(int id) {
        String query = "SELECT * FROM customer_card WHERE card_number=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Response<>(customerCardFromResultSet(resultSet), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<CustomerCard> createCustomerCard(CustomerCard customerCard) {

        List<String> customerCardErrors = validateCustomerCard(customerCard);
        if (!customerCardErrors.isEmpty()) {
            return new Response<>(null, customerCardErrors);
        }

        String query = "INSERT INTO customer_card (card_number, card_surname, card_name, card_patronymic," +
                " phone_number, city, street, zip_code, percent) VALUES (?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setNull(1, Types.NULL);
            statement.setString(2, customerCard.getCustomerSurname());
            statement.setString(3, customerCard.getCustomerName());
            statement.setString(4, customerCard.getCustomerPatronymic());
            statement.setString(5, customerCard.getPhone());
            statement.setString(6, customerCard.getCity());
            statement.setString(7, customerCard.getStreet());
            statement.setString(8, customerCard.getZipcode());
            statement.setInt(9, customerCard.getPercent());

            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to save"));
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int newCustomerCardId = generatedKeys.getInt("card_number");

            return new Response<>(findCustomerCardById(newCustomerCardId).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<CustomerCard> updateCustomerCard(CustomerCard customerCard) {

        List<String> customerErrors = validateCustomerCard(customerCard);
        if (!customerErrors.isEmpty()) {
            return new Response<>(null, customerErrors);
        }

        if (findCustomerCardById(customerCard.getCardNumber()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't edit nonexistent product"));
        }

        String query = "UPDATE customer_card SET card_surname = ?, card_name = ?, card_patronymic = ?, " +
                "phone_number = ?, city = ?, street = ?, zip_code = ?, percent = ? WHERE card_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, customerCard.getCustomerSurname());
            statement.setString(2, customerCard.getCustomerName());
            statement.setString(3, customerCard.getCustomerPatronymic());
            statement.setString(4, customerCard.getPhone());
            statement.setString(5, customerCard.getCity());
            statement.setString(6, customerCard.getStreet());
            statement.setString(7, customerCard.getZipcode());
            statement.setInt(8, customerCard.getPercent());
            statement.setInt(9, customerCard.getCardNumber());

            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to update"));
            }

            return new Response<>(findCustomerCardById(customerCard.getCardNumber()).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<CustomerCard> deleteCustomerCard(int customerCardId) {

        if (findCustomerCardById(customerCardId).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't delete nonexistent customer card"));
        }

        String query = "DELETE FROM customer_card WHERE card_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, customerCardId);
            statement.execute();

            return new Response<>(null, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<CustomerCard>> findMostValuableCustomer() {
        String query = "SELECT * " +
                "FROM customer_card " +
                "WHERE card_number IN ( " +
                "SELECT card_number " +
                "FROM receipt " +
                "GROUP BY card_number " +
                "HAVING SUM(sum_total) = ( " +
                "SELECT MAX(stats.total) " +
                "FROM ( " +
                "SELECT SUM(sum_total) AS total " +
                "FROM receipt " +
                "GROUP BY card_number) AS stats " +
                ")" +
                ")";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<CustomerCard> cardList = new LinkedList<>();

            while (resultSet.next()) cardList.add(customerCardFromResultSet(resultSet));

            return new Response<>(cardList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<CustomerCard>> customersThatByOnlyThooseProductsAs(int id) {
        String query =
                "SELECT * " +
                        "FROM customer_card AS CustomerCard " +
                        "WHERE card_number<>? AND card_number NOT IN ( " +
                        "SELECT card_number " +
                        "FROM sale AS A " +
                        "INNER JOIN receipt AS B ON A.check_number = B.check_number " +
                        "INNER JOIN store_product AS C ON C.upc = A.upc " +
                        "WHERE C.id_product NOT IN ( " +
                        "SELECT id_product " +
                        "FROM sale AS AA " +
                        "INNER JOIN receipt AS BB ON AA.check_number = BB.check_number " +
                        "INNER JOIN store_product AS CC ON CC.upc = AA.upc " +
                        "WHERE BB.card_number = ? " +
                        ")" +
                        ")";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setInt(2, id);
            ResultSet resultSet = statement.executeQuery();

            List<CustomerCard> cardList = new LinkedList<>();

            while (resultSet.next()) cardList.add(customerCardFromResultSet(resultSet));

            return new Response<>(cardList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<CustomerCard>> getTheSameDaysAsCustomer(int cardId) {
        String query =
                "SELECT * FROM customer_card AS CustomerCard WHERE card_number <> ? AND card_number NOT IN (" +
                        " SELECT card_number FROM Receipt WHERE print_date NOT IN(" +
                        "SELECT DISTINCT print_date FROM Receipt WHERE card_number = ? ))";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, cardId);
            statement.setInt(2, cardId);
            ResultSet resultSet = statement.executeQuery();

            List<CustomerCard> customerCards = new LinkedList<>();

            while (resultSet.next()) {
                customerCards.add(customerCardFromResultSet(resultSet));
            }

            return new Response<>(customerCards, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<CustomerCard>> sameProductsAs(int cardId) {
        String query =
                "SELECT * " +
                        "FROM customer_card AS CC1 " +
                        "WHERE card_number<>? AND NOT EXISTS ( " +
                        "SELECT * " +
                        "FROM sale AS S " +
                        "INNER JOIN receipt AS R ON R.check_number=S.check_number " +
                        "INNER JOIN store_product AS SP ON S.upc=SP.upc " +
                        "WHERE R.card_number = ? " +
                        "AND S.upc NOT IN ( " +
                        "SELECT SP1.upc " +
                        "FROM sale AS S1 " +
                        "INNER JOIN receipt AS R1 ON R1.check_number=S1.check_number " +
                        "INNER JOIN store_product AS SP1 ON S1.upc=SP1.upc " +
                        "WHERE R1.card_number = CC1.card_number " +
                        ")" +
                        ")";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, cardId);
            statement.setInt(2, cardId);
            ResultSet resultSet = statement.executeQuery();

            List<CustomerCard> customerCards = new LinkedList<>();

            while (resultSet.next()) {
                customerCards.add(customerCardFromResultSet(resultSet));
            }

            return new Response<>(customerCards, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public List<CustomerCard> findAll() {
        String query =
                "SELECT * FROM customer_card";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            List<CustomerCard> customerCardList = new LinkedList<>();
            while (resultSet.next()) {
                customerCardList.add(customerCardFromResultSet(resultSet));
            }
            return customerCardList;
        } catch (SQLException e) {
            return new LinkedList<>();
        }
    }

    public Response<List<CustomerCard>> findCustomersCardBySurname(String surname) {
        String query = "SELECT * FROM customer_card WHERE card_surname = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, surname);
            ResultSet resultSet = statement.executeQuery();
            List<CustomerCard> customerCardList = new LinkedList<>();
            while (resultSet.next()) {
                customerCardList.add(customerCardFromResultSet(resultSet));
            }
            return new Response<>(customerCardList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<CustomerCard>> findCustomersWithCertainPercent(int percent) {
        String query = "SELECT * FROM customer_card WHERE percent = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, percent);
            ResultSet resultSet = statement.executeQuery();
            List<CustomerCard> customerCardList = new LinkedList<>();
            while (resultSet.next()) {
                customerCardList.add(customerCardFromResultSet(resultSet));
            }
            return new Response<>(customerCardList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    private CustomerCard customerCardFromResultSet(ResultSet resultSet) throws SQLException {
        int cardNumber = resultSet.getInt("card_number");
        String cardSur = resultSet.getString("card_surname");
        String cardName = resultSet.getString("card_name");
        String cardPatr = resultSet.getString("card_patronymic");
        String phone = resultSet.getString("phone_number");
        String city = resultSet.getString("city");
        String street = resultSet.getString("street");
        String zipCode = resultSet.getString("zip_code");
        int percent = resultSet.getInt("percent");
        return new CustomerCard(cardNumber, cardSur, cardName, cardPatr, phone, city, street, zipCode, percent);
    }

    private List<String> validateCustomerCard(CustomerCard customerCard) {
        List<String> errors = new LinkedList<>();

        if (customerCard.getCustomerName() == null || customerCard.getCustomerName().isBlank()) {
            errors.add("Customer's name can't be empty");
        }
        if (customerCard.getCustomerSurname() == null || customerCard.getCustomerSurname().isBlank()) {
            errors.add("Customer's surname can't be empty");
        }

        if (customerCard.getCustomerPatronymic() == null || customerCard.getCustomerPatronymic().isBlank()) {
            errors.add("Customer's patronymic can't be empty");
        }
        if (customerCard.getPhone() == null || customerCard.getPhone().isBlank()) {
            errors.add("Customer's phone number can't be empty");
        } else if (!customerCard.getPhone().matches(PHONE_NUMBER_REGEX)) {
            errors.add("Wrong phone format");
        }
        return errors;
    }


}
