package tarc.edu.prototype;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import tarc.edu.prototype.Model.User;
import tarc.edu.prototype.Model.UserOrder;

public class DeliveryReportActivity extends AppCompatActivity {

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private String userType, tempId, tempNode;
    private TextView sname,sid;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private RecyclerView reportRecyclerView;
    private FirebaseRecyclerOptions<UserOrder> options;
    private FirebaseRecyclerAdapter<UserOrder, JobReportViewHolder> adapter;
    String customerOrderNode;

    public DeliveryReportActivity(){
        customerOrderNode = "UserOrderDetail";
    }


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
        reportRecyclerView = findViewById(R.id.reportRecyclerView);

        reportRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reportRecyclerView.setLayoutManager(layoutManager);

        loadStaff();
        loadReport();
    }

    private void loadReport() {
        DatabaseReference coref = FirebaseDatabase.getInstance().getReference().child(customerOrderNode);
        //Query query = coref.orderByChild("0");
        options = new FirebaseRecyclerOptions.Builder<UserOrder>().setQuery(coref,UserOrder.class).build();

        adapter = new FirebaseRecyclerAdapter<UserOrder, JobReportViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JobReportViewHolder jobReportViewHolder, int i, @NonNull UserOrder userOrder) {
                jobReportViewHolder.sstatus.setText("COMPLETE");
                jobReportViewHolder.sid.setText(userOrder.getOrderID());
                jobReportViewHolder.ddate.setText(userOrder.getDate());
            }

            @NonNull
            @Override
            public JobReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_report_details, parent, false);
                return new JobReportViewHolder(view);
            }
        };
        adapter.startListening();
        reportRecyclerView.setAdapter(adapter);
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
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
