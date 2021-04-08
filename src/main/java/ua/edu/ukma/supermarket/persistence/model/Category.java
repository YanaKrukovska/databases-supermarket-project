package ua.edu.ukma.supermarket.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private int categoryNumber;
    private String categoryName;


    public int getCategoryNumber() {
        return categoryNumber;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
