package tarc.edu.prototype;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import tarc.edu.prototype.Model.Product;
import tarc.edu.prototype.Model.Transaction;
import tarc.edu.prototype.Model.UserOrder;
import tarc.edu.prototype.Model.UserOrderDetail;


public class OrderDetailsActivity extends AppCompatActivity {

    private TextView rtoship,rName,rPhone,radd,ttotal,oid,transacId,tdate,sdatetime,textstimedate;
    private ImageView imageDelivery;
    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private String userType, tempId, tempNode,orderNode,orderId,orderDetailsNode,transNode,transId,userOrderNode;
    FirebaseUser user;
    DatabaseReference orderdref, transref,odref,pref;
    Query addquery,pquery;
    UserOrder userOrder;
    UserOrderDetail userOrderDetail;
    Transaction transaction;
    Product p;
    private RecyclerView orderProduct;
    private FirebaseRecyclerOptions<UserOrder> options;
    private FirebaseRecyclerAdapter<UserOrder, ProductOrderViewHolder> adapter;

    public OrderDetailsActivity() {
        orderNode = "Orders";
        orderDetailsNode = "UserOrderDetail";
        transNode = "Transaction";
        userOrderNode = "CustomerOrder";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        orderProduct = findViewById(R.id.orderProduct);
        orderProduct.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        orderProduct.setLayoutManager(layoutManager);

        rtoship = findViewById(R.id.rtoship);
        imageDelivery = findViewById(R.id.imagedelivery);
        rName = findViewById(R.id.rName);
        rPhone = findViewById(R.id.rPhone);
        radd = findViewById(R.id.radd);
        ttotal = findViewById(R.id.ttotal);
        oid = findViewById(R.id.orderId);
        transacId = findViewById(R.id.transId);
        tdate = findViewById(R.id.tdate);
        sdatetime = findViewById(R.id.sdatetime);
        textstimedate = findViewById(R.id.textstimedate);

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

        odref = FirebaseDatabase.getInstance().getReference().child(orderDetailsNode);

        addquery = odref.orderByChild("orderID").equalTo(orderId);
        checkOrderStatus();
        displayAddress();

        displayProduct();
    }

    private void displayProduct() {
        DatabaseReference uoRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(userOrderNode).child(orderId);

        options = new FirebaseRecyclerOptions.Builder<UserOrder>()
                .setQuery(uoRef, UserOrder.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<UserOrder, ProductOrderViewHolder>(options) {

            @NonNull
            @Override
            public ProductOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_order, parent, false);
                return new ProductOrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductOrderViewHolder productOrderViewHolder, int i, @NonNull UserOrder userOrder) {
                productOrderViewHolder.pqty.setText(userOrder.getProductQuantity());
                productOrderViewHolder.pprice.setText(getString(R.string.currency,Double.valueOf(userOrder.getSellPrice())));
                productOrderViewHolder.pname.setText(userOrder.getProductName());
                String pid = userOrder.getProductID();
                pref = FirebaseDatabase.getInstance().getReference().child("Product").child(pid);
                pref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            p = dataSnapshot.getValue(Product.class);
                            Picasso.get().load(p.getProductImage()).into(productOrderViewHolder.pimage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        adapter.startListening();
        orderProduct.setAdapter(adapter);
    }
    private void displayAddress() {

        addquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userOrderDetail = snapshot.getValue(UserOrderDetail.class);
                    assert userOrderDetail != null;
                    transId = userOrderDetail.getTransId();
                    rName.setText(userOrderDetail.getAddress().getReceiverName());
                    rPhone.setText(userOrderDetail.getAddress().getPhoneNo());
                    oid.setText(userOrderDetail.getOrderID());
                    radd.setText(userOrderDetail.getAddress().getAddress()+ " " + userOrderDetail.getAddress().getPostalCode() + " "+
                            userOrderDetail.getAddress().getArea() + " " + userOrderDetail.getAddress().getState());

                    transref = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(tempNode)
                            .child(tempId)
                            .child(transNode).child(transId);

                    transref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                transaction = dataSnapshot.getValue(Transaction.class);
                                assert transaction != null;
                                String total = String.valueOf(transaction.getAmount());
                                ttotal.setText(getString(R.string.currency,  Double.valueOf(total)));
                                transacId.setText(transaction.getTransactionId());
                                tdate.setText(transaction.getDateTime());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }
        );

    }

    private void checkOrderStatus() {
            orderdref = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(tempNode)
                    .child(tempId)
                    .child(orderNode)
                    .child(orderId);

            orderdref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        userOrder = dataSnapshot.getValue(UserOrder.class);
                        if(userOrder.getStatus().equals("COMPLETE")){
                            loadTitleComplete();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void loadTitleComplete() {
        rtoship.setText("Order Completed");
        imageDelivery.setImageResource(R.drawable.ic_shipped);
        sdatetime.setVisibility(View.VISIBLE);
        textstimedate.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
