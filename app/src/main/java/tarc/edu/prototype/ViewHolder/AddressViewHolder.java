package tarc.edu.prototype.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tarc.edu.prototype.R;


public class AddressViewHolder extends RecyclerView.ViewHolder {
    public TextView receiverAddress, phoneNo, receiverName;

    public AddressViewHolder(@NonNull View itemView) {
        super(itemView);
        receiverAddress = itemView.findViewById(R.id.rAddress);
        phoneNo = itemView.findViewById(R.id.phoneNo);
        receiverName = itemView.findViewById(R.id.receiverName);
    }
}
