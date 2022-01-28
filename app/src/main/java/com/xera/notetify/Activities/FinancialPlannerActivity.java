package com.xera.notetify.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.xera.notetify.Controller.FinancialAdapter;
import com.xera.notetify.Database.DatabaseFinancialPlanner;
import com.xera.notetify.Model.FinancialModel;
import com.xera.notetify.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FinancialPlannerActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private static float[] expenseCategoryAmount;
    private static String[] expensesCategory;
    private static float totalAmountExpenses = 0;
    private Button btnEditList, btnAddNew;
    private static PieChart pieChart;
    private static ArrayList<PieEntry> pieEntries;
    private static RecyclerView rv_financialList;
    private static ArrayList<FinancialModel> financialModelArrayList = new ArrayList<>();
    private static ArrayList<Integer> colors;
    private FinancialAdapter financialAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseFinancialPlanner databaseFinancialPlanner;
    private MaterialToolbar toolbarFinancialPlanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_planner);

        initDatabase();
        initViews();
        initList();
        initChart();
        chartProperties();
    }

    private void initDatabase(){
        databaseFinancialPlanner = new DatabaseFinancialPlanner(this);
    }

    private void initViews(){

        toolbarFinancialPlanner = findViewById(R.id.toolbarFP);
        setSupportActionBar(toolbarFinancialPlanner);
        toolbarFinancialPlanner.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close activity when user click back button
                finish();
            }
        });
        btnEditList = findViewById(R.id.btnEditList);
        btnEditList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editListIntent = new Intent(FinancialPlannerActivity.this, EditListActivity.class);
                startActivity(editListIntent);
            }
        });

        btnAddNew = findViewById(R.id.btnAddNew);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewListIntent = new Intent(FinancialPlannerActivity.this, AddNewFinancialListActivity.class);
                startActivity(addNewListIntent);
            }
        });
    }

    private void initList() {
        rv_financialList = findViewById(R.id.rv_financialList);
        financialModelArrayList = databaseFinancialPlanner.getAllData();
        financialAdapter = new FinancialAdapter(this, financialModelArrayList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_financialList.setLayoutManager(linearLayoutManager);
        rv_financialList.setAdapter(financialAdapter);
    }

    private void chartProperties() {
        pieChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(75f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.setOnChartValueSelectedListener(this);
        pieChart.setCenterText("Expenses\nChart");
        pieChart.setCenterTextSize(16f);

        pieChart.animateY(1400, Easing.EaseInOutQuad);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
    }

    private void initChart() {
        pieChart = findViewById(R.id.pieChart_view);
        pieEntries = new ArrayList<>();
        String label = "type";

        expensesCategory = getResources().getStringArray(R.array.expenses_category_array);

        //initializing data
        totalAmountExpenses = 0;
        expenseCategoryAmount = new float[expensesCategory.length];
        for(int i=0; i<expensesCategory.length; i++){
            expenseCategoryAmount[i] = 0;
            for(FinancialModel fm: financialModelArrayList){
                if(fm.getExpensesCategory() == i){
                    expenseCategoryAmount[i] += fm.getAmount();
                }
            }
            totalAmountExpenses += expenseCategoryAmount[i];
        }

        //convert to PERCENTAGE

        for(int i=0; i<expensesCategory.length; i++){
            pieEntries.add(new PieEntry(expenseCategoryAmount[i]/totalAmountExpenses*100, expensesCategory[i]));
        }

        //initializing colors for the entries
        colors = new ArrayList<>();
        colors.add(Color.parseColor("#003f5c"));
        colors.add(Color.parseColor("#2f4b7c"));
        colors.add(Color.parseColor("#665191"));
        colors.add(Color.parseColor("#a05195"));
        colors.add(Color.parseColor("#d45087"));
        colors.add(Color.parseColor("#f95d6a"));
        colors.add(Color.parseColor("#ff7c43"));
        colors.add(Color.parseColor("#ffa600"));

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(false);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if(e == null){
            return;
        }else{
            DecimalFormat df = new DecimalFormat("0.00");
            PieEntry pe= pieEntries.get((int) h.getX());
            SpannableString spannableString = new SpannableString(pe.getLabel() + "\n" + df.format(pe.getValue()) + " %\n$ " + df.format(expenseCategoryAmount[(int) h.getX()]));
            final int index = spannableString.toString().indexOf('\n');
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, index, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            pieChart.setCenterText(spannableString);
        }
    }

    @Override
    public void onNothingSelected() {
        pieChart.setCenterText("Expenses\nChart");
    }


    protected static void refreshFinancialList(Context context){
        DatabaseFinancialPlanner databaseFinancialPlanner = new DatabaseFinancialPlanner(context);
        financialModelArrayList = databaseFinancialPlanner.getAllData();
        FinancialAdapter financialAdapter = new FinancialAdapter(context, financialModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv_financialList.setLayoutManager(linearLayoutManager);
        rv_financialList.setAdapter(financialAdapter);

        pieEntries = new ArrayList<>();
        String label = "type";

        //initializing data
        totalAmountExpenses = 0;
        expenseCategoryAmount = new float[expensesCategory.length];
        for(int i=0; i<expensesCategory.length; i++){
            expenseCategoryAmount[i] = 0;
            for(FinancialModel fm: financialModelArrayList){
                if(fm.getExpensesCategory() == i){
                    expenseCategoryAmount[i] += fm.getAmount();
                }
            }
            totalAmountExpenses += expenseCategoryAmount[i];
        }

        //convert to PERCENTAGE

        for(int i=0; i<expensesCategory.length; i++){
            pieEntries.add(new PieEntry(expenseCategoryAmount[i]/totalAmountExpenses*100, expensesCategory[i]));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(false);

        pieChart.setData(pieData);
        pieChart.highlightValue(null);
        pieChart.setCenterText("Expense Chart");
        pieChart.notifyDataSetChanged(); // let the chart know it's data changed
        pieChart.invalidate(); // refresh

    }
}