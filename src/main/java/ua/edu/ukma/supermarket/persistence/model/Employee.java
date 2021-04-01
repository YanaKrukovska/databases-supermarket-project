package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

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

}
