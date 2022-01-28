package com.xera.notetify.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FinancialModel implements Comparable<FinancialModel>{

    private int financialID;
    private String event;
    private String dateCreated;
    private double amount;
    private int expensesCategory;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected;

    public FinancialModel() {
        this.financialID = -1;
        this.event = "";
        this.dateCreated = "";
        this.amount = 0;
        this.expensesCategory = -1;
    }

    public FinancialModel(int financialID, String event, String dateCreated, double amount, int expensesCategory) {
        this.financialID = financialID;
        this.event = event;
        this.dateCreated = dateCreated;
        this.amount = amount;
        this.expensesCategory = expensesCategory;
    }

    public int getFinancialID() {
        return financialID;
    }

    public void setFinancialID(int financialID) {
        this.financialID = financialID;
    }

    public int getExpensesCategory() {
        return expensesCategory;
    }

    public void setExpensesCategory(int expensesCategory) {
        this.expensesCategory = expensesCategory;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(FinancialModel financialModel) {
        if (getDateCreated() == null || financialModel.getDateCreated() == null){
            return -1;
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            try{
                Date date1 = sdf.parse(getDateCreated());
                Date date2 = sdf.parse(financialModel.getDateCreated());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }
}
