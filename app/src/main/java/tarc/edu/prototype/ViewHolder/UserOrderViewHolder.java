package tarc.edu.prototype.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tarc.edu.prototype.R;

public class UserOrderViewHolder extends RecyclerView.ViewHolder {
    public TextView oid,pPrice, txtcom;
    public Button completeBtn;
    public UserOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        oid = itemView.findViewById(R.id.oid);
        pPrice = itemView.findViewById(R.id.pPrice);
        completeBtn = itemView.findViewById(R.id.completeBtn);
        txtcom = itemView.findViewById(R.id.txtcom);
    }
}
