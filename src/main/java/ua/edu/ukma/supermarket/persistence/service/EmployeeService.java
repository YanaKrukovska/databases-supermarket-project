package ua.edu.ukma.supermarket.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.Employee;
import ua.edu.ukma.supermarket.persistence.model.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class EmployeeService {

    private final Connection connection;

    @Autowired
    public EmployeeService(Connection connection) {
        this.connection = connection;
    }


    public Response<Employee> createEmployee(Employee employee) {

        List<String> employeeErrors = validateEmployee(employee);
        if (!employeeErrors.isEmpty()) {
            return new Response<>(null, employeeErrors);
        }

        PreparedStatement statement;
        try {
            String query = "INSERT INTO employee (id_employee, empl_surname, empl_name, empl_patronymic, " +
                    "empl_role, salary, date_of_birth, date_of_start, phone_number, city, street, zip_code) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            statement = connection.prepareStatement(query);
            statement.setString(1, employee.getEmployeeId());
            statement.setString(2, employee.getSurname());
            statement.setString(3, employee.getName());
            statement.setString(4, employee.getPatronymic());
            statement.setString(5, employee.getRole());
            statement.setDouble(6, employee.getSalary());
            statement.setDate(7, new java.sql.Date(employee.getBirthDate().getTime()));
            statement.setDate(8, new java.sql.Date(employee.getStartDate().getTime()));
            statement.setString(9, employee.getPhoneNumber());
            statement.setString(10, employee.getCity());
            statement.setString(11, employee.getStreet());
            statement.setString(12, employee.getZipCode());
            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(employee, Collections.singletonList("Failed to save"));
            }

            return new Response<>(findEmployeeById(employee.getEmployeeId()).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Employee> updateCategory(Employee employee) {

        List<String> employeeErrors = validateEmployee(employee);
        if (!employeeErrors.isEmpty()) {
            return new Response<>(null, employeeErrors);
        }

        if (findEmployeeById(employee.getEmployeeId()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't edit nonexistent employee"));
        }

        PreparedStatement statement;
        try {
            String query = "UPDATE employee SET empl_surname = ?, empl_name = ?, empl_patronymic = ?, empl_role = ?, salary = ?, date_of_birth = ?, date_of_start = ?, phone_number = ?, city = ?, street = ?, zip_code = ? WHERE id_employee = ?";

            statement = connection.prepareStatement(query);
            statement.setString(1, employee.getSurname());
            statement.setString(2, employee.getName());
            statement.setString(3, employee.getPatronymic());
            statement.setString(4, employee.getRole());
            statement.setDouble(5, employee.getSalary());
            statement.setDate(6, new java.sql.Date(employee.getBirthDate().getTime()));
            statement.setDate(7, new java.sql.Date(employee.getStartDate().getTime()));
            statement.setString(8, employee.getPhoneNumber());
            statement.setString(9, employee.getCity());
            statement.setString(10, employee.getStreet());
            statement.setString(11, employee.getZipCode());
            statement.setString(12, employee.getEmployeeId());
            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(null, Collections.singletonList("Failed to update"));
            }

            return new Response<>(findEmployeeById(employee.getEmployeeId()).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Employee> deleteEmployee(String employeeId) {

        if (employeeId.isBlank()) {
            return new Response<>(null, Collections.singletonList("Id can't be null"));
        }

        if (findEmployeeById(employeeId).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't delete nonexistent employee"));
        }

        PreparedStatement statement;
        try {
            String query = "DELETE FROM employee WHERE id_employee = ?";

            statement = connection.prepareStatement(query);
            statement.setString(1, employeeId);
            statement.execute();

            return new Response<>(null, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Employee>> getCashiersSortedBySurname() {

        PreparedStatement statement;
        try {
            String query = "SELECT * FROM EMPLOYEE WHERE empl_role='Cashier' ORDER BY empl_surname ASC";

            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<Employee> employeeList = new LinkedList<>();

            while (resultSet.next()) {
                String id = resultSet.getString("id_employee");
                String surname = resultSet.getString("empl_surname");
                String name = resultSet.getString("empl_name");
                String patronymic = resultSet.getString("empl_patronymic");
                String role = resultSet.getString("empl_role");
                double salary = resultSet.getDouble("salary");
                Date birthDate = resultSet.getDate("date_of_birth");
                Date startDate = resultSet.getDate("date_of_start");
                String phoneNumber = resultSet.getString("phone_number");
                String city = resultSet.getString("city");
                String street = resultSet.getString("street");
                String zipCode = resultSet.getString("zip_code");

                Employee employee = new Employee(id, surname, name, patronymic, role, salary, birthDate, startDate, phoneNumber, city, street, zipCode);

                employeeList.add(employee);
            }

            return new Response<>(employeeList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Employee>> findPhoneNumberAndAddressBySurname(String surname) {

        if (surname.isBlank()) {
            return new Response<>(null, Collections.singletonList("Surname can't be null"));
        }

        PreparedStatement statement;
        try {
            String query = "SELECT phone_number, city, street, zip_code FROM EMPLOYEE WHERE empl_surname=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, surname);
            ResultSet resultSet = statement.executeQuery();
            List<Employee> employeeList = new LinkedList<>();

            while (resultSet.next()) {
                String phoneNumber = resultSet.getString("phone_number");
                String city = resultSet.getString("city");
                String street = resultSet.getString("street");
                String zipCode = resultSet.getString("zip_code");

                Employee employee = new Employee(null, surname, null, null, null,
                        null, null, null, phoneNumber, city, street, zipCode);

                employeeList.add(employee);
            }
            return new Response<>(employeeList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Employee> findEmployeeById(String id) {

        if (id.isBlank()) {
            return new Response<>(null, Collections.singletonList("Id can't be null"));
        }

        PreparedStatement statement;
        try {
            String query = "SELECT * FROM EMPLOYEE WHERE id_employee=?";

            statement = connection.prepareStatement(query);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            String surname = resultSet.getString("empl_surname");
            String name = resultSet.getString("empl_name");
            String patronymic = resultSet.getString("empl_patronymic");
            String role = resultSet.getString("empl_role");
            double salary = resultSet.getDouble("salary");
            Date birthDate = resultSet.getDate("date_of_birth");
            Date startDate = resultSet.getDate("date_of_start");
            String phoneNumber = resultSet.getString("phone_number");
            String city = resultSet.getString("city");
            String street = resultSet.getString("street");
            String zipCode = resultSet.getString("zip_code");

            return new Response<>(new Employee(id, surname, name, patronymic, role, salary, birthDate, startDate, phoneNumber, city, street, zipCode), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    private List<String> validateEmployee(Employee employee) {
        List<String> errors = new LinkedList<>();

        if (employee.getEmployeeId().isBlank()) {
            errors.add("Id can't be empty");
        }
        if (employee.getSurname().isBlank()) {
            errors.add("Surname can't be empty");
        }
        if (employee.getName().isBlank()) {
            errors.add("Name can't be empty");
        }
        if (employee.getPatronymic().isBlank()) {
            errors.add("Patronymic can't be empty");
        }
        if (employee.getRole().isBlank()) {
            errors.add("Role can't be empty");
        }
        if (employee.getSalary() == null || employee.getSalary() <= 0) {
            errors.add("Salary must be more than 0");
        }
        if (employee.getPhoneNumber().isBlank()) {
            errors.add("Phone number can't be empty");
        }
        if (employee.getCity().isBlank()) {
            errors.add("City can't be empty");
        }
        if (employee.getStreet().isBlank()) {
            errors.add("Street can't be empty");
        }
        if (employee.getZipCode().isBlank()) {
            errors.add("Zip code can't be empty");
        }
        if (employee.getBirthDate() == null) {
            errors.add("Birth date can't be empty");
        }
        if (employee.getStartDate() == null) {
            errors.add("Start date can't be empty");
        }
        return errors;
    }
}
