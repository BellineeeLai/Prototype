package tarc.edu.prototype.View.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

import tarc.edu.prototype.Model.PromoCode;
import tarc.edu.prototype.R;

public class PromoCodeActivity extends AppCompatActivity {

    private TextInputEditText promo_code;
    private MaterialButton btn_apply;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_promo_code);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        promo_code = findViewById(R.id.input_promo);
        btn_apply = findViewById(R.id.apply_code);

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = promo_code.getText().toString();

                if (code.isEmpty()) {

                } else {
                    final DatabaseReference reference = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("PromoCode");

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    PromoCode promoCode = ds.getValue(PromoCode.class);
                                    assert promoCode != null;
                                    if (promoCode.getCode().equals(code)) {
                                        Intent intent = new Intent();
                                        intent.putExtra("promo_code", code);
                                        intent.putExtra("discount", promoCode.getValue());
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    } else {

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if (code.equals("test")) {

                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
