package tarc.edu.prototype.View.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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

import java.util.HashMap;
import java.util.Objects;

import tarc.edu.prototype.Adapter.ViewPagerAdapter;
import tarc.edu.prototype.Interface.OnClickListener;
import tarc.edu.prototype.Model.Product;
import tarc.edu.prototype.Model.User;
import tarc.edu.prototype.Model.Wishlist;
import tarc.edu.prototype.R;
import tarc.edu.prototype.ViewHolder.ProductViewHolder;

import static android.content.Context.MODE_PRIVATE;
import static tarc.edu.prototype.SendNotification.CHANNEL_ID;


public class HomeFragment extends Fragment implements OnClickListener {
    private OnClickListener callback;

    private String productNode, wishlistNode;
    private int dotCount;
    private ImageView[] dots;

    private FirebaseRecyclerOptions<Product> options;
    private FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    private FirebaseRecyclerOptions<Product> options_prefer;
    private FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter_prefer;

    private RecyclerView latestProducts, preferredProducts;

    private String tempId, tempNode, userType;
    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;

    private String prefer;
    private DatabaseReference userRef;
    private Query query_prefer;

    private DatabaseReference wishlistRef, productRef;
    private String tempPreferNode;

    public HomeFragment() {
        productNode = "Product";
        wishlistNode = "Wishlist";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //Hides the toolbar
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();

        //Latest products
        RecyclerView.LayoutManager layoutManager_latest = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        latestProducts = view.findViewById(R.id.latestProducts);
        latestProducts.setHasFixedSize(false);
        latestProducts.setLayoutManager(layoutManager_latest);

        //Preferred products
        RecyclerView.LayoutManager layoutManager_preferred = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        preferredProducts = view.findViewById(R.id.preferredProducts);
        preferredProducts.setHasFixedSize(false);
        preferredProducts.setLayoutManager(layoutManager_preferred);

        //For image ViewPager
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        LinearLayout dotPanel = view.findViewById(R.id.SliderDots);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity());
        viewPager.setAdapter(viewPagerAdapter);
        dotCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotCount];
        for (int i = 0; i < dotCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 8, 8, 0);

            dotPanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

        if (userType != null) {
            if (userType.equals("Customer")) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                tempId = user.getUid();
                tempNode = "Users";
                getPreferences();
            } else {
                tempId = sharedPreferences.getString("staffId", null);
                tempNode = "Staff";
                getPreferences();
            }
        } else {
            query_prefer = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(productNode).limitToFirst(9);
        }
        loadLatestProduct();
    }

    private void getPreferences() {
        userRef = FirebaseDatabase.getInstance().getReference(tempNode).child(tempId).child("Details");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    prefer = user.getPreferences();
                    if (prefer == null) {
                        query_prefer = FirebaseDatabase
                                .getInstance()
                                .getReference()
                                .child(productNode).limitToFirst(9);
                        loadPreferredProduct();
                    } else {
                        if(prefer.equals("Puma") || prefer.equals("Adidas") | prefer.equals("Nike")){
                            tempPreferNode = "brand";
                        } else {
                            tempPreferNode = "category";
                        }
                        query_prefer = FirebaseDatabase
                                .getInstance()
                                .getReference()
                                .child(productNode).orderByChild(tempPreferNode).equalTo(prefer).limitToLast(9);
                        productRef = FirebaseDatabase
                                .getInstance()
                                .getReference()
                                .child(productNode);

                        wishlistRef = FirebaseDatabase
                                .getInstance()
                                .getReference()
                                .child(tempNode)
                                .child(tempId)
                                .child(wishlistNode);
                        loadPreferredProduct();
                        updateWishlistStock();
                        inStockNotification();
                    }
                } else {
                    query_prefer = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(productNode).limitToFirst(9);
                    loadPreferredProduct();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPreferredProduct() {
        options_prefer = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query_prefer, Product.class)
                .build();

        adapter_prefer = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options_prefer) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, final int i, @NonNull final Product product) {
                Picasso.get().load(product.getProductImage()).into(productViewHolder.productImage);
                productViewHolder.productName.setText(product.getProductName());
                productViewHolder.sellPrice.setText(getString(R.string.currency, Double.parseDouble(product.getSellPrice())));
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Passing value to ProductDetailsFragment
                        callback.onClick(getSnapshots().getSnapshot(i).getKey());
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.latest_products, parent, false);

                return new ProductViewHolder(view);
            }
        };

        adapter_prefer.startListening();
        preferredProducts.setAdapter(adapter_prefer);
    }

    private void loadLatestProduct() {
        DatabaseReference productRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(productNode);

        Query query = productRef.orderByValue().limitToLast(9);

        options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, final int i, @NonNull final Product product) {
                Picasso.get().load(product.getProductImage()).into(productViewHolder.productImage);
                productViewHolder.productName.setText(product.getProductName());
                productViewHolder.sellPrice.setText(getString(R.string.currency, Double.parseDouble(product.getSellPrice())));
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Passing value to ProductDetailsFragment
                        callback.onClick(getSnapshots().getSnapshot(i).getKey());
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.latest_products, parent, false);

                return new ProductViewHolder(view);
            }
        };

        adapter.startListening();
        latestProducts.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnImageClickListener");
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
        adapter.stopListening();
    }

    @Override
    public void onClick(String productID) {

    }

    public interface OnFragmentInteractionListener {
    }

    private void inStockNotification() {
        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Object> map = new HashMap<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final Wishlist wishlist = ds.getValue(Wishlist.class);
                        assert wishlist != null;
                        final int initial_stock = wishlist.getStock();
                        productRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Product product = ds.getValue(Product.class);
                                        assert product != null;
                                        if (product.getProductID().equals(wishlist.getProductID())) {
                                            if (initial_stock == 0) {
                                                if (product.getStock() != 0) {
                                                    NotificationCompat.Builder builder =
                                                            new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                                                    .setSmallIcon(R.drawable.baseline_notification_important_black_24dp)
                                                                    .setContentTitle("Back In Stock!")
                                                                    .setContentText(product.getProductName() + " is back in stock!")
                                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);
                                                    NotificationManagerCompat notificationManagerCompat
                                                            = NotificationManagerCompat.from(getActivity());

                                                    notificationManagerCompat.notify(1, builder.build());

                                                    map.put("stock", product.getStock());
                                                    wishlistRef.child(wishlist.getProductID()).updateChildren(map);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateWishlistStock() {
        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Object> map = new HashMap<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final Wishlist wishlist = ds.getValue(Wishlist.class);
                        assert wishlist != null;
                        productRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Product product = ds.getValue(Product.class);
                                        assert product != null;
                                        if (product.getProductID().equals(wishlist.getProductID())) {
                                            map.put("stock", product.getStock());
                                            wishlistRef.child(wishlist.getProductID()).updateChildren(map);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
