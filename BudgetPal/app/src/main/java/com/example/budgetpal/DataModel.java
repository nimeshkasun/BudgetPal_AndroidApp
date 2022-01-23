package com.example.budgetpal;

public class DataModel {

    private int catId;
    private String catName;
    private String catDescription;
    private String catType;
    private double catBudget;

    private int tranId;
    private String tranCategory;
    private String tranDate;
    private String tranDescription;
    private String tranRecurring;
    private double transPrice;
    private String transType;

    private int totId;
    private double totCredit;
    private double totDebit;
    private double totBalance;


    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatDescription() {
        return catDescription;
    }

    public void setCatDescription(String catDescription) {
        this.catDescription = catDescription;
    }

    public String getCatType() {
        return catType;
    }

    public void setCatType(String catType) {
        this.catType = catType;
    }

    public double getCatBudget() {
        return catBudget;
    }

    public void setCatBudget(double catBudget) {
        this.catBudget = catBudget;
    }

    public int getTranId() {
        return tranId;
    }

    public void setTranId(int tranId) {
        this.tranId = tranId;
    }

    public String getTranCategory() {
        return tranCategory;
    }

    public void setTranCategory(String tranCategory) {
        this.tranCategory = tranCategory;
    }

    public String getTranDate() {
        return tranDate;
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranDescription() {
        return tranDescription;
    }

    public void setTranDescription(String tranDescription) {
        this.tranDescription = tranDescription;
    }

    public String getTranRecurring() {
        return tranRecurring;
    }

    public void setTranRecurring(String tranRecurring) {
        this.tranRecurring = tranRecurring;
    }

    public double getTransPrice() {
        return transPrice;
    }

    public void setTransPrice(double transPrice) {
        this.transPrice = transPrice;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public double getTotId() {
        return totId;
    }

    public void setTotId(int totId) {
        this.totId = totId;
    }

    public double getTotCredit() {
        return totCredit;
    }

    public void setTotCredit(double totCredit) {
        this.totCredit = totCredit;
    }

    public double getTotDebit() {
        return totDebit;
    }

    public void setTotDebit(double totDebit) {
        this.totDebit = totDebit;
    }

    public double getTotBalance() {
        return totBalance;
    }

    public void setTotBalance(double totBalance) {
        this.totBalance = totBalance;
    }

    public DataModel(int catId, String catName, String catDescription, String catType, double catBudget) {
        this.catId = catId;
        this.catName = catName;
        this.catDescription = catDescription;
        this.catType = catType;
        this.catBudget = catBudget;
    }

    public DataModel(int tranId, String tranCategory, String tranDate, String tranDescription, String tranRecurring, double transPrice, String transType) {
        this.tranId = tranId;
        this.tranCategory = tranCategory;
        this.tranDate = tranDate;
        this.tranDescription = tranDescription;
        this.tranRecurring = tranRecurring;
        this.transPrice = transPrice;
        this.transType = transType;
    }

    public DataModel(int totId, double totCredit, double totDebit, double totBalance) {
        this.catId = catId;
        this.totCredit = totCredit;
        this.totDebit = totDebit;
        this.totBalance = totBalance;
    }

    public DataModel(int catId, String catName) {
        this.catId = catId;
        this.catName = catName;
    }

    public DataModel(double transPrice) {
        this.transPrice = transPrice;
    }
}
