package tarc.edu.prototype.View.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;


import tarc.edu.prototype.Adapter.WishlistAdapter;
import tarc.edu.prototype.Helper.SwipeHelper;
import tarc.edu.prototype.Interface.MyButtonClickListener;
import tarc.edu.prototype.Model.Wishlist;
import tarc.edu.prototype.R;

import static android.content.Context.MODE_PRIVATE;

public class WishlistFragment extends Fragment {
    private String wishlistNode;

    private WishlistAdapter adapter;

    private RecyclerView recyclerView;

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;

    private TextView empty_wishlist;
    private TextView login_wishlist;

    private String userType, tempId, tempNode;

    private DatabaseReference wishlistRef;

    //Constructor
    public WishlistFragment() {
        wishlistNode = "Wishlist";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

        login_wishlist = view.findViewById(R.id.login_wishlist);
        empty_wishlist = view.findViewById(R.id.empty_wishlist_text);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
        recyclerView = view.findViewById(R.id.wishlistRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (userType != null) {
            if (userType.equals("Customer")) {
                login_wishlist.setVisibility(View.INVISIBLE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                tempId = user.getUid();
                tempNode = "Users";


            } else {
                login_wishlist.setVisibility(View.INVISIBLE);
                tempId = sharedPreferences.getString("staffId", null);
                tempNode = "Staff";
            }
        } else {
            login_wishlist.setVisibility(View.VISIBLE);
        }

        wishlistRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(tempNode)
                .child(tempId)
                .child(wishlistNode);



        //updateWishlistStock();
        loadWishlist(view);
        //inStockNotification();
    }

    private void loadWishlist(final View view) {
        FirebaseRecyclerOptions<Wishlist> options = new FirebaseRecyclerOptions.Builder<Wishlist>()
                .setQuery(wishlistRef, Wishlist.class)
                .build();

        adapter = new WishlistAdapter(options, getContext());

        //Swipe menu
        @SuppressWarnings("unused")
        SwipeHelper swipeHelper = new SwipeHelper(getContext(), recyclerView, 200) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(Objects.requireNonNull(getContext()),
                        "Delete",
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

        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    empty_wishlist.setVisibility(View.VISIBLE);
                } else {
                    adapter.startListening();
                    recyclerView.setAdapter(adapter);
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
