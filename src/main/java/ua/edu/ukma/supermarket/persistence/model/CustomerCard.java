package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCard {

    private String cardNumber;
    private String customerSurname;
    private String customerName;
    private String customerPatronymic;
    private String city;
    private String street;
    private String zipcode;
    private int percent;

}
