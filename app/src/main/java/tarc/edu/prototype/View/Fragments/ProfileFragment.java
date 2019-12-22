package tarc.edu.prototype.View.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


import tarc.edu.prototype.GeneratePromoCode;
import tarc.edu.prototype.Model.User;
import tarc.edu.prototype.MyPurchaseActivity;
import tarc.edu.prototype.R;

import static android.content.Context.MODE_PRIVATE;

@SuppressWarnings("ALL")
public class ProfileFragment extends Fragment implements View.OnClickListener{
    //View
    private TextView textViewName,phistory;
    private Button btnSetUP, btnEWallet, btnTrackShipping, btnSetAddress, btnSignout;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;

    //Shared Preferences
    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private OnFragmentInteractionListener mListener;

    private String userType;
    private String staffId;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();

        textViewName = view.findViewById(R.id.Name);
        btnSetUP = view.findViewById(R.id.btnSetUp);
        btnEWallet = view.findViewById(R.id.btnEWallet);
        btnTrackShipping = view.findViewById(R.id.btnTrackShipping);
        btnSetAddress = view.findViewById(R.id.btnSetAddress);
        btnSignout = view.findViewById(R.id.btnSignOut);
        phistory = view.findViewById(R.id.phistory);



        userType = getArguments().getString("user", null);
        staffId = getArguments().getString("staffId", null);

        editor = sharedPreferences.edit();
        editor.putString("user", userType);
        editor.commit();

        if(userType.equals("Customer")){
            staffId = null;
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();
            CustomerProfile();
        }

        if(userType.equals("Staff")){
            editor = sharedPreferences.edit();
            editor.putString("staffId", staffId);
            editor.commit();
            StaffProfile();
        }

        return view;
    }

    private void StaffProfile() {
        displayName();
        btnSetUP.setOnClickListener(this);
        btnEWallet.setOnClickListener(this);
        btnTrackShipping.setOnClickListener(this);
        btnSetAddress.setOnClickListener(this);
        btnSignout.setOnClickListener(this);
        phistory.setOnClickListener(this);
    }

    private void CustomerProfile() {
        if (firebaseAuth.getCurrentUser() == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.replace(R.id.fragment_container, loginFragment);
            fragmentTransaction.commit();
        }

        displayName();

        btnSetUP.setOnClickListener(this);
        btnEWallet.setOnClickListener(this);
        btnTrackShipping.setVisibility(View.GONE);
        btnTrackShipping.setOnClickListener(this);
        btnSetAddress.setOnClickListener(this);
        btnSignout.setOnClickListener(this);
        phistory.setOnClickListener(this);
    }

    private void displayName() {
        if (userType.equals("Customer")) {
            ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Details");
        }

        if (userType.equals("Staff")) {
            ref = FirebaseDatabase.getInstance().getReference().child("Staff").child(staffId).child("Details");
        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User cuser = dataSnapshot.getValue(User.class);
                assert cuser != null;
                textViewName.setText(cuser.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnSetUP) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            UserPreferenceFragment userPreferenceFragment = new UserPreferenceFragment();
            fragmentTransaction.replace(R.id.fragment_container, userPreferenceFragment).addToBackStack(null);
            fragmentTransaction.commit();
        }
        if (v == btnEWallet) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            eWalletFragment eWalletFragment = new eWalletFragment();
            fragmentTransaction.replace(R.id.fragment_container, eWalletFragment).addToBackStack(null);
            fragmentTransaction.commit();
        }
        if (v == btnSetAddress) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            AddressFragment addressFragment = new AddressFragment();
            fragmentTransaction.replace(R.id.fragment_container, addressFragment).addToBackStack(null);
            fragmentTransaction.commit();
        }
        if (v == btnTrackShipping) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DeliveryServiceFragment deliveryServiceFragment = new DeliveryServiceFragment();
            fragmentTransaction.replace(R.id.fragment_container,deliveryServiceFragment).addToBackStack(null);
            fragmentTransaction.commit();
        }
        if (v == btnSignout) {
            //Sign out from Firebase
            if(userType.equals("Customer")){
                FirebaseAuth.getInstance().signOut();
            }

            //Clear the user ID from shared preferences
            editor.clear();
            editor.commit();
            //Redirect to LoginFragment
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.replace(R.id.fragment_container, loginFragment);
            fragmentTransaction.commit();
        }
        if(v == phistory){
            Intent intent = new Intent(getActivity(), MyPurchaseActivity.class);
            startActivity(intent);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
