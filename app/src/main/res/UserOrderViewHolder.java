package tarc.edu.prototype;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserOrderViewHolder extends RecyclerView.ViewHolder {
    public TextView oid,pPrice;
    public ImageView pImage;

    public UserOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        oid = itemView.findViewById(R.id.oid);
        pPrice = itemView.findViewById(R.id.pPrice);
        pImage = itemView.findViewById(R.id.pImage);
    }
}
