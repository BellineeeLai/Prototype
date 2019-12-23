package tarc.edu.prototype;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductOrderViewHolder extends RecyclerView.ViewHolder {
    ImageView pimage;
    TextView pname,pprice,pqty;
    public ProductOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        pimage = itemView.findViewById(R.id.pimage);
        pname = itemView.findViewById(R.id.pName);
        pprice = itemView.findViewById(R.id.pprice);
        pqty = itemView.findViewById(R.id.pqty);

    }
}
