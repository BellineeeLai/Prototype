package tarc.edu.prototype;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDetailsViewHolder extends RecyclerView.ViewHolder {

    public TextView oid,pPrice;
    public ImageView pImage;

    public OrderDetailsViewHolder(@NonNull View itemView) {
        super(itemView);
        oid = itemView.findViewById(R.id.oid);
        pPrice = itemView.findViewById(R.id.pPrice);

    }
}
