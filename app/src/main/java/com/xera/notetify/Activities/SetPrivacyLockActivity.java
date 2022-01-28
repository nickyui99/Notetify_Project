package com.xera.notetify.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xera.notetify.R;

public class SetPrivacyLockActivity extends AppCompatActivity {

    private LinearLayout linearLayoutSetPassword, linearLayoutWrongPassword;
    private TextView txtViewSetUpPassword, txtViewReenterPassword, txtViewWrongPassword;
    private EditText edtTextSetPassword1, edtTextSetPassword2, edtTextSetPassword3, edtTextSetPassword4, edtTextSetPassword5, edtTextSetPassword6;
    private String inputPassword1 = null;
    private String inputPassword2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_privacy_lock);
        initView();
    }

    private void initView() {
        linearLayoutSetPassword = findViewById(R.id.linearLayoutSetPassword);
        linearLayoutWrongPassword = findViewById(R.id.linearLayoutWrongPassword);
        txtViewSetUpPassword = findViewById(R.id.txtViewSetPassword);
        txtViewReenterPassword = findViewById(R.id.txtViewReenterPassword);
        txtViewWrongPassword = findViewById(R.id.textViewWrongPassword);
        edtTextSetPassword1 = findViewById(R.id.editTextSetPassword1);
        edtTextSetPassword2 = findViewById(R.id.editTextSetPassword2);
        edtTextSetPassword3 = findViewById(R.id.editTextSetPassword3);
        edtTextSetPassword4 = findViewById(R.id.editTextSetPassword4);
        edtTextSetPassword5 = findViewById(R.id.editTextSetPassword5);
        edtTextSetPassword6 = findViewById(R.id.editTextSetPassword6);

        edtTextSetPassword1.addTextChangedListener(new TextWatcher() {
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
                    edtTextSetPassword1.clearFocus();
                    edtTextSetPassword2.requestFocus();
//                    edtTextPassword2.setCursorVisible(true);
                }else if (editable.length()<1){
                    edtTextSetPassword1.requestFocus();
                }
            }
        });

        edtTextSetPassword2.addTextChangedListener(new TextWatcher() {
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
                    edtTextSetPassword2.clearFocus();
                    edtTextSetPassword3.requestFocus();
                }else if (editable.length()<1){
                    edtTextSetPassword1.requestFocus();
                }
            }
        });

        edtTextSetPassword3.addTextChangedListener(new TextWatcher() {
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
                    edtTextSetPassword3.clearFocus();
                    edtTextSetPassword4.requestFocus();
                }else if (editable.length()<1){
                    edtTextSetPassword2.requestFocus();
                }
            }
        });

        edtTextSetPassword4.addTextChangedListener(new TextWatcher() {
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
                    edtTextSetPassword4.clearFocus();
                    edtTextSetPassword5.requestFocus();
                }else if (editable.length()<1){
                    edtTextSetPassword3.requestFocus();
                }
            }
        });

        edtTextSetPassword5.addTextChangedListener(new TextWatcher() {
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
                    edtTextSetPassword5.clearFocus();
                    edtTextSetPassword6.requestFocus();
//                    edtTextPassword6.setCursorVisible(true);
                }else if (editable.length()<1){
                    edtTextSetPassword4.requestFocus();
                }
            }
        });

        edtTextSetPassword6.addTextChangedListener(new TextWatcher() {
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
                    if (inputPassword1 == null){
                        savePassword1();
                        edtTextSetPassword1.setText("");
                        edtTextSetPassword2.setText("");
                        edtTextSetPassword3.setText("");
                        edtTextSetPassword4.setText("");
                        edtTextSetPassword5.setText("");
                        edtTextSetPassword6.setText("");
                        txtViewReenterPassword.setVisibility(View.VISIBLE);
                        txtViewSetUpPassword.setVisibility(View.GONE);
                        edtTextSetPassword1.requestFocus();
                    }else{
                        savePassword2();
                        checkPassword();
                    }

                }else if (editable.length()<1){
                    edtTextSetPassword5.requestFocus();
                }
            }
        });
    }

    private void savePassword1(){
        inputPassword1 = edtTextSetPassword1.getText().toString() +
                edtTextSetPassword2.getText().toString() +
                edtTextSetPassword3.getText().toString() +
                edtTextSetPassword4.getText().toString() +
                edtTextSetPassword5.getText().toString() +
                edtTextSetPassword6.getText().toString();
    }

    private void savePassword2(){
        inputPassword2 = edtTextSetPassword1.getText().toString() +
                edtTextSetPassword2.getText().toString() +
                edtTextSetPassword3.getText().toString() +
                edtTextSetPassword4.getText().toString() +
                edtTextSetPassword5.getText().toString() +
                edtTextSetPassword6.getText().toString();
    }

    private void checkPassword() {
        if(inputPassword1.equals(inputPassword2)){

            Intent securityQuestionIntent = new Intent(SetPrivacyLockActivity.this, SecurityQuestionActivity.class);
            securityQuestionIntent.putExtra("privacyPassword", inputPassword1);
            startActivity(securityQuestionIntent);
            finish();

        }else{
            edtTextSetPassword1.setText("");
            edtTextSetPassword2.setText("");
            edtTextSetPassword3.setText("");
            edtTextSetPassword4.setText("");
            edtTextSetPassword5.setText("");
            edtTextSetPassword6.setText("");
            edtTextSetPassword1.clearFocus();
            edtTextSetPassword2.clearFocus();
            edtTextSetPassword3.clearFocus();
            edtTextSetPassword4.clearFocus();
            edtTextSetPassword5.clearFocus();
            edtTextSetPassword6.clearFocus();
            edtTextSetPassword1.requestFocus();

            edtTextSetPassword1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextSetPassword2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextSetPassword3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextSetPassword4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextSetPassword5.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            edtTextSetPassword6.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            linearLayoutWrongPassword.setVisibility(View.VISIBLE);
        }
    }
}