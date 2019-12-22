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

import tarc.edu.prototype.Model.UserOrderList;

import static android.content.Context.MODE_PRIVATE;


public class CompletedFragment extends Fragment {
    private static final String FILE_NAME = "SmartDresser";
    private String userNode;
    private String orderNode;
    private String staffId, userType, staffNode, status;
    private DatabaseReference orderRef;
    private OrderAdapter adapter;
    private FirebaseUser user;
    private Query query;

    private RecyclerView order_list;
    public CompletedFragment() {
        orderNode = "Orders";
        userNode = "Users";
        staffNode = "Staff";
        status = "COMPLETE";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        assert getArguments() != null;
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

        assert userType != null;
        if(userType.equals("Staff")){
            staffId = sharedPreferences.getString("staffId", null);
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
        if(userType.equals("Customer")) {
            orderRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(userNode)
                    .child(user.getUid())
                    .child(orderNode);
        }

        if(userType.equals("Staff")) {
            orderRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(staffNode)
                    .child(staffId)
                    .child(orderNode);
        }
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