package tarc.edu.prototype.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tarc.edu.prototype.R;


public class ProductViewHolder extends RecyclerView.ViewHolder{

    public TextView productName, sellPrice;
    public ImageView productImage;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.productImage);
        productName = itemView.findViewById(R.id.productName);
        sellPrice = itemView.findViewById(R.id.sellPrice);
    }
}
