package com.xera.notetify.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xera.notetify.Model.FinancialModel;
import com.xera.notetify.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class FinancialEditAdapter extends RecyclerView.Adapter<FinancialEditAdapter.Viewholder> {
    private Context context;
    private ArrayList<FinancialModel> financialModelArrayList;

    //Constructor
    public FinancialEditAdapter(Context context, ArrayList<FinancialModel> financialModelArrayList) {
        this.context = context;
        //Sort all the data from the most recent to oldest
        Collections.sort(financialModelArrayList, new FinancialEditAdapter.SortByDate());
        Collections.reverse(financialModelArrayList);
        this.financialModelArrayList = financialModelArrayList;
    }

    static class SortByDate implements Comparator<FinancialModel> {
        @Override
        public int compare(FinancialModel a, FinancialModel b) {
            if (a.getDateCreated() == null || b.getDateCreated() == null){
                return -1;
            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                try{
                    Date date1 = sdf.parse(a.getDateCreated());
                    Date date2 = sdf.parse(b.getDateCreated());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return -1;
                }
            }
        }
    }

    @NonNull
    @Override
    public FinancialEditAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_edit_financial_list, parent, false);
        return new FinancialEditAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinancialEditAdapter.Viewholder holder, int position) {
        DecimalFormat df = new DecimalFormat("#.00");

        final FinancialModel currentFinancialModel = financialModelArrayList.get(position);
        if(position == 0){
            //Do nothing if it is the first item
            holder.checkBoxEvent.setText(currentFinancialModel.getEvent());
            holder.txtViewDate.setText(currentFinancialModel.getDateCreated());
            String amount = df.format(currentFinancialModel.getAmount());
            holder.txtViewAmount.setText(stringToCurrency(amount));
        }else{
            //check date
            FinancialModel previousFinancialModel = financialModelArrayList.get(position-1);
            holder.checkBoxEvent.setText(currentFinancialModel.getEvent());
            if(currentFinancialModel.getDateCreated().equals(previousFinancialModel.getDateCreated())){
                //if current model date same with previous model hide the date
                holder.txtViewDate.setVisibility(View.GONE);
            }else{
                //else show date
                holder.txtViewDate.setVisibility(View.VISIBLE);
                holder.txtViewDate.setText(currentFinancialModel.getDateCreated());
            }
            String amount = df.format(currentFinancialModel.getAmount());
            holder.txtViewAmount.setText(stringToCurrency(amount));
        }

        holder.checkBoxEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFinancialModel.isSelected()) {
                    currentFinancialModel.setSelected(false);
                } else {
                    currentFinancialModel.setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return financialModelArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        private TextView  txtViewDate, txtViewAmount;
        private CheckBox checkBoxEvent;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            checkBoxEvent = itemView.findViewById(R.id.checkBoxEvent);
            txtViewDate = itemView.findViewById(R.id.txtViewFP_dateEditList);
            txtViewAmount = itemView.findViewById(R.id.txtViewEditAmount);
        }
    }


    public String stringToCurrency(String stringValue){
        String cleanString = stringValue.replaceAll("[$,.]", "");
        BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        String formatted = NumberFormat.getCurrencyInstance().format(parsed);
        return formatted;
    }
}
