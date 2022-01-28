package com.xera.notetify.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xera.notetify.Controller.NotetifySecurityManager;
import com.xera.notetify.Model.PrivacySecurityModel;
import com.xera.notetify.R;

public class SecurityQuestionActivity extends AppCompatActivity {

    private Spinner spinnerSecurityQuestion;
    private EditText edtTextSecurityAnswer;
    private Button btnDone;
    private String privacyPassword;
    private int securityQuestion;
    private String securityAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_question);

        Intent setPrivacyPasswordIntent = getIntent();
        privacyPassword = setPrivacyPasswordIntent.getStringExtra("privacyPassword");

        intViews();

    }

    private void intViews() {
        spinnerSecurityQuestion = (Spinner) findViewById(R.id.spinnerSecurityQuestion);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.security_question_array,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

        spinnerSecurityQuestion.setAdapter(adapter);

        spinnerSecurityQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                securityQuestion = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edtTextSecurityAnswer = findViewById(R.id.editTextSecurityAnswer);


        btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtTextSecurityAnswer.getText().toString().equals("")){
                    Toast.makeText(SecurityQuestionActivity.this, "Security answer cannot be empty", Toast.LENGTH_SHORT).show();
                }else if(securityQuestion == 0){
                    Toast.makeText(SecurityQuestionActivity.this, "Please select one security question", Toast.LENGTH_SHORT).show();
                }else{
                    securityAnswer = edtTextSecurityAnswer.getText().toString();
                    PrivacySecurityModel privacySecurityModel = new PrivacySecurityModel(Integer.parseInt(privacyPassword), securityQuestion, securityAnswer);
                    NotetifySecurityManager notetifySecurityManager = new NotetifySecurityManager();
                    notetifySecurityManager.setNewPassword(SecurityQuestionActivity.this, privacySecurityModel);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}