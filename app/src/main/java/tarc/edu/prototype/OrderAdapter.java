package tarc.edu.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import tarc.edu.prototype.Model.UserOrder;
import tarc.edu.prototype.Model.UserOrderList;
import tarc.edu.prototype.ViewHolder.UserOrderViewHolder;

import static android.content.Context.MODE_PRIVATE;


public class OrderAdapter extends FirebaseRecyclerAdapter<UserOrderList, UserOrderViewHolder> {

    private static final String FILE_NAME = "SmartDresser";
    private Context context;
    private String userNode;
    private String orderNode;
    private String staffId, userType, staffNode,status;
    private FirebaseUser user;
    DatabaseReference uoRef;

    public OrderAdapter(@NonNull FirebaseRecyclerOptions<UserOrderList> options, Context context) {
        super(options);
        this.context = context;
        this.orderNode = "Orders";
        this.userNode = "Users";
        this.staffNode = "Staff";
    }

    @Override
    protected void onBindViewHolder(@NonNull UserOrderViewHolder userOrderViewHolder, int i, @NonNull final UserOrderList userOrder) {
        assert  context != null;
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

        assert userType != null;
        if(userType.equals("Staff")){
            staffId = sharedPreferences.getString("staffId", null);
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userOrderViewHolder.oid.setText(userOrder.getOrderId());
        userOrderViewHolder.pPrice.setText(context.getResources().getString(R.string.currency, userOrder.getAmount()));
        userOrderViewHolder.txtcom.setText(userOrder.getStatus());
        if(userOrder.getStatus().equals("COMPLETE")) {
            userOrderViewHolder.completeBtn.setVisibility(View.INVISIBLE);
            userOrderViewHolder.trackBtn.setVisibility(View.INVISIBLE);
        }
        userOrderViewHolder.completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType.equals("Customer")) {
                    uoRef = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(userNode)
                            .child(user.getUid())
                            .child(orderNode);
                }
                if(userType.equals("Staff")) {
                    uoRef = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(staffNode)
                            .child(staffId)
                            .child(orderNode);

                }

                DatabaseReference tempRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("CustomerOrder").child(userOrder.getOrderId());

                DatabaseReference tempRef2 = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("UserOrder");

                /*tempRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserOrder order = dataSnapshot.getValue(UserOrder.class);
                        if(order.getOrderID().equals(userOrder.getOrderId())){
                            order.setStatus("COMPLETE");
                            tempRef2.child(dataSnapshot.getKey()).setValue(order);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/

                ArrayList<UserOrder> orderList = new ArrayList<>();
                final HashMap<String, Object> userOrderMap = new HashMap<>();
                userOrderMap.put("status", "COMPLETE");
                tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            orderList.add(ds.getValue(UserOrder.class));
                        }
                        for(int i = 0; i < orderList.size(); i++){
                            orderList.get(i).setStatus("COMPLETE");
                        }

                        tempRef.setValue(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                uoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            UserOrderList uo = ds.getValue(UserOrderList.class);
                            userOrder.setStatus("COMPLETE");
                            if(uo.getOrderId().equals(userOrder.getOrderId())){
                                uoRef.child(uo.getOrderId()).setValue(userOrder);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
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