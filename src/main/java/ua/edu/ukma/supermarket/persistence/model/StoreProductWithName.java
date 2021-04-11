package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreProductWithName extends StoreProduct{

    private String productName;

    public StoreProductWithName(String upc, String upcPromo, int productId, double sellingPrice, int productsNumber,
                                boolean isPromotionalProduct, String productName) {
        super(upc, upcPromo, productId, sellingPrice, productsNumber, isPromotionalProduct);
        this.productName = productName;
    }
}
