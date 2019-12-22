package tarc.edu.prototype.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tarc.edu.prototype.Model.CardDetails;
import tarc.edu.prototype.R;


public class CreditCardActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText textCardNum, textExpiryDate, textCvv, editMinAmount;
    private Button btnSubmit;
    private CheckBox save;
    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        textCardNum = findViewById(R.id.editCardNum);
        textExpiryDate = findViewById(R.id.editExpiryDate);
        textCvv = findViewById(R.id.editCVV);
        btnSubmit = findViewById(R.id.btnSubmit);
        save = findViewById(R.id.checkSave);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        btnSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            if (save.isChecked()) {
                saveCardDetails();
            }

            String amount = editMinAmount.getText().toString().trim();
            String activity = "Topup";

            Intent intent = new Intent(this, TransactionConfirmationActivity.class);
            intent.putExtra("amount", Double.parseDouble(amount));
            intent.putExtra("activity", activity);
            startActivity(intent);
            finish();
        }
    }

    private void saveCardDetails() {
        String cardNumber = textCardNum.getText().toString().trim();
        String expiryDate = textExpiryDate.getText().toString().trim();
        String cvv = textCvv.getText().toString().trim();

        CardDetails cardDetails = new CardDetails(cardNumber, expiryDate, cvv);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        databaseReference.child(user.getUid()).child("Card Information").setValue(cardDetails);
        Toast.makeText(this, "Card Information saved.", Toast.LENGTH_LONG).show();
    }
}
