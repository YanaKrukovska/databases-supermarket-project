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
public class CategoryStatistic {

    private int categoryNumber;
    private String categoryName;
    private int soldProductsAmount;

    public int getCategoryNumber() {
        return categoryNumber;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getSoldProductsAmount() {
        return soldProductsAmount;
    }
}
