package tarc.edu.prototype;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import tarc.edu.prototype.Interface.OnClickListener;
import tarc.edu.prototype.Model.E_Wallet;
import tarc.edu.prototype.View.Fragments.CartFragment;
import tarc.edu.prototype.View.Fragments.DeliveryServiceFragment;
import tarc.edu.prototype.View.Fragments.FilterFragment;
import tarc.edu.prototype.View.Fragments.HomeFragment;
import tarc.edu.prototype.View.Fragments.LoginFragment;
import tarc.edu.prototype.View.Fragments.ProductDetailsFragment;
import tarc.edu.prototype.View.Fragments.WishlistFragment;

public class MainActivity extends AppCompatActivity implements
        OnClickListener,
        FilterFragment.OnFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        WishlistFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        CartFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        DeliveryServiceFragment.OnFragmentInteractionListener {

    private FirebaseUser user;
    private E_Wallet wallet;
    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public MainActivity() {
    }

    @SuppressLint("ApplySharedPref")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            loadData();
        } else {
            editor = sharedPreferences.edit();
            editor.putString("balance", "0.00");
            editor.commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        loadFragment(new HomeFragment());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        navView.setSelectedItemId(R.id.nav_home);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_search:
                fragment = new FilterFragment();
                break;

            case R.id.nav_bookmark:
                fragment = new WishlistFragment();
                break;

            case R.id.nav_home:
                fragment = new HomeFragment();
                break;

            case R.id.nav_account:
                fragment = new LoginFragment();
                break;
            case R.id.nav_cart:
                fragment = new CartFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    private void loadData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("E-Wallet");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    wallet = dataSnapshot.getValue(E_Wallet.class);
                    assert wallet != null;
                    editor = sharedPreferences.edit();
                    editor.putString("user", "Customer");
                    editor.putString("balance", String.valueOf(wallet.getBalance()));
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(String productID) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putString("productID", productID);
        fragment.setArguments(args);
        loadFragment(fragment);
    }
}
