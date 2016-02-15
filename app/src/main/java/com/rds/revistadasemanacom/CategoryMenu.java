package com.rds.revistadasemanacom;

/**
 * Created by brunomorais on 15/02/16.
 */
public class CategoryMenu {

    private String catName;
    private int quantity = 0;

    //Sample Content
    public static final CategoryMenu[] categoryMenu = {
            new CategoryMenu("Todos"),
            new CategoryMenu("Blog"),
            new CategoryMenu("Nacionais"),
            new CategoryMenu("Regional"),
            new CategoryMenu("Estaduais"),
            new CategoryMenu("Locais")
    };

    //Constructor
    public CategoryMenu(String name) {
        this.catName = name;
    }

    //Getter and Setter

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        } else {
            this.quantity = 0;
        }

    }


}
