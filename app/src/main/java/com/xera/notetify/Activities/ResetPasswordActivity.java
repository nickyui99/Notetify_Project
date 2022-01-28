package com.xera.notetify.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xera.notetify.Controller.NotetifySecurityManager;
import com.xera.notetify.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtTextNumberPassword1, edtTextNumberPassword2;
    private Button fabReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();
        checkPassword();
    }

    private void initViews() {
        edtTextNumberPassword1 = findViewById(R.id.editTextNumberPassword1);
        edtTextNumberPassword2 = findViewById(R.id.editTextNumberPassword2);
        fabReset = findViewById(R.id.fabReset);

    }

    private void checkPassword() {
        fabReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int inputPassword1 = Integer.parseInt(edtTextNumberPassword1.getText().toString());
                int inputPassword2 = Integer.parseInt(edtTextNumberPassword2.getText().toString());
                if(inputPassword1 == inputPassword2){
                    NotetifySecurityManager notetifySecurityManager = new NotetifySecurityManager();
                    notetifySecurityManager.resetPassword(ResetPasswordActivity.this, inputPassword1);
                    finish();
                }else{
                    Toast.makeText(ResetPasswordActivity.this, "Wrong Pin", Toast.LENGTH_SHORT).show();
                    edtTextNumberPassword2.setText("");
                    edtTextNumberPassword2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    edtTextNumberPassword1.setText("");
                    edtTextNumberPassword1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    edtTextNumberPassword1.requestFocus();
                }
            }
        });
    }
}