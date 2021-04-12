package ua.edu.ukma.supermarket.persistence.service;

import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.Response;
import ua.edu.ukma.supermarket.persistence.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;

@Service
public class RoleService {

    private final Connection connection;

    public RoleService(Connection connection) {
        this.connection = connection;
    }

    public Response<Role> findRoleById(Long id) {

        if (id == null) {
            return new Response<>(null, Collections.singletonList("Id can't be null"));
        }

        String query = "SELECT * FROM role WHERE id_role = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Response<>(new Role(id, resultSet.getString("name")), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

}
