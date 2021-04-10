package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicCustomerCard {

    private String customerSurname;
    private String customerName;
    private String customerPatronymic;
    private String phone;
    private String city;
    private String street;
}
