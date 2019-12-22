package tarc.edu.prototype.View.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import tarc.edu.prototype.R;
import tarc.edu.prototype.View.Activities.RegisterActivity;

import static android.content.Context.MODE_PRIVATE;

@SuppressWarnings("ALL")
public class LoginFragment extends Fragment {
    private EditText editEmail, editPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String staffId;
    private OnFragmentInteractionListener mListener;


    private static final String FILE_NAME = "SmartDresser";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public LoginFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
        TextView textViewRegister = view.findViewById(R.id.textViewSignup);
        TextView textViewSignStaff = view.findViewById(R.id.textViewSignStaff);
        editEmail = view.findViewById(R.id.editTextEmail);
        editPassword = view.findViewById(R.id.editTextPassword);
        Button buttonLogin = view.findViewById(R.id.buttonSignIn);

        progressDialog = new ProgressDialog(getActivity());

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = this.getActivity().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        staffId = sharedPreferences.getString("staffId", null);

        if (firebaseAuth.getCurrentUser() != null) {
            //profile activity
            FragmentManager fragmentManager = getFragmentManager();
            assert fragmentManager != null;

            Bundle bundle = new Bundle();
            bundle.putString("user", "Customer");

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ProfileFragment profileFragment = new ProfileFragment();

            profileFragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.fragment_container, profileFragment);
            fragmentTransaction.commit();
        }

        if(staffId != null){
            //profile activity
            FragmentManager fragmentManager = getFragmentManager();
            assert fragmentManager != null;

            Bundle bundle = new Bundle();
            bundle.putString("user", "Staff");
            bundle.putString("staffId", staffId);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ProfileFragment profileFragment = new ProfileFragment();

            profileFragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.fragment_container, profileFragment);
            fragmentTransaction.commit();
        }

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RegisterActivity.class));
            }
        });

        textViewSignStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                StaffLoginFragment staffLoginFragment = new StaffLoginFragment();

                fragmentTransaction.replace(R.id.fragment_container, staffLoginFragment);
                fragmentTransaction.commit();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        return view;
    }

    private void userLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_LONG).show();
        }

        progressDialog.setMessage("Login...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Successful", Toast.LENGTH_LONG).show();
                if (task.isSuccessful()) {
                    FragmentManager fragmentManager = getFragmentManager();
                    assert fragmentManager != null;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ProfileFragment profileFragment = new ProfileFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("user", "Customer");
                    profileFragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.fragment_container, profileFragment);
                    fragmentTransaction.commit();

                } else {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
