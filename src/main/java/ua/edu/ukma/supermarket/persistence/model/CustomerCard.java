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
    private String phone;
    private String city;
    private String street;
    private String zipcode;
    private int percent;

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCustomerSurname() {
        return customerSurname;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPatronymic() {
        return customerPatronymic;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getDiscount() {
        return percent+"%";
    }
}
