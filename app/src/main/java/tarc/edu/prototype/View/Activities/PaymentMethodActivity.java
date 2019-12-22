package tarc.edu.prototype.View.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import tarc.edu.prototype.Model.E_Wallet;
import tarc.edu.prototype.R;


public class PaymentMethodActivity extends AppCompatActivity {
    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;

    private String walletNode;

    private E_Wallet eWallet;
    private FirebaseUser user;
    private String userType, tempId, tempNode;

    public PaymentMethodActivity() {
        walletNode = "E-Wallet";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        sharedPreferences = this.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        final double subtotal = getIntent().getDoubleExtra("subtotal", 0.00);

        userType = sharedPreferences.getString("user", null);

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

        final MaterialCardView cardView_wallet = findViewById(R.id.card_view_wallet);
        final MaterialCardView cardView_bank = findViewById(R.id.card_view_bank);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        final TextView wallet = findViewById(R.id.wallet_pay);
        final TextView balance = findViewById(R.id.balance);

        wallet.setText(getString(R.string.smart_pay));

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(tempNode).child(tempId).child(walletNode);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eWallet = dataSnapshot.getValue(E_Wallet.class);
                    assert eWallet != null;
                    if(eWallet.getBalance() < subtotal){
                        balance.setText(getString(R.string.insufficient_balance));
                        cardView_wallet.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final TextView bank = findViewById(R.id.bank_pay);
        final TextView card_number = findViewById(R.id.card_number);
        bank.setText(getString(R.string.credit_card));

        cardView_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("paymentMethod", wallet.getText());
                PaymentMethodActivity.this.setResult(Activity.RESULT_OK, intent);
                PaymentMethodActivity.this.finish();
            }
        });

        cardView_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("paymentMethod", card_number.getText());
                PaymentMethodActivity.this.setResult(Activity.RESULT_OK, intent);
                PaymentMethodActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
