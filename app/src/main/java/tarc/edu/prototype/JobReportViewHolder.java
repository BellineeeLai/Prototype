package tarc.edu.prototype;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class JobReportViewHolder extends RecyclerView.ViewHolder {
    TextView ddate,sid,sstatus;
    public JobReportViewHolder(@NonNull View itemView) {
        super(itemView);
        ddate = itemView.findViewById(R.id.ddate);
        sid = itemView.findViewById(R.id.sid);
        sstatus = itemView.findViewById(R.id.sstatus);
    }
}
