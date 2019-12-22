package tarc.edu.prototype.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tarc.edu.prototype.R;


public class WishlistViewHolder extends RecyclerView.ViewHolder{

    public TextView productName;
    public TextView sellPrice;
    public ImageView productImage;
    public TextView productStock;

    public WishlistViewHolder(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.productName);
        sellPrice = itemView.findViewById(R.id.sellPrice);
        productImage = itemView.findViewById(R.id.productImage);
        productStock = itemView.findViewById(R.id.productStock);
    }
}
