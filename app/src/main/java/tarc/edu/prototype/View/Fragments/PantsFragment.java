package tarc.edu.prototype.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import tarc.edu.prototype.Interface.OnClickListener;
import tarc.edu.prototype.Model.Product;
import tarc.edu.prototype.R;
import tarc.edu.prototype.ViewHolder.ProductViewHolder;

public class PantsFragment extends Fragment {

    private OnClickListener callback;
    private String productNode;
    private String productNameNode;
    private DatabaseReference productsRef;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;


    public PantsFragment() {
        // Required empty public constructor
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
        final View view = inflater.inflate(R.layout.fragment_pants, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        productsRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(productNode);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Query query = productsRef.orderByChild("category").equalTo("Pants");

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();

        adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Product product) {
                        productViewHolder.productName.setText(product.getProductName());
                        productViewHolder.sellPrice.setText(getString(R.string.currency, Double.parseDouble(product.getSellPrice())));
                        Picasso.get().load(product.getProductImage()).into(productViewHolder.productImage);
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callback.onClick(product.getProductID());
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_layout, parent, false);
                        return new ProductViewHolder(view);
                    }
                };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (OnClickListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+ " must implement OnImageClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        /*adapter.stopListening();*/
    }

    public interface OnFragmentInteractionListener {
    }
}
