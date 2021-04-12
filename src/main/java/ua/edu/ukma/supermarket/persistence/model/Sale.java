package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    private String upc;
    private String receiptNumber;
    private int productNumber;
    private double sellingPrice;

}
