package tarc.edu.prototype.View.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;
import tarc.edu.prototype.Adapter.AddressAdapter;
import tarc.edu.prototype.Helper.SwipeHelper;
import tarc.edu.prototype.Interface.MyButtonClickListener;
import tarc.edu.prototype.Model.Address;
import tarc.edu.prototype.R;
import tarc.edu.prototype.View.Activities.SetAddressActivity;

import static android.content.Context.MODE_PRIVATE;


public class AddressFragment extends Fragment {
    private static final String FILE_NAME = "SmartDresser";

    private String userNode;
    private String addressNode;
    private String staffId, userType, staffNode;
    private DatabaseReference addressRef;
    private AddressAdapter adapter;
    private FirebaseUser user;

    private RecyclerView address_list;

    public AddressFragment() {
        addressNode = "Address";
        userNode = "Users";
        staffNode = "Staff";
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        assert getArguments() != null;
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

        assert userType != null;
        if(userType.equals("Staff")){
            staffId = sharedPreferences.getString("staffId", null);
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        address_list = view.findViewById(R.id.addressRecycleView);
        Button btnSetAddress = view.findViewById(R.id.btnAddAddress);
        address_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        address_list.setLayoutManager(layoutManager);

        loadAddress();

        btnSetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressFragment.this.getActivity(), SetAddressActivity.class);
                intent.putExtra("user", userType);
                intent.putExtra("staffId", staffId);
                AddressFragment.this.startActivity(intent);
            }
        });

        return view;
    }

    private void loadAddress() {
        if(userType.equals("Customer")) {
            addressRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(userNode)
                    .child(user.getUid())
                    .child(addressNode);
        }

        if(userType.equals("Staff")) {
            addressRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(staffNode)
                    .child(staffId)
                    .child(addressNode);
        }

        FirebaseRecyclerOptions<Address> options = new FirebaseRecyclerOptions.Builder<Address>()
                .setQuery(addressRef, Address.class)
                .build();

        adapter = new AddressAdapter(options, getContext());

        @SuppressWarnings("unused")
        SwipeHelper swipeHelper = new SwipeHelper(getContext(), address_list, 200) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(Objects.requireNonNull(getContext()),
                        "Remove",
                        30,
                        0,
                        Color.parseColor("#FF3C30"),
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                adapter.deleteItem(pos);
                                adapter.notifyItemRemoved(pos);
                            }
                        }));
            }
        };

        adapter.startListening();
        address_list.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

}