package tarc.edu.prototype.View.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

import tarc.edu.prototype.Interface.OnClickListener;
import tarc.edu.prototype.Model.Product;
import tarc.edu.prototype.R;
import tarc.edu.prototype.ViewHolder.ProductViewHolder;


public class FilterFragment extends Fragment {

    private MaterialCardView filter_all_products;
    private MaterialCardView filter_shirts;
    private MaterialCardView filter_pants;
    private MaterialCardView filter_shoes;

    private OnClickListener callback;
    private String productNode;
    private String productNameNode;
    private DatabaseReference productsRef;
    private FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    public FilterFragment() {
        productNameNode = "productName";
        productNode = "Product";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_filter, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
        filter_all_products = view.findViewById(R.id.filter_all_products);
        filter_shirts = view.findViewById(R.id.filter_shirts);
        filter_pants = view.findViewById(R.id.filter_pants);
        filter_shoes = view.findViewById(R.id.filter_shoes);

        filter_all_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                ProductListFragment fragment = new ProductListFragment();
                FilterFragment.this.loadFragment(fragment);
            }
        });

        filter_shirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                ShirtsFragment fragment = new ShirtsFragment();
                FilterFragment.this.loadFragment(fragment);
            }
        });

        filter_pants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                PantsFragment fragment = new PantsFragment();
                FilterFragment.this.loadFragment(fragment);
            }
        });

        filter_shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                ShoesFragment fragment = new ShoesFragment();
                FilterFragment.this.loadFragment(fragment);
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
