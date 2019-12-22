package tarc.edu.prototype;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import tarc.edu.prototype.Model.UserOrder;

public class OrderDetailsActivity extends AppCompatActivity {

    private String UserOrderNode;

    private FirebaseRecyclerOptions<UserOrder> options;
    private FirebaseRecyclerAdapter<UserOrder, OrderDetailsViewHolder> adapter;

    private RecyclerView details_list;

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;

    private FirebaseUser user;
    private String userType, tempId, tempNode,id;

    public OrderDetailsActivity() {
        UserOrderNode = "UserOrder";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        id = getIntent().getStringExtra("Id");
        sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        details_list = findViewById(R.id.orderDetailsrv);
        details_list.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        details_list.setLayoutManager(layoutManager);

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
        DatabaseReference uoRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(UserOrderNode);

        Query query = uoRef.orderByChild("orderID").equalTo(id);

        options = new FirebaseRecyclerOptions.Builder<UserOrder>()
                .setQuery(query, UserOrder.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<UserOrder, OrderDetailsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderDetailsViewHolder uoViewHolder, int i, @NonNull final UserOrder userOrder) {
                uoViewHolder.oid.setText(userOrder.getOrderID());
                uoViewHolder.pPrice.setText(getResources().getString(R.string.currency, Double.valueOf(userOrder.getSellPrice())));
            }

            @NonNull
            @Override
            public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details, parent, false);
                return new OrderDetailsViewHolder(view);
            }
        };
        adapter.startListening();
        details_list.setAdapter(adapter);
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
