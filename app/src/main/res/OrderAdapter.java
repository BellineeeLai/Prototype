package tarc.edu.prototype;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import tarc.edu.prototype.ViewHolder.UserOrderViewHolder;


public class OrderAdapter extends FirebaseRecyclerAdapter<UserOrderList, UserOrderViewHolder> {

    private Context context;

    public OrderAdapter(@NonNull FirebaseRecyclerOptions<UserOrderList> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserOrderViewHolder userOrderViewHolder, int i, @NonNull UserOrderList userOrder) {
        userOrderViewHolder.oid.setText(userOrder.getOrderId());
        userOrderViewHolder.pPrice.setText(context.getResources().getString(R.string.currency, userOrder.getAmount()));
        userOrderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("Id",userOrder.getOrderId());
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public UserOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history, parent, false);

        return new UserOrderViewHolder(view);
    }
}