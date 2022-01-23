package com.example.budgetpal.transactions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.budgetpal.DataModel;
import com.example.budgetpal.DbConn;
import com.example.budgetpal.MainActivity;
import com.example.budgetpal.R;
import com.example.budgetpal.categories.Categories;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Transactions extends AppCompatActivity {

    private FloatingActionButton btnAddTransaction;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    Spinner AddTransCategory, EditTransCategory;
    private EditText AddTransDescription, AddTransPrice, EditTransDescription, EditTransPrice;
    private Switch AddTransRecurring, EditTransRecurring;
    private Button AddTransDate, btnAddTransSave, btnAddTransCancel, EditTransDate, btnEditTransSave, btnEditTransCancel;
    private DatePickerDialog datePickerDialog;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    ArrayList<DataModel> recordsList, recordsListCatNames;
    private DbConn dbConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.transactions);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.summary:
                        startActivity(new Intent(getApplicationContext()
                                , MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.transactions:
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

        recordsList = new ArrayList<>();
        dbConn = new DbConn(Transactions.this);
        recordsList = dbConn.readTransactions();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(recordsList);
        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        btnAddTransaction = findViewById(R.id.btnAddTransaction);
        btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTransactionDialog();
            }
        });

        initDatePicker();
        AddTransDate = findViewById(R.id.AddTransDate);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstRun", false)) {
            dbConn.addSampleCategoryData();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstRun", true);
            editor.commit();
        }

    }

    public void confirmDelete(int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure to Delete?")
                .setMessage("Record " + position + " will be deleted permanently.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //deletedRecord = recordsList.get(position);
                        DataModel modal = recordsList.get(position);
                        dbConn.deleteTransaction(String.valueOf(modal.getTranId()));

                        recordsList.remove(position);
                        recyclerAdapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recreate();
                    }
                })
                .show();
    }

    String deletedRecord = null;
    List<String> archivedRecords = new ArrayList<>();

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    confirmDelete(position);
                    break;

                case ItemTouchHelper.RIGHT:
                    editTransactionDialog(position);
                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(Transactions.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(Transactions.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(Transactions.this, R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_edit_white_24dp)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void addNewTransactionDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View addTransactionPopupView = getLayoutInflater().inflate(R.layout.add_transaction, null);

        AddTransCategory = (Spinner) addTransactionPopupView.findViewById(R.id.AddTransCategory);
        AddTransDate = (Button) addTransactionPopupView.findViewById(R.id.AddTransDate);
        AddTransDescription = (EditText) addTransactionPopupView.findViewById(R.id.AddTransDescription);
        AddTransRecurring = (Switch) addTransactionPopupView.findViewById(R.id.AddTransRecurring);
        AddTransPrice = (EditText) addTransactionPopupView.findViewById(R.id.AddTransPrice);
        btnAddTransSave = (Button) addTransactionPopupView.findViewById(R.id.btnAddTransSave);
        btnAddTransCancel = (Button) addTransactionPopupView.findViewById(R.id.btnAddTransCancel);

        AddTransDate.setText(getTodaysDate());

        recordsList = new ArrayList<>();
        dbConn = new DbConn(Transactions.this);
        recordsList = dbConn.readCategoryNamesForSpinner();
        ArrayList<String> categoryNames = new ArrayList<>();

        for (int i = 0; i < recordsList.size(); i++) {
            DataModel modal = recordsList.get(i);
            categoryNames.add(modal.getCatName());
            Log.i("CateName", modal.getCatName());
        }
        ArrayAdapter<String> categoryNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        categoryNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AddTransCategory.setAdapter(categoryNamesAdapter);

        dialogBuilder.setView(addTransactionPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnAddTransSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(AddTransDescription.getText())) {
                    AddTransDescription.setError("Mandatory field - Enter Transaction description");
                } else if (TextUtils.isEmpty(AddTransPrice.getText())) {
                    AddTransPrice.setError("Mandatory field - Enter Transaction price");
                } else {
                    recreate();
                    dbConn.addNewTransaction(AddTransCategory.getSelectedItem().toString(), String.valueOf(AddTransDate.getText()), AddTransDescription.getText().toString(), String.valueOf(AddTransRecurring.isChecked()), Double.parseDouble(AddTransPrice.getText().toString()));
                }
            }
        });

        btnAddTransCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void editTransactionDialog(int position) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View editTransactionPopupView = getLayoutInflater().inflate(R.layout.edit_transaction, null);

        EditTransCategory = (Spinner) editTransactionPopupView.findViewById(R.id.EditTransCategory);
        EditTransDate = (Button) editTransactionPopupView.findViewById(R.id.EditTransDate);
        EditTransDescription = (EditText) editTransactionPopupView.findViewById(R.id.EditTransDescription);
        EditTransRecurring = (Switch) editTransactionPopupView.findViewById(R.id.EditTransRecurring);
        EditTransPrice = (EditText) editTransactionPopupView.findViewById(R.id.EditTransPrice);
        btnEditTransSave = (Button) editTransactionPopupView.findViewById(R.id.btnEditTransSave);
        btnEditTransCancel = (Button) editTransactionPopupView.findViewById(R.id.btnEditTransCancel);

        DataModel modal = recordsList.get(position);
        EditTransDate.setText(modal.getTranDate());
        EditTransDescription.setText(modal.getTranDescription());
        EditTransPrice.setText(String.valueOf(modal.getTransPrice()));
        EditTransRecurring.setChecked(Boolean.parseBoolean(modal.getTranRecurring()));

        recordsListCatNames = new ArrayList<>();
        dbConn = new DbConn(Transactions.this);
        recordsListCatNames = dbConn.readCategoryNamesForSpinner();
        ArrayList<String> categoryNames = new ArrayList<>();

        for (int i = 0; i < recordsListCatNames.size(); i++) {
            DataModel modalCatNames = recordsListCatNames.get(i);
            categoryNames.add(modalCatNames.getCatName());
        }
        ArrayAdapter<String> categoryNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        categoryNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditTransCategory.setAdapter(categoryNamesAdapter);

        for (int i = 0; i < recordsListCatNames.size(); i++) {
            DataModel modalCatNames = recordsListCatNames.get(i);
            if (modalCatNames.getCatName().equals(modal.getTranCategory())) {
                EditTransCategory.setSelection(i);
            }
        }


        dialogBuilder.setView(editTransactionPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnEditTransSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(EditTransDescription.getText())) {
                    EditTransDescription.setError("Mandatory field - Enter Transaction description");
                } else if (TextUtils.isEmpty(EditTransPrice.getText())) {
                    EditTransPrice.setError("Mandatory field - Enter Transaction price");
                } else {
                    recreate();
                    //Save code
                    DataModel modal = recordsList.get(position);
                    dbConn.updateTransaction(modal.getTranId(), EditTransCategory.getSelectedItem().toString(), String.valueOf(EditTransDate.getText()), EditTransDescription.getText().toString(), String.valueOf(EditTransRecurring.isChecked()), Double.parseDouble(EditTransPrice.getText().toString()));
                }
            }
        });

        btnEditTransCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel code
                dialog.dismiss();
                recreate();
            }
        });
    }


    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                try {
                    AddTransDate.setText(date);
                } catch (Exception e) {
                    EditTransDate.setText(date);
                }
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = android.R.style.Theme_Holo_Light_Dialog;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}
