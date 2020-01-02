package tarc.edu.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import tarc.edu.prototype.Model.User;
import tarc.edu.prototype.Model.UserOrder;
import tarc.edu.prototype.Model.UserOrderDetail;

public class JobAssignedActivity extends AppCompatActivity {
    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private String userType, tempId, tempNode,userOrderNode,status,incharge,orderNode,state,detailsNode;
    FirebaseUser user;
    private RecyclerView assignJob;
    private FirebaseRecyclerOptions<UserOrder> options;
    private FirebaseRecyclerAdapter<UserOrder, JobAssignViewHolder> adapter;


    public JobAssignedActivity(){
        userOrderNode = "UserOrderDetail";
        orderNode = "CustomerOrder";
        status = "TO RECEIVE";
        detailsNode = "Details";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_assigned);
        sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userType = sharedPreferences.getString("user", null);

        assignJob = findViewById(R.id.jobRecycler);
        assignJob.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        assignJob.setLayoutManager(layoutManager);

        assert userType != null;
        if (userType.equals("Customer")) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            tempId = user.getUid();
            tempNode = "Users";

        } else {
            tempId = sharedPreferences.getString("staffId", null);
            tempNode = "Staff";}

        displayStg();
    }

    private void check(){
        DatabaseReference uref = FirebaseDatabase.getInstance().getReference().child(tempNode).child(tempId).child(detailsNode);
        uref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
            //    incharge;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayStg() {
        DatabaseReference uoRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(userOrderNode);

      //  Query query = uoRef.orderByChild("status").equalTo("TO RECEIVE");
        options = new FirebaseRecyclerOptions.Builder<UserOrder>()
                .setQuery(uoRef, UserOrder.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<UserOrder, JobAssignViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull JobAssignViewHolder jobAssignViewHolder, int i, @NonNull UserOrder userOrder) {
                jobAssignViewHolder.sid.setText(userOrder.getOrderID());

                    jobAssignViewHolder.done.setImageResource(R.drawable.ic_tick);

                jobAssignViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), JobDetailsActivity.class);
                        intent.putExtra("Id",userOrder.getOrderID());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public JobAssignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_assigned, parent, false);
                return new JobAssignViewHolder(view);
            }

                };
        adapter.startListening();
        assignJob.setAdapter(adapter);
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

