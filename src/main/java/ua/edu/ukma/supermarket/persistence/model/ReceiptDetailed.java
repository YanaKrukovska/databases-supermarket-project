package ua.edu.ukma.supermarket.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ReceiptDetailed extends Receipt {

    private List<ProductDetails> productDetailsList;

    public List<ProductDetails> getProductDetailsList() {
        return productDetailsList;
    }

    public ReceiptDetailed(Integer receiptNumber, String employeeId, Integer cardNumber, Date printDate, double sumTotal,
                           double vat, List<ProductDetails> productDetailsList) {
        super(receiptNumber, employeeId, cardNumber, printDate, sumTotal, vat);
        this.productDetailsList = productDetailsList;
    }

    @Getter
    @Setter
    public static class ProductDetails {
        private String productName;
        private int productAmount;
        private double productPrice;

        public ProductDetails(String productName, int productAmount, double productPrice) {
            this.productName = productName;
            this.productAmount = productAmount;
            this.productPrice = productPrice;
        }

        public String getProductName() {
            return productName;
        }

        public int getProductAmount() {
            return productAmount;
        }

        public double getProductPrice() {
            return productPrice;
        }
    }

}
