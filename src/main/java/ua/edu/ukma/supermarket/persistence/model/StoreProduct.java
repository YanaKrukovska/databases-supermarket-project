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

    public String getUpc() {
        return upc;
    }

    public String getUpcPromo() {
        return upcPromo;
    }

    public int getProductId() {
        return productId;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public int getProductsNumber() {
        return productsNumber;
    }

    public String isPromotionalProductString() {
        return isPromotionalProduct? "Yes":"No";
    }

    public boolean isPromotionalProduct() {
        return isPromotionalProduct;
    }
}
