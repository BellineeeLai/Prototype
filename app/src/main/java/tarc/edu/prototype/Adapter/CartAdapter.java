package tarc.edu.prototype.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import tarc.edu.prototype.R;
import tarc.edu.prototype.ViewHolder.CartViewHolder;

import static android.content.Context.MODE_PRIVATE;


public class CartAdapter extends FirebaseRecyclerAdapter<Cart, CartViewHolder> {
    private Context context;

    private String tempId, tempNode;

    public CartAdapter(@NonNull FirebaseRecyclerOptions<Cart> options, Context context, String tempId, String tempNode) {
        super(options);
        this.context = context;
        this.tempId = tempId;
        this.tempNode = tempNode;
    }

    @Override
    protected void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
        cartViewHolder.productName.setText(cart.getProductName());
        cartViewHolder.productQuantity.setText(context.getResources().getString(R.string.qty, cart.getProductQuantity()));
        cartViewHolder.sellPrice.setText(context.getResources().getString(R.string.currency, Double.parseDouble(cart.getItemsPrice())));
        Picasso.get().load(cart.getProductImage()).into(cartViewHolder.productImage);

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Product");

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Product product = ds.getValue(Product.class);
                        assert product != null;
                        if(product.getProductID().equals(cart.getProductID())){

                            if(product.getStock() <= 5 && product.getStock() != 0){
                                cartViewHolder.edit_qty.setRange(1, product.getStock());
                                cartViewHolder.leftover.setTextColor(ContextCompat.getColor(context, R.color.light_orange));
                                cartViewHolder.leftover.setText(context.getResources().getString(
                                        R.string.text_leftover, product.getStock()
                                ));
                            }
                            if(product.getStock() == 0){
                                cartViewHolder.edit_qty.setRange(0, product.getStock());
                                cartViewHolder.leftover.setTextColor(ContextCompat.getColor(context, R.color.red));
                                cartViewHolder.leftover.setText(R.string.remove_from_cart);
                            } else {
                                cartViewHolder.edit_qty.setRange(1, product.getStock());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cartViewHolder.edit_qty.setNumber(cart.getProductQuantity());
        cartViewHolder.edit_qty.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                final HashMap<String, Object> cartMap = new HashMap<>();

                cartViewHolder.productQuantity.setText(context.getResources().getString(R.string.qty, String.valueOf(newValue)));
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(tempNode).child(tempId).child("Cart").child(cart.getProductID());

                String price = "" + Double.parseDouble(cart.getSellPrice()) * newValue;
                cartMap.put("itemsPrice", price);
                cartMap.put("productQuantity", String.valueOf(newValue));
                reference.updateChildren(cartMap);
            }
        });
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);

        return new CartViewHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getRef().removeValue();
    }
}
