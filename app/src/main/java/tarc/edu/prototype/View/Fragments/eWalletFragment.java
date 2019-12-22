package tarc.edu.prototype.View.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


import tarc.edu.prototype.Adapter.TransactionAdapter;
import tarc.edu.prototype.Model.E_Wallet;
import tarc.edu.prototype.Model.Transaction;
import tarc.edu.prototype.R;
import tarc.edu.prototype.View.Activities.TopupActivity;
import tarc.edu.prototype.View.Activities.WithdrawActivity;

import static android.content.Context.MODE_PRIVATE;

public class eWalletFragment extends Fragment implements View.OnClickListener {

    private TextView textViewAmount;
    private Button btnTopup, btnWithdraw;

    private FirebaseUser user;

    private E_Wallet wallet;
    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String userType, tempId, tempNode;

    private TransactionAdapter transAdap;
    private RecyclerView trans_list;

    private String walletNode;
    private String transNode;

    public eWalletFragment() {
        walletNode = "E-Wallet";
        transNode = "Transaction";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_e_wallet, container, false);

        //Attach to context
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.attach(this);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        sharedPreferences = this.getActivity().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        textViewAmount = view.findViewById(R.id.textViewAmount);
        btnTopup = view.findViewById(R.id.buttonTopUp);
        btnWithdraw = view.findViewById(R.id.buttonWithdraw);

        trans_list = view.findViewById(R.id.transactionRecycleView);
        trans_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        trans_list.setLayoutManager(layoutManager);

        btnTopup.setOnClickListener(this);
        btnWithdraw.setOnClickListener(this);
        loadData();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnTopup) {
            startActivity(new Intent(getActivity(), TopupActivity.class));
        }
        if (v == btnWithdraw) {
            startActivity(new Intent(getActivity(), WithdrawActivity.class));
        }
    }

    private void loadData() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(tempNode).child(tempId).child(walletNode);

        final DatabaseReference transRef = FirebaseDatabase.getInstance().getReference().child(tempNode).child(tempId).child(transNode);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    wallet = dataSnapshot.getValue(E_Wallet.class);
                    assert wallet != null;
                    textViewAmount.setText(getResources().getString(R.string.currency, wallet.getBalance()));
                } else {
                    textViewAmount.setText(R.string.rm0_00);
                    E_Wallet initWallet = new E_Wallet(0.00);
                    reference.setValue(initWallet);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<Transaction> options = new FirebaseRecyclerOptions.Builder<Transaction>()
                .setQuery(transRef, Transaction.class)
                .build();

        transAdap = new TransactionAdapter(options, getContext());
        transAdap.startListening();
        trans_list.setAdapter(transAdap);
    }

    public interface OnFragmentInteractionListener {

    }

    @Override
    public void onStart() {
        super.onStart();
        transAdap.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}