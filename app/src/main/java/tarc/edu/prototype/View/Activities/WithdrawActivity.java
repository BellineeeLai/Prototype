package tarc.edu.prototype.View.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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


public class WithdrawActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String FILE_NAME = "SmartDresser";

    private EditText withdrawAmount, accountNumber;
    private Button submit;
    private TextView available;
    private DatabaseReference ref;

    private String walletNode;

    FirebaseDatabase firebaseDatabase;

    private String userType, tempId, tempNode;
    private E_Wallet wallet;

    public WithdrawActivity() {
        walletNode = "E-Wallet";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        SharedPreferences sharedPreferences = this.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        userType = sharedPreferences.getString("user", null);

        //New Changes
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

        withdrawAmount = findViewById(R.id.editwithdrawAmount);
        accountNumber = findViewById(R.id.editAccNo);
        submit = findViewById(R.id.btnSubmit);
        available = findViewById(R.id.textAvailable);

        firebaseDatabase = FirebaseDatabase.getInstance();
        getAvailableBalance();

        submit.setOnClickListener(this);
    }

    private void getAvailableBalance() {

        ref = firebaseDatabase.getReference(tempNode).child(tempId).child(walletNode);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    wallet = dataSnapshot.getValue(E_Wallet.class);
                    assert wallet != null;
                    available.setText(getString(R.string.currency, wallet.getBalance()));
                } else {
                    Toast.makeText(getApplicationContext(), "Not enough balance to withdraw", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == submit) {
            String amount = withdrawAmount.getText().toString().trim();
            String accNo = accountNumber.getText().toString().trim();
            String activity = "Withdraw";
            Intent intent = new Intent(this, TransactionConfirmationActivity.class);
            intent.putExtra("amount", Double.parseDouble(amount));
            intent.putExtra("activity", activity);
            intent.putExtra("accNo", accNo);
            startActivity(intent);
            finish();
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
