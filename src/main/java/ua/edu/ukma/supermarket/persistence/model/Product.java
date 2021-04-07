package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private int productId;
    private String productName;
    private String characteristics;
    private int categoryNumber;

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public int getCategoryNumber() {
        return categoryNumber;
    }
}
