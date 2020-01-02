package tarc.edu.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import tarc.edu.prototype.Model.UserOrder;
import tarc.edu.prototype.Model.UserOrderDetail;

public class JobDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView name,phone,order,add;
    private Button btnDelivered,btnDeliverFailed;
    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private String userType, tempId, tempNode,orderId,orderDetailsNode,orderNode;
    FirebaseUser user;
    DatabaseReference odref;
    Query query;

    public JobDetailsActivity() {
        orderDetailsNode = "UserOrderDetail";
        orderNode = "CustomerOrder";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        add = findViewById(R.id.add);
        order = findViewById(R.id.order);
        btnDelivered = findViewById(R.id.btnDelivered);
        btnDeliverFailed = findViewById(R.id.btnDeliveredFailed);
        sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userType = sharedPreferences.getString("user", null);
        assert  orderId != null;
        orderId = getIntent().getStringExtra("Id");
        assert userType != null;
        if (userType.equals("Customer")) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            tempId = user.getUid();
            tempNode = "Users";

        } else {
            tempId = sharedPreferences.getString("staffId", null);
            tempNode = "Staff";
        }


        displayDetails();
        btnDelivered.setOnClickListener(this);

    }

    private void displayDetails() {
        odref = FirebaseDatabase.getInstance().getReference().child(orderDetailsNode);

        query = odref.orderByChild("orderID").equalTo(orderId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserOrderDetail uod = snapshot.getValue(UserOrderDetail.class);
                    assert uod != null;
                    name.setText(uod.getAddress().getReceiverName());
                    phone.setText(uod.getAddress().getPhoneNo());
                    order.setText(orderId);
                    add.setText(uod.getAddress().getAddress()+ " " + uod.getAddress().getPostalCode() + " "+
                            uod.getAddress().getArea() + " " + uod.getAddress().getState());
            }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        DatabaseReference oref = FirebaseDatabase.getInstance().getReference().child(orderNode).child(orderId);
        if(view == btnDelivered){

         /*   oref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserOrder uo = dataSnapshot.getValue(UserOrder.class);
                    assert uo != null;
                    uo.setStatus("Delivering");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/

            ArrayList<UserOrder> orderList = new ArrayList<>();
            final HashMap<String, Object> userOrderMap = new HashMap<>();
            userOrderMap.put("status", "COMPLETE");
            oref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        orderList.add(ds.getValue(UserOrder.class));
                    }
                    for(int i = 0; i < orderList.size(); i++){
                        orderList.get(i).setStatus("COMPLETE");
                    }

                    oref.setValue(orderList);

                    Intent intent = new Intent(getApplicationContext(),JobAssignedActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(view == btnDeliverFailed){
         oref.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 Intent intent = new Intent(getApplicationContext(),JobAssignedActivity.class);
                 startActivity(intent);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         })   ;
        }
    }
}
