package tarc.edu.prototype.Model;
@SuppressWarnings("unused")
public class Wishlist extends Product {

    public Wishlist(){

    }

    public Wishlist(String productID, String productName, String productImage, String categoryID, String sellPrice, int stock){
        super(productID, productName, productImage, categoryID, sellPrice, stock);
    }

}

