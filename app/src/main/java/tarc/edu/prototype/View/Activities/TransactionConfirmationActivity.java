package tarc.edu.prototype.View.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import tarc.edu.prototype.Animation.SuccessLoadingView;
import tarc.edu.prototype.Model.Address;
import tarc.edu.prototype.Model.Cart;
import tarc.edu.prototype.Model.Product;
import tarc.edu.prototype.Model.User;
import tarc.edu.prototype.Model.UserOrder;
import tarc.edu.prototype.Model.UserOrderDetail;
import tarc.edu.prototype.Model.UserOrderList;
import tarc.edu.prototype.R;

public class TransactionConfirmationActivity extends AppCompatActivity implements Serializable {
    private TextView transID, transTo, transDesc, transAmount;
    private Button btnSubmit;
    FirebaseDatabase database;
    DatabaseReference transaction, ref;

    private String tempTransId;
    String activity, orderId;
    double amount;

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private String userType, tempId, tempNode;

    private String cartNode;
    private String userOrderNode;
    private String shippingDetailsNode;

    private Address address;
    private String shipping;
    private SuccessLoadingView successView;

    public TransactionConfirmationActivity() {
        cartNode = "Cart";
        userOrderNode = "UserOrder";
        shippingDetailsNode = "UserOrderDetail";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_confirmation);

