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

    private final Connection connection;

    @Autowired
    public CustomerService(Connection connection) {
        this.connection = connection;
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

    private CustomerCard customerCardFromResultSet(ResultSet resultSet) throws SQLException {
        String cardNumber = resultSet.getString("card_number");
        String cardSur = resultSet.getString("card_surname");
        String cardName = resultSet.getString("card_name");
        String cardPatr = resultSet.getString("card_patronymic");
        String phone = resultSet.getString("phone_number");
        String city = resultSet.getString("city");
        String street = resultSet.getString("street");
        String zip_code = resultSet.getString("zip_code");
        int percent = resultSet.getInt("percent");
        CustomerCard card = new CustomerCard(cardNumber, cardSur, cardName, cardPatr, phone, city, street, zip_code, percent);
        return card;
    }


}