package com.xera.notetify.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.xera.notetify.Controller.NotetifySecurityManager;
import com.xera.notetify.R;

public class VerificationActivity extends AppCompatActivity {

    private Spinner spinnerSecurityQuestion1;
    private EditText editTextSecurityAnswer1;
    private ExtendedFloatingActionButton fabProceed;
    private int securityQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        
        initViews();
        
    }

    private void initViews() {
        spinnerSecurityQuestion1 = findViewById(R.id.spinnerSecurityQuestion1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.security_question_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

        spinnerSecurityQuestion1.setAdapter(adapter);

        editTextSecurityAnswer1 = findViewById(R.id.editTextSecurityAnswer1);

        spinnerSecurityQuestion1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                securityQuestion = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fabProceed = findViewById(R.id.fabProceed);
        fabProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (securityQuestion == 0){
                    Toast.makeText(VerificationActivity.this, "Please select one security question", Toast.LENGTH_SHORT).show();
                }else if (editTextSecurityAnswer1.getText().toString().equals("")){
                    Toast.makeText(VerificationActivity.this, "Security answer cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    NotetifySecurityManager notetifySecurityManager = new NotetifySecurityManager();
                    boolean isSecurityAnswerCorrect = notetifySecurityManager.checkSecurityAnswer(VerificationActivity.this, securityQuestion, editTextSecurityAnswer1.getText().toString());
                    if (isSecurityAnswerCorrect){
                        Intent resetPasswordIntent = new Intent(VerificationActivity.this, ResetPasswordActivity.class);
                        startActivity(resetPasswordIntent);
                        finish();
                    }else{
                        Toast.makeText(VerificationActivity.this, "Wrong security question or answer", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}