package tarc.edu.prototype;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import tarc.edu.prototype.Model.PromoCode;

public class GeneratePromoCode extends Fragment {
    private TextInputEditText input_code;
    private TextInputEditText input_value;
    private MaterialButton btn_confirm;

    private String tempPromoCode;
    private String tempPromoValue;

    public GeneratePromoCode() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate_promo_code, container, false);

        input_code = view.findViewById(R.id.input_code);
        input_value = view.findViewById(R.id.input_value);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("PromoCode");

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPromoCode = input_code.getText().toString().trim();
                tempPromoValue = input_value.getText().toString().trim();

                if(!TextUtils.isEmpty(tempPromoCode) && !TextUtils.isEmpty(tempPromoValue)) {
                    String code = tempPromoCode;
                    double value = Double.parseDouble(tempPromoValue);

                    final HashMap<String, Object> map = new HashMap<>();
                    map.put("code", code);
                    map.put("value", value);
                    reference.push().setValue(map);
                }
            }
        });
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
