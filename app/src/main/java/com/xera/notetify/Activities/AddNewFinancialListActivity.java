package com.xera.notetify.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.xera.notetify.Controller.MoneyTextWatcher;
import com.xera.notetify.Database.DatabaseFinancialPlanner;
import com.xera.notetify.Model.FinancialModel;
import com.xera.notetify.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNewFinancialListActivity extends AppCompatActivity {

    private static final String TAG = "AddNewFinancialActivity";

    private MaterialToolbar toolbarAddNewList;
    private Spinner spinnerExpenseCategory;
    private EditText edtTextExpenseEvent, edtTextExpenseAmount;
    private DatePicker datePickerEvent;
    private Button btnAddNewList, btnCancel;
    private int expensesCategory;
    private DatabaseFinancialPlanner databaseFinancialPlanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_financial_list);
        initDatabase();
        initViews();
    }

    private void initDatabase(){
        databaseFinancialPlanner = new DatabaseFinancialPlanner(AddNewFinancialListActivity.this);
    }

    private void initViews() {

        toolbarAddNewList = findViewById(R.id.toolbarFP_AddNewList);
        setSupportActionBar(toolbarAddNewList);
        toolbarAddNewList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close activity when user click back button
                finish();
            }
        });
        spinnerExpenseCategory = findViewById(R.id.spinnerExpenseCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.expenses_category_array,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        spinnerExpenseCategory.setAdapter(adapter);

        spinnerExpenseCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                expensesCategory = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        edtTextExpenseEvent = findViewById(R.id.edtTextExpenseEvent);
        edtTextExpenseAmount = findViewById(R.id.edtTextExpenseAmount);
//        edtTextExpenseAmount.setFilters(new InputFilter[] {
//                new DecimalDigitsInputFilter(2), new InputFilter.LengthFilter(10)});

        edtTextExpenseAmount.addTextChangedListener(new MoneyTextWatcher(edtTextExpenseAmount));

        datePickerEvent = findViewById(R.id.datePickerEvent);

        btnAddNewList = findViewById(R.id.btnAddNewList2);
        btnAddNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataValidation();
            }
        });
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void checkDataValidation() {
        if (edtTextExpenseEvent.getText().toString().equals("")){
            Toast.makeText(AddNewFinancialListActivity.this, "Expense Event Cannot Be Empty", Toast.LENGTH_SHORT).show();
        } else if (edtTextExpenseAmount.getText().toString().equals("")){
            Toast.makeText(AddNewFinancialListActivity.this, "Expense Amount Cannot Be Empty", Toast.LENGTH_SHORT).show();
        } else{
            //List not empty proceed add new list
            addNewList();
        }
    }

    private void addNewList(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        String currentDateTime = datePickerEvent.getDayOfMonth() + " " + (datePickerEvent.getMonth()+1) + " " + datePickerEvent.getYear();
        Date date;
        try {
            date = sdf.parse(currentDateTime);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String expenseAmount = edtTextExpenseAmount.getText().toString();

            FinancialModel fm = new FinancialModel(
                    -1,
                    edtTextExpenseEvent.getText().toString(),
                    sdf1.format(date),
                    Double.parseDouble(expenseAmount.replaceAll("[$,]", "")),
                    expensesCategory);

            boolean insertStatus = databaseFinancialPlanner.addNewList(fm);

            if (insertStatus){
                FinancialPlannerActivity.refreshFinancialList(this);
                finish(); //Kill this activity and back to note list activity
            }else{
                Toast.makeText(AddNewFinancialListActivity.this,TAG + ": Error inserting into database", Toast.LENGTH_SHORT).show();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}