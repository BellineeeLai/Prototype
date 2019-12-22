package tarc.edu.prototype.View.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import tarc.edu.prototype.DeliveryReportActivity;
import tarc.edu.prototype.JobAssignedActivity;
import tarc.edu.prototype.R;

import static android.content.Context.MODE_PRIVATE;


public class DeliveryServiceFragment extends Fragment implements View.OnClickListener {

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CardView cardJob,cardReport;

    private String userType, tempId, tempNode;

    public DeliveryServiceFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery, container, false);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.attach(this);
        cardJob = view.findViewById(R.id.jobCard);
        cardReport= view.findViewById(R.id.reportCard);

        cardJob.setOnClickListener(this);
        cardReport.setOnClickListener(this);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        sharedPreferences = this.getActivity().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

        assert userType != null;
        tempId = sharedPreferences.getString("staffId", null);
        tempNode = "Staff";


        return view;
    }


    @Override
    public void onClick(View view) {
        if(view == cardJob){
            Intent intent = new Intent(getActivity(), JobAssignedActivity.class);
            startActivity(intent);
        }

        if(view == cardReport){
            Intent intent = new Intent(getActivity(), DeliveryReportActivity.class);
            startActivity(intent);
        }
    }

    public interface OnFragmentInteractionListener {

    }
}
