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
public class EmployeeStatistic {

    private String employeeId;
    private String employeeSurname;
    private Date date;
    private int receiptAmount;

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeSurname() {
        return employeeSurname;
    }

    public Date getDate() {
        return date;
    }

    public int getReceiptAmount() {
        return receiptAmount;
    }
}
