package tarc.edu.prototype.View.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import tarc.edu.prototype.Adapter.CartAdapter;
import tarc.edu.prototype.Helper.SwipeHelper;
import tarc.edu.prototype.Interface.MyButtonClickListener;
import tarc.edu.prototype.Model.Cart;
import tarc.edu.prototype.Model.Product;
import tarc.edu.prototype.R;
import tarc.edu.prototype.View.Activities.PaymentReviewActivity;

import static android.content.Context.MODE_PRIVATE;


public class CartFragment extends Fragment {
    private double subtotal_data;
    private String cartNode;

    private TextView empty_cart;
    private TextView login_cart;
    private MaterialCardView checkout_view;

    private RecyclerView cartList;

    private TextView subtotal;

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;

    private DatabaseReference cartRef;

    private CartAdapter adapter;

    private String userType, tempId, tempNode;

    private MaterialButton btn_checkout;

    public CartFragment() {
        cartNode = "Cart";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

        subtotal = view.findViewById(R.id.subtotal);
        btn_checkout = view.findViewById(R.id.checkout);
        empty_cart = view.findViewById(R.id.empty_cart_text);
        login_cart = view.findViewById(R.id.login_cart);
        checkout_view = view.findViewById(R.id.checkout_view);
        cartList = view.findViewById(R.id.cart_list);

        //Hides the toolbar
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();

        cartList.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cartList.setLayoutManager(layoutManager);

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartFragment.this.getContext(), PaymentReviewActivity.class);
                intent.putExtra("subtotal", subtotal_data);
                CartFragment.this.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (userType != null) {
            if (userType.equals("Customer")) {
                login_cart.setVisibility(View.INVISIBLE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                tempId = user.getUid();
                tempNode = "Users";
                loadData(view);
            } else {
                login_cart.setVisibility(View.INVISIBLE);
                tempId = sharedPreferences.getString("staffId", null);
                tempNode = "Staff";
                loadData(view);
            }
        } else {
            login_cart.setVisibility(View.VISIBLE);
            checkout_view.setVisibility(View.INVISIBLE);
        }
    }

    private void loadData(final View view) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Product");

        cartRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(tempNode)
                .child(tempId)
                .child(cartNode);

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartRef, Cart.class)
                .build();

        adapter = new CartAdapter(options, getContext(), tempId, tempNode);

        //Swipe menu
        @SuppressWarnings("unused")
        SwipeHelper swipeHelper = new SwipeHelper(getContext(), cartList, 200) {
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
                                final Snackbar snackbar = Snackbar.make(view, "Item removed.", Snackbar.LENGTH_SHORT);
                                snackbar.setAction("Got it.", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view1) {
                                        snackbar.dismiss();
                                    }
                                });
                                snackbar.show();
                                adapter.deleteItem(pos);
                                adapter.notifyItemRemoved(pos);
                            }
                        }));
            }
        };

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final Product product = ds.getValue(Product.class);
                        assert product != null;
                        cartRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        Cart cart = dataSnapshot1.getValue(Cart.class);
                                        if (product.getProductID().equals(cart.getProductID())) {
                                            if (product.getStock() == 0){
                                                btn_checkout.setEnabled(false);
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

        updateCart();

        cartRef.child("productQuantity").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateCart();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateCart() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    empty_cart.setVisibility(View.VISIBLE);
                    checkout_view.setVisibility(View.INVISIBLE);

                } else {
                    double[] tempSubtotal = {0.00};
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Cart cartItem = ds.getValue(Cart.class);
                        assert cartItem != null;
                        tempSubtotal[0] += Double.parseDouble(cartItem.getItemsPrice());
                        subtotal_data = tempSubtotal[0];
                    }
                    subtotal.setText(getString(R.string.currency, tempSubtotal[0]));
                    checkout_view.setVisibility(View.VISIBLE);
                    adapter.startListening();
                    cartList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (userType != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
    }
}