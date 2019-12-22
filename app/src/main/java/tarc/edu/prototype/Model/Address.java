package tarc.edu.prototype.Model;

import java.io.Serializable;

public class Address implements Serializable {
    private String address;
    private String area;
    private String phoneNo;
    private String state;
    private String receiverName;
    private String postalCode;
    private String defaultAddress;

    public Address() {
    }

    public Address(String receiverName, String phoneNo, String address, String postalCode, String area, String state,String defaultAddress) {
        this.receiverName = receiverName;
        this.phoneNo = phoneNo;
        this.address = address;
        this.postalCode = postalCode;
        this.area = area;
        this.state = state;
        this.defaultAddress = defaultAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
