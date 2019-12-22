package tarc.edu.prototype.Model;

public class UserOrderDetail {
    private String transId;
    private Address address;
    private String date;
    private String userID;
    private String paymentStatus;
    private String shippingMethod;
    private String orderID;

    public UserOrderDetail() {
    }

    public UserOrderDetail(String transId, Address address, String date, String userID, String paymentStatus, String shippingMethod, String orderID) {
        this.transId = transId;
        this.address = address;
        this.date = date;
        this.userID = userID;
        this.paymentStatus = paymentStatus;
        this.shippingMethod = shippingMethod;
        this.orderID = orderID;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}
