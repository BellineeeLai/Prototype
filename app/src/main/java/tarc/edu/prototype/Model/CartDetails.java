package tarc.edu.prototype.Model;

@SuppressWarnings("unused")
public class CartDetails {
    private String subtotal;
    private int totalQuantity;

    public CartDetails(String subtotal, int totalQuantity) {
        this.subtotal = subtotal;
        this.totalQuantity = totalQuantity;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
