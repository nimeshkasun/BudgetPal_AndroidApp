package com.example.budgetpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.example.budgetpal.categories.Categories;
import com.example.budgetpal.transactions.Transactions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView currentBalance;

    ArrayList<DataModel> recordsList, recordsListCatNames, totalCalculations;
    private DbConn dbConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.summary);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.summary:
                        return true;
                    case R.id.transactions:
                        startActivity(new Intent(getApplicationContext()
                                , Transactions.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.categories:
                        startActivity(new Intent(getApplicationContext()
                                , Categories.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        totalCalculations = new ArrayList<>();
        dbConn = new DbConn(MainActivity.this);

        double totBalance = dbConn.readTotalBalance();
        currentBalance = findViewById(R.id.currentBalance);
        currentBalance.setText("Balance: " + String.valueOf(totBalance));

        double totalSpending = 0.0;
        totalCalculations = new ArrayList<>();
        dbConn = new DbConn(MainActivity.this);
        totalCalculations = dbConn.readTotalCalculations();
        List<DataEntry> dataOverall = new ArrayList<>();

        for (int i = 0; i < totalCalculations.size(); i++) {
            DataModel modalCalculations = totalCalculations.get(i);

            recordsList = new ArrayList<>();
            TextView totalCredit = findViewById(R.id.totalCredit);
            totalCredit.setText("Total Credit: " + modalCalculations.getTotCredit());
            TextView totalDebit = findViewById(R.id.totalDebit);
            totalDebit.setText("Total Debit: " + modalCalculations.getTotDebit());
            totalSpending = modalCalculations.getTotCredit();
        }


        dbConn = new DbConn(MainActivity.this);
        double totalBudgetOverall = dbConn.readTotalBudgetAnalysis();
        List<DataEntry> dataBudgetOverall = new ArrayList<>();

        dataBudgetOverall.add(new ValueDataEntry("Total Spending", totalSpending));
        dataBudgetOverall.add(new ValueDataEntry("Total Budget", totalBudgetOverall));

        Log.i("BUGETSUM", totalSpending + "--" + totalBudgetOverall);

        AnyChartView anyChartViewBudgetOverall = findViewById(R.id.overallBudgetSummary);
        Pie pieBudgetOverall = AnyChart.pie();
        pieBudgetOverall.data(dataBudgetOverall);
        anyChartViewBudgetOverall.setChart(pieBudgetOverall);


        recordsListCatNames = new ArrayList<>();
        dbConn = new DbConn(MainActivity.this);
        recordsListCatNames = dbConn.readDebitCategories();

        TableLayout table = (TableLayout) findViewById(R.id.TableLayout);


        for (int i = 0; i < recordsListCatNames.size(); i++) {
            DataModel modalCatNames = recordsListCatNames.get(i);

            recordsList = new ArrayList<>();
            double cost = dbConn.readTransactionCostPerCategory(modalCatNames.getCatName());
            double budget = dbConn.readBudgetPerCategory(modalCatNames.getCatName());

            Log.i("CATSUM", modalCatNames.getCatName() + "--" + budget + "--" + cost);

            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            float px;

            TableRow row = new TableRow(MainActivity.this);

            TextView c = new TextView(MainActivity.this);
            px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 170, getResources().getDisplayMetrics());
            c.setWidth(Math.round(px));
            Log.i("PIX1", String.valueOf(px) + "-" + String.valueOf(Math.round(px)));
            c.setText(String.valueOf(modalCatNames.getCatName()));

            TextView b = new TextView(MainActivity.this);
            px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 100, getResources().getDisplayMetrics());
            b.setWidth(Math.round(px));
            Log.i("PIX2", String.valueOf(px) + "-" + String.valueOf(Math.round(px)));
            b.setText(String.valueOf(budget));

            TextView s = new TextView(MainActivity.this);
            px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 100, getResources().getDisplayMetrics());
            s.setWidth(Math.round(px));
            Log.i("PIX3", String.valueOf(px) + "-" + String.valueOf(Math.round(px)));
            s.setText(String.valueOf(cost));

            row.addView(c);
            row.addView(b);
            row.addView(s);

            table.removeView(row);
            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        }

    }
}