package tarc.edu.prototype.Model;

@SuppressWarnings("unused")
public class Cart {
    private String productID;
    private String productName;
    private String productQuantity;
    private String itemsPrice;
    private String productImage;
    private String sellPrice;

    public Cart() {
    }

    public Cart(String productID, String productName, String productQuantity, String itemsPrice, String productImage, String sellPrice) {
        this.productID = productID;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.itemsPrice = itemsPrice;
        this.productImage = productImage;
        this.sellPrice = sellPrice;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getItemsPrice() {
        return itemsPrice;
    }

    public void setItemsPrice(String itemsPrice) {
        this.itemsPrice = itemsPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }
}

