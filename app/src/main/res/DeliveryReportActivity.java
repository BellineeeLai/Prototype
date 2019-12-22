package tarc.edu.prototype;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tarc.edu.prototype.Model.User;

public class DeliveryReportActivity extends AppCompatActivity {

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private String userType, tempId, tempNode;
    private TextView sname,sid;
    private FirebaseDatabase db;
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_report);
        sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        sname = findViewById(R.id.Name);
        sid = findViewById(R.id.id);
        db = FirebaseDatabase.getInstance();
        userType = sharedPreferences.getString("user", null);
        assert userType != null;
        tempId = sharedPreferences.getString("staffId", null);
        tempNode = "Staff";

        loadStaff();
    }

    private void loadStaff() {
        sid.setText(tempId);
        ref = db.getReference().child(tempNode).child(tempId).child("Details");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                assert u != null;
                sname.setText(u.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
