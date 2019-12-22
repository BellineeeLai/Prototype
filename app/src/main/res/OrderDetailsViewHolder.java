package tarc.edu.prototype;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDetailsViewHolder extends RecyclerView.ViewHolder {

    public TextView oid, pname,pqty,pPrice;
    public ImageView pImage;
   
    public OrderDetailsViewHolder(@NonNull View itemView) {
        super(itemView);
        oid = itemView.findViewById(R.id.oid);
        pPrice = itemView.findViewById(R.id.pPrice);
        pImage = itemView.findViewById(R.id.pImage);
        pname = itemView.findViewById(R.id.pName);
        pqty = itemView.findViewById(R.id.pQty);
    }
}
