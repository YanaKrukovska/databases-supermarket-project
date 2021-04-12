package ua.edu.ukma.supermarket.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.edu.ukma.supermarket.persistence.model.Employee;
import ua.edu.ukma.supermarket.persistence.model.EmployeeStatistic;
import ua.edu.ukma.supermarket.persistence.model.Response;
import ua.edu.ukma.supermarket.persistence.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class EmployeeService implements UserDetailsService {

    private final Connection connection;
    private final RoleService roleService;
    private final PasswordService passwordService;

    @Autowired
    public EmployeeService(Connection connection, RoleService roleService, PasswordService passwordService) {
        this.connection = connection;
        this.roleService = roleService;
        this.passwordService = passwordService;
    }


    public Response<Employee> createEmployee(Employee employee) {

        List<String> employeeErrors = validateEmployee(employee);
        if (!employeeErrors.isEmpty()) {
            return new Response<>(null, employeeErrors);
        }

        employee.setPassword(passwordService.encodePassword(employee.getPassword()));

        if (employee.getRole().equals("Manager")) {
            employee.setAuthority(new Role(1L, "ROLE_MANAGER"));
        } else {
            employee.setAuthority(new Role(2L, "ROLE_CASHIER"));
        }

        String query = "INSERT INTO employee (id_employee, empl_surname, empl_name, empl_patronymic, " +
                "empl_role, salary, date_of_birth, date_of_start, phone_number, city, street, zip_code, username, " +
                "password, id_role) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

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
            statement.setString(13, employee.getUsername());
            statement.setString(14, employee.getPassword());
            statement.setLong(15, employee.getAuthority().getId());
            int rows = statement.executeUpdate();

            if (rows == 0) {
                return new Response<>(employee, Collections.singletonList("Failed to save"));
            }

            return new Response<>(findEmployeeById(employee.getEmployeeId()).getObject(), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Employee>> findAll() {
        String query = "SELECT * FROM employee";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<Employee> employeeList = new LinkedList<>();
            while (resultSet.next()) employeeList.add(employeeFromResultSet(resultSet));
            return new Response<>(employeeList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Employee> updateEmployee(Employee employee) {

        List<String> employeeErrors = validateEmployee(employee);
        if (!employeeErrors.isEmpty()) {
            return new Response<>(null, employeeErrors);
        }

        if (findEmployeeById(employee.getEmployeeId()).getObject() == null) {
            return new Response<>(null, Collections.singletonList("Can't edit nonexistent employee"));
        }

        String query = "UPDATE employee SET empl_surname = ?, empl_name = ?, empl_patronymic = ?, empl_role = ?, salary = ?, date_of_birth = ?, date_of_start = ?, phone_number = ?, city = ?, street = ?, zip_code = ? WHERE id_employee = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

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

        String query = "DELETE FROM employee WHERE id_employee = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, employeeId);
            statement.execute();
            return new Response<>(null, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Employee>> findMostValuableCashier() {
        String query = "SELECT * " +
                "FROM employee " +
                "WHERE id_employee IN ( " +
                "SELECT id_employee " +
                "FROM receipt " +
                "GROUP BY id_employee " +
                "HAVING SUM(sum_total) = ( " +
                "SELECT MAX(stats.total) " +
                "FROM ( " +
                "SELECT SUM(sum_total) AS total " +
                "FROM receipt " +
                "GROUP BY id_employee) AS stats " +
                ")" +
                ")";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<Employee> employeeList = new LinkedList<>();

            while (resultSet.next()) {
                Employee employee = employeeFromResultSet(resultSet);
                employeeList.add(employee);
            }

            return new Response<>(employeeList, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<List<Employee>> getCashiersSortedBySurname() {

        String query = "SELECT * FROM EMPLOYEE WHERE empl_role='Cashier' ORDER BY empl_surname ASC";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            List<Employee> employeeList = new LinkedList<>();

            while (resultSet.next()) {
                Employee employee = employeeFromResultSet(resultSet);
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

        String query = "SELECT phone_number, city, street, zip_code FROM EMPLOYEE WHERE empl_surname=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
        String query = "SELECT * FROM EMPLOYEE WHERE id_employee=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Response<>(employeeFromResultSet(resultSet), new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Employee> findEmployeeByUsername(String username) {

        if (username == null || username.isBlank()) {
            return new Response<>(null, Collections.singletonList("Username can't be null"));
        }
        String query = "SELECT * FROM EMPLOYEE WHERE username=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Employee employee = employeeFromResultSet(resultSet);
            employee.setPassword(resultSet.getString("password"));
            employee.setUsername(resultSet.getString("username"));
            employee.setAuthority(roleService.findRoleById(resultSet.getLong("id_role")).getObject());
            return new Response<>(employee, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }


    public Response<List<EmployeeStatistic>> getEmployeeReceiptSumStats(double sum) {
        String query = "SELECT e.id_employee, e.empl_surname, print_date AS date, COUNT(*) AS receipt_amount FROM Receipt r " +
                "LEFT JOIN Employee e ON r.id_employee = e.id_employee WHERE r.sum_total > ? GROUP BY print_date, e.empl_surname;";


        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, sum);
            ResultSet resultSet = statement.executeQuery();

            List<EmployeeStatistic> employeeStatistics = new LinkedList<>();

            while (resultSet.next()) {
                String id = resultSet.getString("id_employee");
                String surname = resultSet.getString("empl_surname");
                Date date = resultSet.getDate("date");
                int receiptAmount = resultSet.getInt("receipt_amount");

                employeeStatistics.add(new EmployeeStatistic(id, surname, date, receiptAmount));
            }

            return new Response<>(employeeStatistics, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }
    }

    public Response<Map<String, Integer>> getCityPeopleCount() {
        String query = "SELECT city, COUNT(*) AS people_amount FROM (SELECT city FROM Employee UNION ALL " +
                "SELECT city FROM Customer_card WHERE city IS NOT NULL) GROUP BY city";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            Map<String, Integer> result = new HashMap<>();

            while (resultSet.next()) {
                result.put(resultSet.getString("city"), resultSet.getInt("people_amount"));
            }

            return new Response<>(result, new LinkedList<>());
        } catch (SQLException e) {
            return new Response<>(null, Collections.singletonList(e.getMessage()));
        }

    }


    private List<String> validateEmployee(Employee employee) {
        List<String> errors = new LinkedList<>();

        if (employee.getEmployeeId() == null || employee.getEmployeeId().isBlank()) {
            errors.add("Id can't be empty");
        }
        if (employee.getSurname() == null || employee.getSurname().isBlank()) {
            errors.add("Surname can't be empty");
        }
        if (employee.getName() == null || employee.getName().isBlank()) {
            errors.add("Name can't be empty");
        }
        if (employee.getPatronymic() == null || employee.getPatronymic().isBlank()) {
            errors.add("Patronymic can't be empty");
        }
        if (employee.getRole() == null || employee.getRole().isBlank()) {
            errors.add("Role can't be empty");
        }
        if (employee.getSalary() == null || employee.getSalary() <= 0) {
            errors.add("Salary must be more than 0");
        }
        if (employee.getPhoneNumber() == null || employee.getPhoneNumber().isBlank()) {
            errors.add("Phone number can't be empty");
        }
        if (employee.getCity() == null || employee.getCity().isBlank()) {
            errors.add("City can't be empty");
        }
        if (employee.getStreet() == null || employee.getStreet().isBlank()) {
            errors.add("Street can't be empty");
        }
        if (employee.getZipCode() == null || employee.getZipCode().isBlank()) {
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

    private Employee employeeFromResultSet(ResultSet resultSet) throws SQLException {
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
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        Long roleId = resultSet.getLong("id_role");
        Role authority = roleService.findRoleById(roleId).getObject();

        return new Employee(id, surname, name, patronymic, role, salary, birthDate, startDate, phoneNumber, city, street, zipCode, username, password, authority);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Employee employee = findEmployeeByUsername(s).getObject();

        if (employee == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return employee;
    }
}
