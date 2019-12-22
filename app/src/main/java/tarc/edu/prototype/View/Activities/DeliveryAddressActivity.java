package tarc.edu.prototype.View.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import tarc.edu.prototype.Model.Address;
import tarc.edu.prototype.R;
import tarc.edu.prototype.ViewHolder.AddressViewHolder;

public class DeliveryAddressActivity extends AppCompatActivity {

    private String addressNode;

    private FirebaseRecyclerOptions<Address> options;
    private FirebaseRecyclerAdapter<Address, AddressViewHolder> adapter;

    private RecyclerView address_list;

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;

    private FirebaseUser user;
    private String userType, tempId, tempNode;

    public DeliveryAddressActivity() {
        addressNode = "Address";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        address_list = findViewById(R.id.address_list);
        address_list.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        address_list.setLayoutManager(layoutManager);

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

        loadAddress();
    }

    public void loadAddress() {
        DatabaseReference addressRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(tempNode)
                .child(tempId)
                .child(addressNode);

        options = new FirebaseRecyclerOptions.Builder<Address>()
                .setQuery(addressRef, Address.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Address, AddressViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AddressViewHolder addressViewHolder, int i, @NonNull final Address address) {
                addressViewHolder.receiverAddress.setText(address.getAddress());
                addressViewHolder.phoneNo.setText(address.getPhoneNo());
                addressViewHolder.receiverName.setText(address.getReceiverName());
                addressViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("address", address);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
            }

            @NonNull
            @Override
            public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_layout, parent, false);
                return new AddressViewHolder(view);
            }
        };
        adapter.startListening();
        address_list.setAdapter(adapter);
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
