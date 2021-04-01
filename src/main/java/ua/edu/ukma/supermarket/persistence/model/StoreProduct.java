package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreProduct {

    private String upc;
    private String upcPromo;
    private int productId;
    private double sellingPrice;
    private int productsNumber;
    private boolean isPromotionalProduct;

}
