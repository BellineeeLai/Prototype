package tarc.edu.prototype.View.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import tarc.edu.prototype.R;

import static android.content.Context.MODE_PRIVATE;


public class UserPreferenceFragment extends Fragment implements View.OnClickListener {

    private RadioGroup radioCat;
    private Button btnSave;
    private RadioButton cat;
    private String up;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    private String userType, tempId, tempNode;

    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;

    public UserPreferenceFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_preference, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        radioCat = view.findViewById(R.id.radioCat);
        btnSave = view.findViewById(R.id.save);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        btnSave.setOnClickListener(this);

        sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        userType = sharedPreferences.getString("user", null);

        assert userType != null;
        if (userType.equals("Customer")) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            tempId = user.getUid();
            tempNode = "Users";

        } else {
            tempId = sharedPreferences.getString("staffId", null);
            tempNode = "Staff";
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            int id = radioCat.getCheckedRadioButtonId();
            cat = radioCat.findViewById(id);
            up = cat.getText().toString();

            ref = database.getReference(tempNode).child(tempId).child("Details");
            final HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("preferences", up);
            ref.updateChildren(hashMap);
            Toast.makeText(getActivity(), "User Preference Set", Toast.LENGTH_LONG).show();

            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }


    }
}