        sharedPreferences = this.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);
        address = (Address) getIntent().getSerializableExtra("address");
        shipping = getIntent().getStringExtra("shipping");

        assert userType != null;
        if (userType.equals("Customer")) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            tempId = user.getUid();
            tempNode = "Users";
        } else {
            tempId = sharedPreferences.getString("staffId", null);
            tempNode = "Staff";
        }

        transID = findViewById(R.id.transactionID);
        transTo = findViewById(R.id.To);
        transDesc = findViewById(R.id.transDesc);
        transAmount = findViewById(R.id.transAmount);
        btnSubmit = findViewById(R.id.btnSubmit);
        Button btnCancel = findViewById(R.id.btnCancel);
        successView = findViewById(R.id.success_loading_view);

        database = FirebaseDatabase.getInstance();

        amount = getIntent().getDoubleExtra("amount", 0.00);
        activity = getIntent().getStringExtra("activity");
        transaction = database.getReference("Transaction");

        displayTransactionConfirmation();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnSubmit) {
                    successView.startAnim();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (activity.equals("Payment")) {
                                tempTransId = transID.getText().toString().trim();
                                addToOrder();
                            }

                            String id = transID.getText().toString().trim();
                            String to = transTo.getText().toString().trim();
                            String desc = activity;
                            String status = "Successful";

                            Intent intent = new Intent(TransactionConfirmationActivity.this.getApplicationContext(), TransactionDoneActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("to", to);
                            intent.putExtra("transactionAmount", amount);
                            intent.putExtra("desc", desc);
                            intent.putExtra("status", status);
                            intent.putExtra("orderId", orderId);
                            TransactionConfirmationActivity.this.startActivity(intent);
                            TransactionConfirmationActivity.this.finish();
                        }
                    }, 2000);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = transID.getText().toString().trim();
                String to = transTo.getText().toString().trim();
                String desc = transDesc.getText().toString().trim();
                String status = "Failed";

                Intent intent = new Intent(TransactionConfirmationActivity.this.getApplicationContext(), TransactionDoneActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("to", to);
                intent.putExtra("amount", amount);
                intent.putExtra("desc", desc);
                intent.putExtra("status", status);
                intent.putExtra("orderId", orderId);
                TransactionConfirmationActivity.this.startActivity(intent);
                TransactionConfirmationActivity.this.finish();
            }
        });
    }

    private void displayTransactionConfirmation() {
        generateTransactionID();
        orderId = "null";

        transAmount.setText(getString(R.string.currency, amount));

        if (activity.equals("Topup")) {

            transDesc.setText(activity);
            transTo.setText(getString(R.string.smart_dresser));
        }
        if (activity.equals("Withdraw")) {
            transDesc.setText(activity);
            ref = database.getReference(tempNode).child(tempId).child("Details");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User cUser = dataSnapshot.getValue(User.class);
                    assert cUser != null;
                    transTo.setText(cUser.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            transTo.setText(getString(R.string.username));
        }

        if (activity.equals("Payment")) {
            orderId = getIntent().getStringExtra("orderId");
            transDesc.setText(activity);
            transTo.setText(getString(R.string.smart_dresser));
        }

    }

    private void generateTransactionID() {
        final Query transactionQuery = transaction.orderByKey().limitToLast(1);
        final String[] lastTransaction = new String[1];
        final Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        final String fd = df.format(date);
        transactionQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lastTransaction[0] = dataSnapshot.getChildren().iterator().next().getKey();
                    assert lastTransaction[0] != null;
                    String datePart = lastTransaction[0].substring(0, 6);
                    String indexPart = lastTransaction[0].substring(6, 12);

                    if (datePart.equals(fd)) {
                        int tempIndex = Integer.parseInt(indexPart);
                        tempIndex += 1;
                        if (tempIndex > 0 && tempIndex < 10) {
                            transID.setText(fd + "00000" + tempIndex);
                        }
                        if (tempIndex > 9 && tempIndex < 100) {
                            transID.setText(fd + "0000" + tempIndex);
                        }
                        if (tempIndex > 99 && tempIndex < 1000) {
                            transID.setText(fd + "000" + tempIndex);
                        }
                        if (tempIndex > 999 && tempIndex < 10000) {
                            transID.setText(fd + "00" + tempIndex);
                        }
                        if (tempIndex > 9999 && tempIndex < 100000) {
                            transID.setText(fd + "00" + tempIndex);
                        }
                        if (tempIndex > 99999 && tempIndex < 1000000) {
                            transID.setText(fd + "0" + tempIndex);
                        }
                        if (tempIndex > 9999 && tempIndex < 100000) {
                            transID.setText(fd + tempIndex);
                        }
                    }
                    //the next day transaction begin
                    if (!datePart.equals(fd)) {
                        int index = 0;
                        index += 1;
                        transID.setText(fd + "00000" + index);

                    }

                } else {
                    int index = 0;
                    index += 1;
                    transID.setText(fd + "00000" + index);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void addToOrder() {

        //TODO
        final DatabaseReference customerOrder = FirebaseDatabase
                .getInstance().getReference()
                .child("CustomerOrder");

        final DatabaseReference orderRef = FirebaseDatabase
                .getInstance().getReference()
                .child(tempNode)
                .child(tempId)
                .child("Orders");

        final DatabaseReference cartRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(tempNode)
                .child(tempId)
                .child(cartNode);

        final DatabaseReference userOrderRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(userOrderNode);

        final DatabaseReference shippingDetailsRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(shippingDetailsNode);

        final DatabaseReference productUpdateRef = FirebaseDatabase.getInstance().getReference().child("Product");

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String orderID = userOrderRef.push().getKey();
                    assert orderID != null;


                    final HashMap<String, Object> userOrderMap = new HashMap<>();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();
                    //TODO
                    ArrayList<UserOrder> orderList = new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final Cart cart = ds.getValue(Cart.class);
                        assert cart != null;

                        productUpdateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final HashMap<String, Object> updateProduct = new HashMap<>();

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Product product = ds.getValue(Product.class);
                                        assert product != null;

                                        if (cart.getProductID().equals(product.getProductID())) {
                                            updateProduct.put("stock", product.getStock() - Integer.parseInt(cart.getProductQuantity()));
                                            updateProduct.put("sold", product.getSold() + Integer.parseInt(cart.getProductQuantity()));
                                            productUpdateRef.child(product.getProductID()).updateChildren(updateProduct);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //TODO
                        orderList.add(new UserOrder(cart.getProductID(), cart.getProductName(), cart.getProductQuantity()
                                , cart.getSellPrice(), orderID, sdf.format(date), "To Receive"));

                        userOrderMap.put("productID", cart.getProductID());
                        userOrderMap.put("productName", cart.getProductName());
                        userOrderMap.put("productQuantity", cart.getProductQuantity());
                        userOrderMap.put("sellPrice", cart.getSellPrice());
                        userOrderMap.put("orderID", orderID);
                        userOrderMap.put("date", sdf.format(date));
                        userOrderMap.put("status", "TO RECEIVE");
                        userOrderRef.child(Objects.requireNonNull(userOrderRef.push().getKey())).setValue(userOrderMap);
                    }
                    //TODO
                    UserOrderList orderList1 = new UserOrderList(orderID, amount, "TO RECEIVE");
                    customerOrder.child(Objects.requireNonNull(orderID)).setValue(orderList);
                    orderRef.child(orderID).setValue(orderList1);

                    UserOrderDetail userOrderDetail = new UserOrderDetail(tempTransId, address, sdf.format(date), tempId, "COMPLETE", shipping,  orderID);

                    shippingDetailsRef.child(Objects.requireNonNull(shippingDetailsRef.push().getKey())).setValue(userOrderDetail);
                    cartRef.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}