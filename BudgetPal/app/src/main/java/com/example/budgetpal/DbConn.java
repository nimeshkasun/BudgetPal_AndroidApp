package com.example.budgetpal;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DbConn extends SQLiteOpenHelper {

    private static final String DB_NAME = "budgetpal";

    private static final int DB_VERSION = 6;

    private static final String cTABLE_NAME = "categories";
    private static final String cID_COL = "cId";
    private static final String cNAME_COL = "cName";
    private static final String cDESCRIPTION_COL = "cDescription";
    private static final String cTYPE_COL = "cType";
    private static final String cBUDGET_COL = "cBudget";

    private static final String tTABLE_NAME = "transactions";
    private static final String tID_COL = "tId";
    private static final String tCATEGORY_COL = "tCategory";
    private static final String tDATE_COL = "tDate";
    private static final String tDESCRIPTION_COL = "tDescription";
    private static final String tRECURRING_COL = "tRecurring";
    private static final String tPRICE_COL = "tPrice";

    private static final String totTABLE_NAME = "totalcalculations";
    private static final String totID_COL = "totId";
    private static final String totCREDIT_COL = "totCredit";
    private static final String totDEBIT_COL = "totDebit";
    private static final String totBALANCE_COL = "totBalance";

    public DbConn(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cQuery = "CREATE TABLE " + cTABLE_NAME + " ("
                + cID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + cNAME_COL + " TEXT,"
                + cDESCRIPTION_COL + " TEXT,"
                + cTYPE_COL + " TEXT,"
                + cBUDGET_COL + " DOUBLE)";

        String tQuery = "CREATE TABLE " + tTABLE_NAME + " ("
                + tID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + tCATEGORY_COL + " TEXT,"
                + tDATE_COL + " TEXT,"
                + tDESCRIPTION_COL + " TEXT,"
                + tRECURRING_COL + " TEXT,"
                + tPRICE_COL + " DOUBLE)";

        String totalCalculationQuery = "CREATE TABLE " + totTABLE_NAME + " ("
                + totID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + totCREDIT_COL + " DOUBLE,"
                + totDEBIT_COL + " DOUBLE,"
                + totBALANCE_COL + " DOUBLE)";

        db.execSQL(cQuery);
        db.execSQL(tQuery);
        db.execSQL(totalCalculationQuery);

    }

    public void addSampleCategoryData() {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(cNAME_COL, "Salary");
        values.put(cDESCRIPTION_COL, "Monthly Category");
        values.put(cTYPE_COL, "Credit");
        values.put(cBUDGET_COL, 0.0);
        db.insert(cTABLE_NAME, null, values);

        values = new ContentValues();
        values.put(cNAME_COL, "Shopping");
        values.put(cDESCRIPTION_COL, "Shopping Category");
        values.put(cTYPE_COL, "Debit");
        values.put(cBUDGET_COL, 10000.0);
        db.insert(cTABLE_NAME, null, values);

        values = new ContentValues();
        values.put(cNAME_COL, "Utility bill");
        values.put(cDESCRIPTION_COL, "Utility billing  Category");
        values.put(cTYPE_COL, "Debit");
        values.put(cBUDGET_COL, 10000.0);
        db.insert(cTABLE_NAME, null, values);

        db.close();
    }

    public void addNewCategory(String catName, String catDescription, String catType, double catBudget) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(cNAME_COL, catName);
        values.put(cDESCRIPTION_COL, catDescription);
        values.put(cTYPE_COL, catType);
        values.put(cBUDGET_COL, catBudget);
        db.insert(cTABLE_NAME, null, values);

        db.close();
    }

    public void addNewTransaction(String tranCategory, String tranDate, String tranDescription, String tranRecurring, double transPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(tCATEGORY_COL, tranCategory);
        values.put(tDATE_COL, tranDate);
        values.put(tDESCRIPTION_COL, tranDescription);
        values.put(tRECURRING_COL, tranRecurring);
        values.put(tPRICE_COL, transPrice);

        db.insert(tTABLE_NAME, null, values);

        Cursor cursorCategoryType = db.rawQuery("SELECT cType FROM " + cTABLE_NAME + " WHERE cName = '" + tranCategory + "'", null);
        ArrayList<DataModel> transactionModalArrayList = new ArrayList<>();
        String catType = "";

        if (cursorCategoryType.moveToFirst()) {
            do {
                catType = cursorCategoryType.getString(cursorCategoryType.getColumnIndex("cType"));
            } while (cursorCategoryType.moveToNext());
        }
        cursorCategoryType.close();

        updateTotalCalculations(catType, transPrice);

        db.close();
    }

    public void updateCategory(int catId, String catName, String catDescription, String catType, double catBudget) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(cNAME_COL, catName);
        values.put(cDESCRIPTION_COL, catDescription);
        values.put(cTYPE_COL, catType);
        values.put(cBUDGET_COL, catBudget);
        db.update(cTABLE_NAME, values, "cId=?", new String[]{String.valueOf(catId)});

        db.close();
    }

    public void updateTransaction(int tId, String tranCategory, String tranDate, String tranDescription, String tranRecurring, double transPrice) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tCATEGORY_COL, tranCategory);
        values.put(tDATE_COL, tranDate);
        values.put(tDESCRIPTION_COL, tranDescription);
        values.put(tRECURRING_COL, tranRecurring);
        values.put(tPRICE_COL, transPrice);
        db.update(tTABLE_NAME, values, "tId=?", new String[]{String.valueOf(tId)});

        db.close();
    }

    public void updateTotalCalculations(String type, double transPrice) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursorCalculations = db.rawQuery("SELECT totCredit AS Credit, totDebit AS Debit, totBalance AS Balance FROM " + totTABLE_NAME + " WHERE totId = '" + 1 + "'", null);
        ArrayList<DataModel> categoryModalArrayList = new ArrayList<>();
        double totCredit = 0.0;
        double totDebit = 0.0;
        double totBalance = 0.0;
        if (cursorCalculations.moveToFirst()) {
            do {
                totCredit = cursorCalculations.getDouble(cursorCalculations.getColumnIndex("Credit"));
                totDebit = cursorCalculations.getDouble(cursorCalculations.getColumnIndex("Debit"));
                totBalance = cursorCalculations.getDouble(cursorCalculations.getColumnIndex("Balance"));
            } while (cursorCalculations.moveToNext());

        }
        cursorCalculations.close();

        ContentValues values = new ContentValues();

        if (type.equals("Credit")) {
            totCredit = totCredit + transPrice;
            totBalance = totBalance + transPrice;
        } else if (type.equals("Debit")) {
            totDebit = totDebit + transPrice;
            totBalance = totBalance - transPrice;
        }

        values.put(totCREDIT_COL, totCredit);
        values.put(totDEBIT_COL, totDebit);
        values.put(totBALANCE_COL, totBalance);

        try {
            if (db.update(totTABLE_NAME, values, "totId=?", new String[]{String.valueOf(1)}) == 0) {
                db.insert(totTABLE_NAME, null, values);
                Log.i("TOTCAL_Inserted", totBalance + "--" + totBalance + "--" + transPrice);
            }
        } catch (Exception e) {
            db.insert(totTABLE_NAME, null, values);
        }

        db.close();
    }

    public ArrayList<DataModel> readCategories() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCategories = db.rawQuery("SELECT * FROM " + cTABLE_NAME, null);

        ArrayList<DataModel> categoryModalArrayList = new ArrayList<>();

        if (cursorCategories.moveToFirst()) {
            do {
                categoryModalArrayList.add(new DataModel(cursorCategories.getInt(0),
                        cursorCategories.getString(1),
                        cursorCategories.getString(2),
                        cursorCategories.getString(3),
                        cursorCategories.getDouble(4)));
            } while (cursorCategories.moveToNext());
        }

        cursorCategories.close();

        return categoryModalArrayList;
    }

    public ArrayList<DataModel> readTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorTransactions = db.rawQuery("SELECT * FROM " + tTABLE_NAME + " ORDER BY tId DESC", null);

        ArrayList<DataModel> transactionModalArrayList = new ArrayList<>();

        if (cursorTransactions.moveToFirst()) {
            do {
                transactionModalArrayList.add(new DataModel(cursorTransactions.getInt(0),
                        cursorTransactions.getString(1),
                        cursorTransactions.getString(2),
                        cursorTransactions.getString(3),
                        cursorTransactions.getString(4),
                        cursorTransactions.getDouble(5),
                        readAndCheckDebitCategory(cursorTransactions.getString(1))));
                Log.i("TrType", readAndCheckDebitCategory(cursorTransactions.getString(1)));

            } while (cursorTransactions.moveToNext());
        }
        cursorTransactions.close();
        return transactionModalArrayList;
    }

    public ArrayList<DataModel> readDebitCategories() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCategories = db.rawQuery("SELECT * FROM " + cTABLE_NAME + " WHERE cType = 'Debit'", null);

        ArrayList<DataModel> categoryModalArrayList = new ArrayList<>();

        if (cursorCategories.moveToFirst()) {
            do {
                categoryModalArrayList.add(new DataModel(cursorCategories.getInt(0),
                        cursorCategories.getString(1),
                        cursorCategories.getString(2),
                        cursorCategories.getString(3),
                        cursorCategories.getDouble(4)));
            } while (cursorCategories.moveToNext());
        }

        cursorCategories.close();

        return categoryModalArrayList;
    }

    public ArrayList<DataModel> readCategoryNamesForSpinner() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCategories = db.rawQuery("SELECT cId, cName FROM " + cTABLE_NAME, null);

        ArrayList<DataModel> categoryModalArrayList = new ArrayList<>();

        if (cursorCategories.moveToFirst()) {
            do {
                categoryModalArrayList.add(new DataModel(cursorCategories.getInt(0),
                        cursorCategories.getString(1)));
            } while (cursorCategories.moveToNext());
        }

        cursorCategories.close();

        return categoryModalArrayList;
    }

    public double readTransactionCostPerCategory(String tCategory) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCategories = db.rawQuery("SELECT SUM(tPrice) AS Sum FROM " + tTABLE_NAME + " WHERE tCategory = '" + tCategory + "'", null);

        ArrayList<DataModel> categoryModalArrayList = new ArrayList<>();

        double cost = 0.0;

        if (cursorCategories.moveToFirst()) {
            do {

                cost = cursorCategories.getDouble(cursorCategories.getColumnIndex("Sum"));
            } while (cursorCategories.moveToNext());
        }

        cursorCategories.close();
        Log.i("COLUMN", String.valueOf(cost));

        return cost;
    }

    public String readAndCheckDebitCategory(String cName) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCategories = db.rawQuery("SELECT cType FROM " + cTABLE_NAME + " WHERE cName = '" + cName + "'", null);

        ArrayList<DataModel> categoryModalArrayList = new ArrayList<>();

        String type = "";

        if (cursorCategories.moveToFirst()) {
            do {

                type = cursorCategories.getString(cursorCategories.getColumnIndex("cType"));
            } while (cursorCategories.moveToNext());
        }

        cursorCategories.close();

        return type;
    }

    public double readBudgetPerCategory(String cName) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCategories = db.rawQuery("SELECT cBudget FROM " + cTABLE_NAME + " WHERE cName = '" + cName + "'", null);

        ArrayList<DataModel> categoryModalArrayList = new ArrayList<>();

        double budget = 0.0;

        if (cursorCategories.moveToFirst()) {
            do {
                budget = cursorCategories.getDouble(cursorCategories.getColumnIndex("cBudget"));
            } while (cursorCategories.moveToNext());
        }

        cursorCategories.close();
        Log.i("COLUMN", String.valueOf(budget));

        return budget;
    }

    public double readLeftBudgetForNewCategory() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorTransactionsCredit = db.rawQuery("SELECT SUM(t.tPrice) as Sum FROM " + tTABLE_NAME + " t, " + cTABLE_NAME + " c WHERE t.tCategory = c.cName AND c.cType = 'Credit'", null);
        Cursor cursorTransactionsDebit = db.rawQuery("SELECT SUM(cBudget) as Sum FROM " + cTABLE_NAME + " WHERE cType = 'Debit'", null);

        ArrayList<DataModel> transactionModalArrayList = new ArrayList<>();

        double credit = 0.0;
        double debit = 0.0;
        double left = 0.0;

        if (cursorTransactionsCredit.moveToFirst()) {
            do {
                credit = cursorTransactionsCredit.getDouble(cursorTransactionsCredit.getColumnIndex("Sum"));
            } while (cursorTransactionsCredit.moveToNext());
        }

        if (cursorTransactionsDebit.moveToFirst()) {
            do {
                debit = cursorTransactionsDebit.getDouble(cursorTransactionsDebit.getColumnIndex("Sum"));
            } while (cursorTransactionsDebit.moveToNext());
        }

        left = credit - debit;
        Log.i("LEFTBUDGEST", left + "--" + credit + "--" + debit);

        cursorTransactionsCredit.close();

        return left;
    }

    public double readTotalBalance() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCalculations = db.rawQuery("SELECT totBalance AS Balance FROM " + totTABLE_NAME + " WHERE totId = '" + 1 + "'", null);
        ArrayList<DataModel> categoryModalArrayList = new ArrayList<>();
        double totBalance = 0.0;
        if (cursorCalculations.moveToFirst()) {
            do {
                totBalance = cursorCalculations.getDouble(cursorCalculations.getColumnIndex("Balance"));
            } while (cursorCalculations.moveToNext());

        }
        cursorCalculations.close();

        db.close();
        Log.i("TOTBAL", String.valueOf(totBalance));

        return totBalance;
    }

    public ArrayList<DataModel> readTotalCalculations() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCalculations = db.rawQuery("SELECT * FROM " + totTABLE_NAME, null);

        ArrayList<DataModel> categoryModalArrayList = new ArrayList<>();

        if (cursorCalculations.moveToFirst()) {
            do {
                categoryModalArrayList.add(new DataModel(cursorCalculations.getInt(0),
                        cursorCalculations.getDouble(1),
                        cursorCalculations.getDouble(2),
                        cursorCalculations.getDouble(3)));
            } while (cursorCalculations.moveToNext());
        }

        cursorCalculations.close();

        return categoryModalArrayList;
    }

    public double readTotalBudgetAnalysis() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorBudgetOverall = db.rawQuery("SELECT SUM(cBudget) as Sum FROM " + cTABLE_NAME + " WHERE cType = 'Debit'", null);

        double budgetTotal = 0.0;

        if (cursorBudgetOverall.moveToFirst()) {
            do {
                budgetTotal = cursorBudgetOverall.getDouble(cursorBudgetOverall.getColumnIndex("Sum"));
            } while (cursorBudgetOverall.moveToNext());
        }

        return budgetTotal;
    }

    public void deleteCategory(String categoryId) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(cTABLE_NAME, "cId=?", new String[]{categoryId});
        db.close();
    }

    public void deleteTransaction(String transactionId) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(tTABLE_NAME, "tId=?", new String[]{transactionId});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + cTABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + tTABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + totTABLE_NAME);
        onCreate(db);
    }

}
