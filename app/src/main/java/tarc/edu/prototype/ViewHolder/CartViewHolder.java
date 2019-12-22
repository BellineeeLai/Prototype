package tarc.edu.prototype.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import tarc.edu.prototype.R;


public class CartViewHolder extends RecyclerView.ViewHolder{

    public TextView productName, sellPrice, productQuantity, leftover;
    public ImageView productImage;
    public ElegantNumberButton edit_qty;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.productName);
        sellPrice = itemView.findViewById(R.id.sellPrice);
        productQuantity = itemView.findViewById(R.id.productQuantity);
        productImage = itemView.findViewById(R.id.productImage);
        edit_qty = itemView.findViewById(R.id.edit_qty);
        leftover = itemView.findViewById(R.id.leftover);
    }
}
