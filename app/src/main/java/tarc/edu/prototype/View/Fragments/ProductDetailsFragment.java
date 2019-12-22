package tarc.edu.prototype.View.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import tarc.edu.prototype.Model.Cart;
import tarc.edu.prototype.Model.Product;
import tarc.edu.prototype.Model.Wishlist;
import tarc.edu.prototype.R;
import tarc.edu.prototype.View.Activities.PictureActivity;

import static android.content.Context.MODE_PRIVATE;

public class ProductDetailsFragment extends Fragment {

    private static final String FILE_NAME = "SmartDresser";
    private String userNode;
    private String wishlistNode;
    private String cartNode;
    private String productNode;

    private final static String DATA_RECEIVE = "productID";
    private String productID;
    private final HashMap<String, Object> cartMap = new HashMap<>();
    private ImageView productImageUrl;
    private TextView productName, sellPrice, availability, productDesc;
    private static int default_quantity = 0;
    private Product product = null;

    private MaterialButton btnAddCart;
    private MaterialButton btnTry;
    private MaterialButton btnWishlist;

    private String userType, tempId;
    private String tempNode;

    public ProductDetailsFragment() {
        // Required empty public constructor
        userNode = "Users";
        wishlistNode = "Wishlist";
        cartNode = "Cart";
        productNode = "Product";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.attach(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        //Attach to context
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.attach(this);

        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        userType = sharedPreferences.getString("user", null);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        productImageUrl = view.findViewById(R.id.productImage);
        productName = view.findViewById(R.id.productName);
        sellPrice = view.findViewById(R.id.sellPrice);
        availability = view.findViewById(R.id.availability);
        productDesc = view.findViewById(R.id.productDesc);

        btnTry = view.findViewById(R.id.btnTry);
        btnAddCart = view.findViewById(R.id.btnAddCart);
        btnWishlist = view.findViewById(R.id.btnWishlist);

        Bundle args = getArguments();
        if (args != null) {
            productID = args.getString(DATA_RECEIVE);
        }

        if(userType != null){
            if(userType.equals("Customer")){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                tempId = user.getUid();
                tempNode = "Users";
            } else {
                tempId = sharedPreferences.getString("staffId", null);
                tempNode = "Staff";
            }
        } else {
            tempId = "";
        }

        getProductDetails(productID);
        initWishlistBtn(tempId, tempNode, productID);

        btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (tempId.isEmpty()) {
                    final Snackbar snackbar = Snackbar.make(view1, "Login to have your own wishlist.", Snackbar.LENGTH_SHORT);
                    snackbar.setAction("Got it.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view11) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                } else {
                    ProductDetailsFragment.this.addToWishList(view1, tempId, tempNode);
                }
            }
        });

        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PictureActivity.class);
                intent.putExtra("productID", productID);
                startActivity(intent);
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempId.isEmpty()) {
                    final Snackbar snackbar = Snackbar.make(v, "Login to start shopping.", Snackbar.LENGTH_SHORT);
                    snackbar.setAction("Got it.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view12) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                } else {
                    ProductDetailsFragment.this.addToCartList(tempId, tempNode);
                    final Snackbar snackbar = Snackbar.make(v, R.string.notify_added_cart, Snackbar.LENGTH_SHORT);
                    snackbar.setAction("Got it.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view12) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
            }
        });

        return view;
    }

    //Add product into wishlist
    private void addToWishList(final View view, String id, String node) {

        final DatabaseReference wishlistRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(node)
                .child(id)
                .child(wishlistNode);

        wishlistRef.child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Wishlist wishlist = new Wishlist(productID, (String) productName.getText(), product.getProductImage(),
                            product.getCategory(), product.getSellPrice(), product.getStock());
                    cartMap.put(productID, wishlist);
                    wishlistRef.updateChildren(cartMap);
                    final Snackbar snackbar = Snackbar.make(view, R.string.notify_added_wishlist, Snackbar.LENGTH_SHORT);
                    snackbar.setAction("Got it.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view1) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    cartMap.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Add product into cart
    private void addToCartList(final String tempId, String node) {
        DatabaseReference tempCartRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(node)
                .child(tempId)
                .child(cartNode);

        final DatabaseReference cartRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(node);

        tempCartRef.child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    default_quantity = 1;
                    cartMap.put("productID", productID);
                    cartMap.put("productName", productName.getText());
                    cartMap.put("productImage", product.getProductImage());
                    cartMap.put("itemsPrice", product.getSellPrice());
                    cartMap.put("sellPrice", product.getSellPrice());
                    cartMap.put("categoryID", product.getCategory());
                    cartMap.put("productQuantity", Integer.toString(default_quantity));
                } else {
                    Cart tempCart = dataSnapshot.getValue(Cart.class);
                    default_quantity = Integer.parseInt(Objects.requireNonNull(tempCart).getProductQuantity());
                    default_quantity++;
                    String price = "" + Double.parseDouble(product.getSellPrice()) * default_quantity;
                    cartMap.put("itemsPrice", price);
                    cartMap.put("productQuantity", Integer.toString(default_quantity));
                }
                cartRef.child(tempId).child(cartNode).child(productID).updateChildren(cartMap);
                cartMap.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Load product details
    private void getProductDetails(String tempId) {
        DatabaseReference productRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(productNode);

        productRef.child(tempId).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    product = dataSnapshot.getValue(Product.class);
                    productName.setText(Objects.requireNonNull(product).getProductName());
                    sellPrice.setText(getResources().getString(R.string.currency, Double.parseDouble(product.getSellPrice())));
                    Picasso.get().load(product.getProductImage()).into(productImageUrl);
                    productDesc.setText(product.getDescription());
                    if (product.getStock() <= 0) {
                        btnAddCart.setEnabled(false);
                        availability.setText(getResources().getString(R.string.out_of_stock));
                    } else if (product.getStock() <= 10) {
                        btnAddCart.setEnabled(true);
                        availability.setText(getResources().getString(R.string.limited_stock));
                    } else {
                        btnAddCart.setEnabled(true);
                        availability.setText(getResources().getString(R.string.in_stock));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initWishlistBtn(String tempId, String node, final String productId){
        final DatabaseReference wishlistRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(node)
                .child(tempId)
                .child(wishlistNode);

        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Wishlist wishlist = ds.getValue(Wishlist.class);
                        assert wishlist != null;
                        if(wishlist.getProductID().equals(productId)){
                            btnWishlist.setEnabled(false);
                            btnWishlist.setText(getResources().getString(R.string.already_in_wish));
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
