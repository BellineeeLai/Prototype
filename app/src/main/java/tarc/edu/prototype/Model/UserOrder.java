package tarc.edu.prototype.Model;

public class UserOrder {
    private String productID;
    private String productName;
    private String productQuantity;
    private String sellPrice;
    private String orderID;
    private String date;
    private String status;

    public UserOrder() {
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

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserOrder(String productID, String productName, String productQuantity, String sellPrice, String orderID, String date, String status) {
        this.productID = productID;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.sellPrice = sellPrice;
        this.orderID = orderID;
        this.date = date;
        this.status = status;
    }
}
