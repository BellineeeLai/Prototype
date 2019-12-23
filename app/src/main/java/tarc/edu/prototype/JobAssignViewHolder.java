package tarc.edu.prototype;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.CollationElementIterator;


public class JobAssignViewHolder extends RecyclerView.ViewHolder {

     TextView sid;
     ImageView done;

    public JobAssignViewHolder(@NonNull View itemView) {
        super(itemView);
        sid = itemView.findViewById(R.id.shippingid);
        done = itemView.findViewById(R.id.done);
    }
}
