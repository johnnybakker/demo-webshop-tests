package nl.avans.data;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {
    private static List<ShopItem> items;
    private String category;
    private String subCategorySelector;
    private String productName;
    private String quantityInputId;
    private String addToCartButtonId;

    private int price;

    public ShopItem(String category, String subCategorySelector, String productName,
                    String quantityInputId, String addToCartButtonId, int price) {
        this.category = category;
        this.subCategorySelector = subCategorySelector;
        this.productName = productName;
        this.quantityInputId = quantityInputId;
        this.addToCartButtonId = addToCartButtonId;
        this.price = price;
    }

    // Getters and setters (optional) for the properties

    public static List<ShopItem> getItems() {
        if (items == null) {
            initializeItems();
        }
        return items;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategorySelector() {
        return subCategorySelector;
    }

    public void setSubCategorySelector(String subCategorySelector) {
        this.subCategorySelector = subCategorySelector;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantityInputId() {
        return quantityInputId;
    }

    public void setQuantityInputId(String quantityInputId) {
        this.quantityInputId = quantityInputId;
    }

    public String getAddToCartButtonId() {
        return addToCartButtonId;
    }

    public void setAddToCartButtonId(String addToCartButtonId) {
        this.addToCartButtonId = addToCartButtonId;
    }

    private static void initializeItems() {
        items = new ArrayList<>();
        items.add(new ShopItem("Electronics", "div.sub-category-grid div.item-box:nth-child(2) a[href=\"/cell-phones\"]",
                "Smartphone", "addtocart_43.EnteredQuantity", "add-to-cart-button-43", 100));
        items.add(new ShopItem("Apparel & Shoes", "", "Blue Jeans", "addtocart_36.EnteredQuantity",
                "add-to-cart-button-36", 5));
    }
}