package com.xera.notetify.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xera.notetify.Controller.NotetifySecurityManager;
import com.xera.notetify.R;

public class PrivacyLockActivity extends AppCompatActivity {

    private LinearLayout linearLayoutInputWrongPassword;
    private TextView txtViewEnterPassword, txtViewForgotPassword;
    private EditText edtTextPassword1, edtTextPassword2, edtTextPassword3, edtTextPassword4, edtTextPassword5, edtTextPassword6;
    private String inputPassword;
    private int noteID;
    private NotetifySecurityManager notetifySecurityManager = new NotetifySecurityManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_lock);

        //retrieve data from NoteActivity
        Intent intent = getIntent();
        noteID = intent.getIntExtra("noteID", 0);

        initView();

    }


    private void initView() {
        linearLayoutInputWrongPassword = findViewById(R.id.linearLayoutInputWrongPassword);
        txtViewForgotPassword = findViewById(R.id.txtViewForgotPassword);
        txtViewEnterPassword = findViewById(R.id.txtViewSetPassword);
        edtTextPassword1 = findViewById(R.id.editTextPassword1);
        edtTextPassword2 = findViewById(R.id.editTextPassword2);
        edtTextPassword3 = findViewById(R.id.editTextPassword3);
        edtTextPassword4 = findViewById(R.id.editTextPassword4);
        edtTextPassword5 = findViewById(R.id.editTextPassword5);
        edtTextPassword6 = findViewById(R.id.editTextPassword6);

        txtViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPasswordIntent = new Intent(PrivacyLockActivity.this, VerificationActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });

        edtTextPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==1)
                {
                    edtTextPassword1.clearFocus();
                    edtTextPassword2.requestFocus();
//                    edtTextPassword2.setCursorVisible(true);
                }else if (editable.length()<1){
                    edtTextPassword1.requestFocus();
                }
            }
        });

        edtTextPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==1)
                {
                    edtTextPassword2.clearFocus();
                    edtTextPassword3.requestFocus();
                }else if (editable.length()<1){
                    edtTextPassword1.requestFocus();
                }
            }
        });

        edtTextPassword3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==1)
                {
                    edtTextPassword3.clearFocus();
                    edtTextPassword4.requestFocus();
                }else if (editable.length()<1){
                    edtTextPassword2.requestFocus();
                }
            }
        });

        edtTextPassword4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==1)
                {
                    edtTextPassword4.clearFocus();
                    edtTextPassword5.requestFocus();
                }else if (editable.length()<1){
                    edtTextPassword3.requestFocus();
                }
            }
        });

        edtTextPassword5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==1)
                {
                    edtTextPassword5.clearFocus();
                    edtTextPassword6.requestFocus();
//                    edtTextPassword6.setCursorVisible(true);
                }else if (editable.length()<1){
                    edtTextPassword4.requestFocus();
                }
            }
        });

        edtTextPassword6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==1)
                {
                    checkPassword();
                }else if (editable.length()<1){
                    edtTextPassword5.requestFocus();
                }
            }
        });
    }

    private void checkPassword() {

        inputPassword = edtTextPassword1.getText().toString() +
                edtTextPassword2.getText().toString() +
                edtTextPassword3.getText().toString() +
                edtTextPassword4.getText().toString() +
                edtTextPassword5.getText().toString() +
                edtTextPassword6.getText().toString();

        Boolean passwordValidity = notetifySecurityManager.checkPasswordValidity(getBaseContext(), inputPassword);

        if (passwordValidity){
            Intent viewNoteIntent = new Intent(PrivacyLockActivity.this, NoteViewActivity.class);
            viewNoteIntent.putExtra("noteID", noteID );
            startActivity(viewNoteIntent);
            finish();
        }else{
            edtTextPassword1.setText("");
            edtTextPassword2.setText("");
            edtTextPassword3.setText("");
            edtTextPassword4.setText("");
            edtTextPassword5.setText("");
            edtTextPassword6.setText("");
            edtTextPassword1.clearFocus();
            edtTextPassword2.clearFocus();
            edtTextPassword3.clearFocus();
            edtTextPassword4.clearFocus();
            edtTextPassword5.clearFocus();
            edtTextPassword6.clearFocus();
            edtTextPassword1.requestFocus();

            edtTextPassword1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextPassword2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextPassword3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextPassword4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextPassword5.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextPassword6.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            linearLayoutInputWrongPassword.setVisibility(View.VISIBLE);
        }

    }
}