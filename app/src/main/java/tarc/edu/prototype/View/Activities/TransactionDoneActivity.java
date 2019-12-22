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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import tarc.edu.prototype.Model.Cart;
import tarc.edu.prototype.Model.E_Wallet;
import tarc.edu.prototype.Model.Transaction;
import tarc.edu.prototype.Model.UserOrder;
import tarc.edu.prototype.Model.UserOrderDetail;
import tarc.edu.prototype.R;

public class TransactionDoneActivity extends AppCompatActivity {
    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;

    private TextView tStatus, tID, tTo, tDesc, tAmount, tBalanceBefore, tBalanceAfter, tDateTime;
    FirebaseDatabase database;
    DatabaseReference transaction, walletRef;

    String status, tid, to, desc, orderId;

    double amount;
    final Date date = Calendar.getInstance().getTime();
    @SuppressLint("SimpleDateFormat")
    final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
    final String fd = df.format(date);
    Transaction trans;
    E_Wallet wallet, tempWallet;

    private String userType, tempId, tempNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_done);
        sharedPreferences = this.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);
        database = FirebaseDatabase.getInstance();

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

        walletRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(tempNode)
                .child(tempId)
                .child("E-Wallet");

        tStatus = findViewById(R.id.status);
        tID = findViewById(R.id.transactionID);
        tTo = findViewById(R.id.To);
        tDesc = findViewById(R.id.transDesc);
        tAmount = findViewById(R.id.transAmount);
        tBalanceBefore = findViewById(R.id.balanceBefore);
        tBalanceAfter = findViewById(R.id.balanceAfter);
        tDateTime = findViewById(R.id.transDateTime);
        Button bBack = findViewById(R.id.btnBack);

        amount = getIntent().getDoubleExtra("transactionAmount", 0.00);
        status = Objects.requireNonNull(getIntent().getExtras()).getString("status");
        tid = getIntent().getExtras().getString("id");
        to = getIntent().getExtras().getString("to");
        desc = getIntent().getExtras().getString("desc");
        orderId = getIntent().getStringExtra("orderId");

        transaction = database.getReference("Transaction");

        displayTransaction();

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionDoneActivity.this.saveTransaction();
                finish();
            }
        });
    }

    private void saveTransaction() {
        final DatabaseReference transRef = FirebaseDatabase.getInstance().getReference().child(tempNode).child(tempId).child("Transaction");

        transaction.child(tid).setValue(trans);

        transRef.child(tid).setValue(trans);

        walletRef.setValue(tempWallet);
    }

    private void displayTransaction() {
        tStatus.setText(status);
        tID.setText(tid);
        tAmount.setText(getString(R.string.currency, amount));
        tTo.setText(to);
        if (desc.equals("Payment")) {
            tDesc.setText(desc + orderId);
        } else {
            tDesc.setText(desc);
        }

        tDateTime.setText(fd);

        final double amount1 = amount;

        walletRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    wallet = dataSnapshot.getValue(E_Wallet.class);
                    assert wallet != null;
                    tBalanceBefore.setText(getString(R.string.currency, wallet.getBalance()));

                    if (desc.equals("Topup")) {
                        tBalanceAfter.setText(getString(R.string.currency, wallet.getBalance() + amount1));
                        trans = new Transaction(tid, to, desc, tDateTime.getText().toString(), String.valueOf(wallet.getBalance() + amount1), String.valueOf(amount), status, orderId);
                        tempWallet = new E_Wallet(wallet.getBalance() + amount1);
                    } else if (desc.equals("Withdraw")) {
                        tBalanceAfter.setText(getString(R.string.currency, wallet.getBalance() - amount1));
                        trans = new Transaction(tid, to, desc, tDateTime.getText().toString(), String.valueOf(wallet.getBalance() - amount1), String.valueOf(amount), status, orderId);
                        tempWallet = new E_Wallet(wallet.getBalance() - amount1);
                    } else {
                        tBalanceAfter.setText(getString(R.string.currency, wallet.getBalance() - amount1));
                        trans = new Transaction(tid, to, desc, tDateTime.getText().toString(), String.valueOf(wallet.getBalance() - amount1), String.valueOf(amount), status, orderId);
                        tempWallet = new E_Wallet(wallet.getBalance() - amount1);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
