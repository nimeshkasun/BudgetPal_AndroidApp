package com.example.budgetpal.categories;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetpal.DataModel;
import com.example.budgetpal.DbConn;
import com.example.budgetpal.MainActivity;
import com.example.budgetpal.R;
import com.example.budgetpal.transactions.Transactions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Categories extends AppCompatActivity {

    RecyclerView recyclerViewCat;
    RecyclerAdapter recyclerAdapterCat;

    ArrayList<DataModel> recordsList;
    private DbConn dbConn;

    private FloatingActionButton btnAddCategory;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText AddCatName, AddCatDescription, AddCatBudget, EditCatName, EditCatDescription, EditCatBudget;
    private RadioButton radioAddCatCreditType, radioAddCatDebitType, radioEditCatCreditType, radioEditCatDebitType;
    private Button btnAddCatSave, btnAddCatCancel, btnEditCatSave, btnEditCatCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.categories);
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
                        startActivity(new Intent(getApplicationContext()
                                , Transactions.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.categories:
                        return true;
                }
                return false;
            }
        });

        recordsList = new ArrayList<>();
        dbConn = new DbConn(Categories.this);
        recordsList = dbConn.readCategories();

        recyclerViewCat = findViewById(R.id.recyclerViewCat);
        recyclerAdapterCat = new RecyclerAdapter(recordsList);
        recyclerViewCat.setAdapter(recyclerAdapterCat);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerViewCat.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewCat);

        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCategoryDialog();
            }
        });

    }

    public void confirmDelete(int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure to Delete?")
                .setMessage("Record " + position + " will be deleted permanently.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DataModel modal = recordsList.get(position);
                        dbConn.deleteCategory(String.valueOf(modal.getCatId()));

                        recordsList.remove(position);
                        recyclerAdapterCat.notifyItemRemoved(position);
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
                    EditCategoryDialog(position);
                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(Categories.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(Categories.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(Categories.this, R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_edit_white_24dp)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private String catType;

    public void addNewCategoryDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View addCategoryPopupView = getLayoutInflater().inflate(R.layout.add_category, null);

        AddCatName = (EditText) addCategoryPopupView.findViewById(R.id.AddCatName);
        AddCatDescription = (EditText) addCategoryPopupView.findViewById(R.id.AddCatDescription);
        radioAddCatCreditType = (RadioButton) addCategoryPopupView.findViewById(R.id.radioAddCatCreditType);
        radioAddCatDebitType = (RadioButton) addCategoryPopupView.findViewById(R.id.radioAddCatDebitType);
        AddCatBudget = (EditText) addCategoryPopupView.findViewById(R.id.AddCatBudget);
        btnAddCatSave = (Button) addCategoryPopupView.findViewById(R.id.btnAddCatSave);
        btnAddCatCancel = (Button) addCategoryPopupView.findViewById(R.id.btnAddCatCancel);

        radioAddCatCreditType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    catType = "Credit";
                    AddCatBudget.setVisibility(View.INVISIBLE);
                    AddCatBudget.setText("0.0");
                }
            }
        });

        radioAddCatDebitType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    catType = "Debit";
                    AddCatBudget.setVisibility(View.VISIBLE);

                    double leftBudgetForNewCategory = dbConn.readLeftBudgetForNewCategory();

                    AddCatBudget.setHint("Budget (" + leftBudgetForNewCategory + " left to allocate)");
                }
            }
        });

        dialogBuilder.setView(addCategoryPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnAddCatSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(AddCatName.getText())) {
                    AddCatName.setError("Mandatory field - Enter Category name");
                } else if (TextUtils.isEmpty(AddCatDescription.getText())) {
                    AddCatDescription.setError("Mandatory field - Enter Category description");
                } else if (!(radioAddCatCreditType.isChecked() || radioAddCatDebitType.isChecked())) {
                    radioAddCatCreditType.setError("Mandatory field - Select Credit/Debit");
                    radioAddCatDebitType.setError("Mandatory field - Select Credit/Debit");
                } else {
                    recreate();
                    dbConn.addNewCategory(AddCatName.getText().toString(), AddCatDescription.getText().toString(), String.valueOf(catType), Double.parseDouble(AddCatBudget.getText().toString()));
                }
            }
        });

        btnAddCatCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel code
                dialog.dismiss();
            }
        });
    }

    public void EditCategoryDialog(int position) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View editCategoryPopupView = getLayoutInflater().inflate(R.layout.edit_category, null);

        EditCatName = (EditText) editCategoryPopupView.findViewById(R.id.EditCatName);
        EditCatDescription = (EditText) editCategoryPopupView.findViewById(R.id.EditCatDescription);
        radioEditCatCreditType = (RadioButton) editCategoryPopupView.findViewById(R.id.radioEditCatCreditType);
        radioEditCatDebitType = (RadioButton) editCategoryPopupView.findViewById(R.id.radioEditCatDebitType);
        EditCatBudget = (EditText) editCategoryPopupView.findViewById(R.id.EditCatBudget);
        btnEditCatSave = (Button) editCategoryPopupView.findViewById(R.id.btnEditCatSave);
        btnEditCatCancel = (Button) editCategoryPopupView.findViewById(R.id.btnEditCatCancel);

        DataModel modal = recordsList.get(position);
        EditCatName.setText(modal.getCatName());
        EditCatDescription.setText(modal.getCatDescription());

        if (modal.getCatType().equals("Credit")) {
            radioEditCatCreditType.setChecked(true);
            EditCatBudget.setVisibility(View.INVISIBLE);
            EditCatBudget.setText("0.0");
        } else if (modal.getCatType().equals("Debit")) {
            radioEditCatDebitType.setChecked(true);
            EditCatBudget.setVisibility(View.VISIBLE);
            EditCatBudget.setText(String.valueOf(modal.getCatBudget()));
        }

        radioEditCatCreditType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    catType = "Credit";
                    EditCatBudget.setVisibility(View.INVISIBLE);
                    EditCatBudget.setText("0.0");
                }
            }
        });

        radioEditCatDebitType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    catType = "Debit";
                    EditCatBudget.setVisibility(View.VISIBLE);
                    double leftBudgetForNewCategory = dbConn.readLeftBudgetForNewCategory();

                    EditCatBudget.setHint("Budget (" + leftBudgetForNewCategory + " left to allocate)");
                }
            }
        });

        dialogBuilder.setView(editCategoryPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnEditCatSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(EditCatName.getText())) {
                    EditCatName.setError("Mandatory field - Enter Category name");
                } else if (TextUtils.isEmpty(EditCatDescription.getText())) {
                    EditCatDescription.setError("Mandatory field - Enter Category description");
                } else if (!(radioEditCatCreditType.isChecked() || radioEditCatDebitType.isChecked())) {
                    radioEditCatCreditType.setError("Mandatory field - Select Credit/Debit");
                    radioEditCatDebitType.setError("Mandatory field - Select Credit/Debit");
                } else {
                    recreate();
                    DataModel modal = recordsList.get(position);
                    dbConn.updateCategory(modal.getCatId(), EditCatName.getText().toString(), EditCatDescription.getText().toString(), String.valueOf(catType), Double.parseDouble(EditCatBudget.getText().toString()));
                }
            }
        });

        btnEditCatCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel code
                dialog.dismiss();
                recreate();
            }
        });
    }

}
