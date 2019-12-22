package tarc.edu.prototype.View.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import tarc.edu.prototype.Interface.OnClickListener;
import tarc.edu.prototype.Model.Transaction;
import tarc.edu.prototype.R;

public class TransactionDetailsActivity extends AppCompatActivity {

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    TextView tamount1, ttype,tdetails,postTime,tstatus,tid;
    Button back;
    String trId,userType,tempId, tempNode;
    DatabaseReference tranRef;
    Transaction trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        sharedPreferences = this.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

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

        trId = Objects.requireNonNull(getIntent().getExtras().getString("tId"));
        tranRef = FirebaseDatabase.getInstance().getReference().child(tempNode).child(tempId).child("Transaction").child(trId);
        tamount1 =findViewById(R.id.tamount1);
        ttype = findViewById(R.id.transType);
        tdetails = findViewById(R.id.tranDetails);
        postTime = findViewById(R.id.postTime);
        tstatus = findViewById(R.id.tStatus);
        tid = findViewById(R.id.transId);
        back = findViewById(R.id.btnBack1);
        display();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void display() {
        this.setFinishOnTouchOutside(false);
        tranRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    trans = dataSnapshot.getValue(Transaction.class);
                    assert trans != null;
                    ttype.setText(trans.getTransactionDesc());
                    postTime.setText(trans.getDateTime());
                    tstatus.setText(trans.getStatus());
                    tid.setText(trId);

                    if(trans.getTransactionDesc().equals("Topup")){
                        tamount1.setText(getString(R.string.plus_amount, Double.valueOf(trans.getAmount())));
                        tamount1.setTextColor(Color.GREEN);
                        tdetails.setText(trans.getToWho());
                    }else if(trans.getTransactionDesc().equals("Withdraw")){
                        tamount1.setText(getString(R.string.minus_amount, Double.valueOf(trans.getAmount())));
                        tamount1.setTextColor(Color.RED);
                        tdetails.setText(trans.getToWho());
                    }else{
                        tamount1.setText(getString(R.string.minus_amount, Double.valueOf(trans.getAmount())));
                        tamount1.setTextColor(Color.RED);
                        tdetails.setText(trans.getToWho() + " " + trans.getOrderId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
