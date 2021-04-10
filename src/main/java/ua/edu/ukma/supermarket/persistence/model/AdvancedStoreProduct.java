package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedStoreProduct {

    private String productName;
    private String characteristics;
    private double sellingPrice;
    private int productsNumber;
}
