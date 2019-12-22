package tarc.edu.prototype.Model;

public class Product {
    private String productID;
    private String productName;
    private String productImage;
    private String category;
    private String description;
    private String purchasePrice;
    private String sellPrice;
    private String qrCode;
    private int sold;
    private String barCode;
    private String brand;
    private int stock;
    private long date;
    private String location;
    private String totalCost;

    Product(){

    }

    public Product(String productID, String productName, String productImage){
        this.productID = productID;
        this.productName = productName;
        this.productImage = productImage;
    }

    public Product(String productID, String productName, String productImage, String sellPrice) {
        this.productID = productID;
        this.productName = productName;
        this.productImage = productImage;
        this.sellPrice = sellPrice;
    }

    Product(String productID, String productName, String productImage, String category, String sellPrice, int stock){
        this.productID = productID;
        this.productName = productName;
        this.productImage = productImage;
        this.stock = stock;
        this.sellPrice = sellPrice;
        this.category = category;
    }

    public Product(String productID, String productName, String productImage, String category, String description, String purchasePrice, String sellPrice, String qrCode, int sold, String barCode, String brand, int stock, long date, String location) {
        this.productID = productID;
        this.productName = productName;
        this.productImage = productImage;
        this.category = category;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.sellPrice = sellPrice;
        this.qrCode = qrCode;
        this.sold = sold;
        this.barCode = barCode;
        this.brand = brand;
        this.stock = stock;
        this.date = date;
        this.location = location;
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

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }
}

