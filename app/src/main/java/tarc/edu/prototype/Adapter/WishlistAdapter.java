package tarc.edu.prototype.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import tarc.edu.prototype.Model.Wishlist;
import tarc.edu.prototype.R;
import tarc.edu.prototype.ViewHolder.WishlistViewHolder;


public class WishlistAdapter extends FirebaseRecyclerAdapter<Wishlist, WishlistViewHolder> {

    private Context context;

    public WishlistAdapter(@NonNull FirebaseRecyclerOptions<Wishlist> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull WishlistViewHolder wishlistViewHolder, int i, @NonNull final Wishlist wishlist) {
        wishlistViewHolder.productName.setText(wishlist.getProductName());
        wishlistViewHolder.sellPrice.setText(context.getResources().getString(R.string.currency, Double.parseDouble(wishlist.getSellPrice())));
        Picasso.get().load(wishlist.getProductImage()).into(wishlistViewHolder.productImage);
        if (wishlist.getStock() == 0) {
            wishlistViewHolder.productStock.setTextColor(Color.RED);
            wishlistViewHolder.productStock.setText(context.getResources().getString(R.string.out_of_stock));
        } else if (wishlist.getStock() <= 10) {
            wishlistViewHolder.productStock.setTextColor(context.getColor(R.color.light_orange));
            wishlistViewHolder.productStock.setText(context.getResources().getString(R.string.limited_stock));
        } else {
            wishlistViewHolder.productStock.setTextColor(context.getColor(R.color.green));
            wishlistViewHolder.productStock.setText(context.getResources().getString(R.string.in_stock));
        }
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_layout, parent, false);
        return new WishlistViewHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getRef().removeValue();
    }
}
