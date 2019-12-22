package tarc.edu.prototype.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tarc.edu.prototype.R;


public class TransactionViewHolder extends RecyclerView.ViewHolder {
    public TextView tstatus,tamount;

    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);
        tstatus = itemView.findViewById(R.id.textTStatus);
        tamount = itemView.findViewById(R.id.txtTAmount);

    }
}
