package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    private Integer receiptNumber;
    private String employeeId;
    private Integer cardNumber;
    private Date printDate;
    private double sumTotal;
    private double vat;

    public Integer getReceiptNumber() {
        return receiptNumber;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public Integer getCardNumber() {
        return cardNumber;
    }

    public Date getPrintDate() {
        return printDate;
    }

    public double getSumTotal() {
        return sumTotal;
    }

    public double getVat() {
        return vat;
    }
}
