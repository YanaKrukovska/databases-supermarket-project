package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements UserDetails {

    private String employeeId;
    private String surname;
    private String name;
    private String patronymic;
    private String role;
    private Double salary;
    private Date birthDate;
    private Date startDate;
    private String phoneNumber;
    private String city;
    private String street;
    private String zipCode;
    private String username;
    private String password;
    private Role authority;

    public Employee(String employeeId, String surname, String name, String patronymic, String role, Double salary,
                    Date birthDate, Date startDate, String phoneNumber, String city, String street, String zipCode) {
        this.employeeId = employeeId;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.role = role;
        this.salary = salary;
        this.birthDate = birthDate;
        this.startDate = startDate;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }

    public Employee(String employeeId, String surname, String name, String patronymic, String role, Double salary, Date
            birthDate, Date startDate, String phoneNumber, String city, String street, String zipCode, String username, Role authority) {
        this.employeeId = employeeId;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.role = role;
        this.salary = salary;
        this.birthDate = birthDate;
        this.startDate = startDate;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
        this.username = username;
        this.authority = authority;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getRole() {
        return role;
    }

    public Double getSalary() {
        return salary;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(authority);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
