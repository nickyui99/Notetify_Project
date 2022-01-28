package com.xera.notetify.Activities;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.xera.notetify.Controller.FinancialEditAdapter;
import com.xera.notetify.Database.DatabaseFinancialPlanner;
import com.xera.notetify.Model.FinancialModel;
import com.xera.notetify.R;

import java.util.ArrayList;

public class EditListActivity extends AppCompatActivity {

    private MaterialToolbar toolbarFP_EditNewList;
    private DatabaseFinancialPlanner databaseFinancialPlanner;
    private static RecyclerView rv_financialEditList;
    private ArrayList<FinancialModel> financialModelArrayList;
    private FinancialEditAdapter financialEditAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        initDatabase();
        initList();
        initView();
    }

    private void initDatabase(){
         databaseFinancialPlanner = new DatabaseFinancialPlanner(this);
    }

    private void initList() {
        rv_financialEditList= findViewById(R.id.rv_editFinancialList);
        financialModelArrayList = databaseFinancialPlanner.getAllData();
        financialEditAdapter = new FinancialEditAdapter(this, financialModelArrayList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_financialEditList.setLayoutManager(linearLayoutManager);
        rv_financialEditList.setAdapter(financialEditAdapter);
    }

    private void initView() {
        btnDelete = findViewById(R.id.btnFP_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (FinancialModel financialModel: financialModelArrayList){
                    if(financialModel.isSelected()){
                        databaseFinancialPlanner.deleteList(financialModel);
                    }
                }
                refreshEditList();
            }
        });

        toolbarFP_EditNewList = findViewById(R.id.toolbarFP_EditNewList);
        setSupportActionBar(toolbarFP_EditNewList);
        toolbarFP_EditNewList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close activity when user click back button
                finish();
            }
        });
    }

    private void refreshEditList() {
        financialModelArrayList = databaseFinancialPlanner.getAllData();
        financialEditAdapter = new FinancialEditAdapter(this, financialModelArrayList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_financialEditList.setLayoutManager(linearLayoutManager);
        rv_financialEditList.setAdapter(financialEditAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FinancialPlannerActivity.refreshFinancialList(this);
        finish();
    }
}