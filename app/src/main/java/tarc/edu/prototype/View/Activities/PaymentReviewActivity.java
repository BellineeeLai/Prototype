package tarc.edu.prototype.View.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Objects;

import tarc.edu.prototype.Model.Address;
import tarc.edu.prototype.R;

public class PaymentReviewActivity extends AppCompatActivity implements Serializable {
    private static final String FILE_NAME = "SmartDresser";
    private String userOrderNode;
    private String addressNode;

    private Address address;
    private Address defaultAddress;
    private TextView address_choice, shipping_choice, payment_choice;

    private MaterialButton proceed_order;

    private String userType, tempId, tempNode;

    private double temporarySub;
    private double subtotal;

    private String orderID;
    private DatabaseReference userOrderRef;
    private TextView textPromo;
    private TextView txtSubtotal;

    public PaymentReviewActivity() {
        userOrderNode = "UserOrder";
        addressNode = "Address";
    }

    String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_review);

        SharedPreferences sharedPreferences = this.getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        userOrderRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(userOrderNode);

        userType = sharedPreferences.getString("user", null);
        temporarySub = getIntent().getDoubleExtra("subtotal", 0.00);
        orderID = userOrderRef.push().getKey();

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

        address_choice = findViewById(R.id.address_choice);
        shipping_choice = findViewById(R.id.shipping_choice);
        payment_choice = findViewById(R.id.payment_method_choice);
        textPromo = findViewById(R.id.text_promo);

        address_choice.setTextColor(getResources().getColor(R.color.green));
        shipping_choice.setText("Please select a shipping method.");
        shipping_choice.setTextColor(getResources().getColor(R.color.red));
        payment_choice.setText("Please select a payment method.");
        payment_choice.setTextColor(getResources().getColor(R.color.red));

        test = sharedPreferences.getString("user", "");

        DatabaseReference addressRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(tempNode)
                .child(tempId)
                .child(addressNode);

        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    defaultAddress = ds.getValue(Address.class);
                    address = defaultAddress;
                    assert defaultAddress != null;
                    if (defaultAddress.getDefaultAddress().equals("Yes")) {
                        address_choice.setText(defaultAddress.getAddress());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        MaterialCardView card_shipping_address = findViewById(R.id.card_shipping_address);
        MaterialCardView card_delivery_method = findViewById(R.id.card_delivery_method);
        MaterialCardView card_payment_method = findViewById(R.id.card_payment_method);
        MaterialCardView card_promo_code = findViewById(R.id.card_promo_code);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        subtotal = getIntent().getDoubleExtra("subtotal", 0.00);
        txtSubtotal = findViewById(R.id.subtotal);
        txtSubtotal.setText(getString(R.string.currency, subtotal));


        card_shipping_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentReviewActivity.this.getApplicationContext(), DeliveryAddressActivity.class);
                PaymentReviewActivity.this.startActivityForResult(intent, 1);
            }
        });

        card_delivery_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentReviewActivity.this.getApplicationContext(), ShippingMethodActivity.class);
                PaymentReviewActivity.this.startActivityForResult(intent, 2);
            }
        });

        card_payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentReviewActivity.this.getApplicationContext(), PaymentMethodActivity.class);
                intent.putExtra("subtotal", subtotal);
                PaymentReviewActivity.this.startActivityForResult(intent, 3);
            }
        });

        card_promo_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentReviewActivity.this.getApplicationContext(), PromoCodeActivity.class);
                PaymentReviewActivity.this.startActivityForResult(intent, 4);
            }
        });

        proceed_order = findViewById(R.id.proceed_order);
        if (TextUtils.isEmpty(address_choice.getText()) || TextUtils.isEmpty(shipping_choice.getText()) || TextUtils.isEmpty(payment_choice.getText())) {
            proceed_order.setEnabled(false);
        }

        proceed_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TransactionConfirmationActivity.class);
                intent.putExtra("amount", temporarySub);
                intent.putExtra("activity", "Payment");
                intent.putExtra("orderId", orderID);
                intent.putExtra("address", address);
                intent.putExtra("shipping", shipping_choice.getText());
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (data != null && resultCode == RESULT_OK) {
                address = (Address) data.getSerializableExtra("address");
                assert address != null;
                address_choice.setText(address.getAddress());
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null) {
                shipping_choice.setTextColor(getResources().getColor(R.color.green));
                shipping_choice.setText(Objects.requireNonNull(data.getStringExtra("shippingMethod")));
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            if (data != null) {
                payment_choice.setTextColor(getResources().getColor(R.color.green));
                payment_choice.setText(Objects.requireNonNull(data.getStringExtra("paymentMethod")));
            }
        } else if (requestCode == 4 && resultCode == RESULT_OK) {
            if (data != null) {
                textPromo.setText(Objects.requireNonNull(data.getStringExtra("promo_code")));
                double afterDiscount = subtotal - data.getDoubleExtra("discount", 0.00);
                if(afterDiscount < 0.00){
                    afterDiscount = 0.00;
                }
                txtSubtotal.setText(getString(R.string.currency, afterDiscount));
                temporarySub = afterDiscount;
            }
        }

        if (TextUtils.isEmpty(address_choice.getText()) || TextUtils.isEmpty(shipping_choice.getText()) || TextUtils.isEmpty(payment_choice.getText())) {
            proceed_order.setEnabled(false);
        } else {
            proceed_order.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
