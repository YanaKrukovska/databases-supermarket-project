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
        return getResponse(query);
    }

    private Response<List<CustomerCard>> getResponse(String query){
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
        CustomerCard card = new CustomerCard(cardNumber,cardSur, cardName, cardPatr,phone,city,street,zip_code,percent);
        return card;
    }
}
