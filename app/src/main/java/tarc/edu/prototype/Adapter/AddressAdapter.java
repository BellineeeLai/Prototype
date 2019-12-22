package tarc.edu.prototype.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import tarc.edu.prototype.Model.Address;
import tarc.edu.prototype.R;
import tarc.edu.prototype.ViewHolder.AddressViewHolder;

public class AddressAdapter extends FirebaseRecyclerAdapter<Address, AddressViewHolder> {

    private Context context;

    public AddressAdapter(@NonNull FirebaseRecyclerOptions<Address> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull AddressViewHolder addressViewHolder, int i, @NonNull Address address) {
        String address_text = address.getAddress() + " " +address.getPostalCode() + " "+ address.getArea() + " "+ address.getState();
        addressViewHolder.receiverAddress.setText(address_text);
        addressViewHolder.phoneNo.setText(address.getPhoneNo());
        addressViewHolder.receiverName.setText(address.getReceiverName());
        addressViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_layout, parent, false);
        return new AddressViewHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getRef().removeValue();
    }
}
