package tarc.edu.prototype;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

import tarc.edu.prototype.Model.UserOrderList;

import static android.content.Context.MODE_PRIVATE;


public class ToReceivedFragment extends Fragment {
    private static final String FILE_NAME = "SmartDresser";
    private String orderNode;
    private String userType,status;
    private DatabaseReference orderRef;
    private OrderAdapter adapter;
    private FirebaseUser user;
    Query query;
    String tempNode, tempId;

    private RecyclerView order_list;
    public ToReceivedFragment() {
        orderNode = "Orders";
        status = "TO RECEIVE";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_to_received, container, false);

        assert getArguments() != null;
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        order_list = view.findViewById(R.id.orderRecycleView);
        order_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        order_list.setLayoutManager(layoutManager);
        loadOrder();
        return view;
    }

    private void loadOrder() {
        orderRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(tempNode)
                .child(tempId)
                .child(orderNode);

        query = orderRef.orderByChild("status").equalTo(status);
        FirebaseRecyclerOptions<UserOrderList> options = new FirebaseRecyclerOptions.Builder<UserOrderList>()
                .setQuery(query, UserOrderList.class)
                .build();

        adapter = new OrderAdapter(options, getContext());
        order_list.setAdapter(adapter);
        adapter.startListening();
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public interface OnFragmentInteractionListener {

    }
}
