package com.example.meta_knight.moneymanagerdatabase;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;

import javax.microedition.khronos.opengles.GL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CompanyDash extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Item> cartList;
    private CartListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;

    private String GlobalCompCUID;
    private int GlobalItemID = 0;
    private String email = null;
    private String uid = null;
    private String GlobalRole = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_dash);
        AddListenerSnapshot();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            GlobalCompCUID = bundle.getString("companyID");
            uid = bundle.getString("uid");
            email = bundle.getString("email");
        } else {
            Toast.makeText(this, "Got Null Bundle", Toast.LENGTH_SHORT).show();
        }
        InitiateRecylerView();
        AddListenerSnapshot();
    }

    private void AddListenerSnapshot() {
        DocumentReference mDocR = FirebaseFirestore.getInstance().document("users/" + uid + "/companies/" + GlobalCompCUID);
        mDocR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                GlobalRole = documentSnapshot.getString("role");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CompanyDash.this, "Error While trying to get User Data", Toast.LENGTH_SHORT).show();
            }
        });

        if (GlobalRole != null) {
            CollectionReference mDocRef = FirebaseFirestore.getInstance().collection("companies/" + GlobalCompCUID + "/expenses");
            mDocRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    List<DocumentSnapshot> QueryList = documentSnapshots.getDocuments();
                    List<Item> ViewItems = new ArrayList<>();
                    if (!QueryList.isEmpty()) {
                        if (Objects.equals(GlobalRole, "admin")) {
                            for (DocumentSnapshot i : QueryList) {
                                Item TempDocSnapShot = new Item();
                                GlobalItemID += 1;
                                TempDocSnapShot.setId(GlobalItemID);
                                TempDocSnapShot.setName(i.getString("title"));
                                TempDocSnapShot.setDescription(i.getString("description") + "\nOwner • " + i.getString("ownerEmail"));
                                TempDocSnapShot.setPrice(i.getDouble("amount"));
                                TempDocSnapShot.setEid(i.getString("eid"));
                                ViewItems.add(TempDocSnapShot);
                            }
                            if (ViewItems != null) {
                                AddItems(ViewItems);
                            }
                        } else if (Objects.equals(GlobalRole, "employ")) {
                            for (DocumentSnapshot i : QueryList) {
                                if (Objects.equals(email, i.getString("ownerEmail"))) {
                                    Item TempDocSnapShot = new Item();
                                    GlobalItemID += 1;
                                    TempDocSnapShot.setId(GlobalItemID);
                                    TempDocSnapShot.setName(i.getString("title"));
                                    TempDocSnapShot.setDescription(i.getString("description") + "\nOwner • " + i.getString("ownerEmail"));
                                    TempDocSnapShot.setEid(i.getString("eid"));
                                    TempDocSnapShot.setPrice(i.getDouble("amount"));
                                    ViewItems.add(TempDocSnapShot);
                                }
                            }
                            if (ViewItems != null) {
                                AddItems(ViewItems);
                            }
                        } else {
                            Toast.makeText(CompanyDash.this, "ERROR WHILE GETTING DATA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, "ERROR WHILE GETTING DATA", Toast.LENGTH_SHORT).show();
        }
    }

    private void InitiateRecylerView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Company Expenses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<>();
        mAdapter = new CartListAdapter(this, cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    private void AddItems(List<Item> InputList) {
        cartList.clear();
        cartList.addAll(InputList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartListAdapter.MyViewHolder) {
            String name = cartList.get(viewHolder.getAdapterPosition()).getName();
            final Item deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from expense list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdapter.restoreItem(deletedItem, deletedIndex);

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            GlobalItemID = GlobalItemID - 1;
            DeleteExpense(deletedItem.getEid());
        }
    }

    public void OnNewExpenseClicked(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View AlertLayout = inflater.inflate(R.layout.create_expense_dialog, null);
        final EditText ExpenseName = AlertLayout.findViewById(R.id.expense_name);
        final EditText ExpensePrice = AlertLayout.findViewById(R.id.expense_price);
        final EditText ExpenseDesc = AlertLayout.findViewById(R.id.expense_desc);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Create New Expense");
        alert.setView(AlertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(CompanyDash.this, "Exit Clicked.", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String ExpenseNameFinal = ExpenseName.getText().toString();
                String ExpensePriceFinal = ExpensePrice.getText().toString();
                String ExpenseDescFinal = ExpenseDesc.getText().toString();

                if (ExpenseNameFinal.trim().length() > 0 && ExpensePriceFinal.trim().length() > 0 && ExpenseDescFinal.trim().length() > 0) {
                    Date TodayDate = Calendar.getInstance().getTime();
                    CreateExpense(Double.parseDouble(ExpensePriceFinal), ExpenseDescFinal, ExpenseNameFinal, -1, TodayDate, GlobalCompCUID);
                } else {
                    Toast.makeText(CompanyDash.this, "Expense Could NOT be created Please Enter Something other then whitespace", Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private double AmountExp;
    private String Desc;
    private String TitleExp;
    private int CategoryExp;
    private Date ExpDate;
    private String InCuid;
    private String CombinedStrExp;
    private void CreateExpense(double Amount, String Description, String title, int Category, Date ExpenseDate, final String InputCid) {
        AmountExp = Amount;
        Desc = Description;
        TitleExp = title;
        CategoryExp = Category;
        ExpDate = ExpenseDate;
        InCuid = InputCid;
        CombinedStrExp = String.valueOf(Amount) + Description + title + String.valueOf(Category) + ExpenseDate.toString() + InputCid;

        DocumentReference mDocExpenseRef = FirebaseFirestore.getInstance().document("companies/" + InputCid + "/expenses/" + "e" + sha256(CombinedStrExp));
        mDocExpenseRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    Map<String, Object> mDocExpenseData = new HashMap<>();
                    DocumentReference mDocExpenseRef = FirebaseFirestore.getInstance().document("companies/" + InCuid + "/expenses/" + "e" + sha256(CombinedStrExp));
                    mDocExpenseData.put("amount", AmountExp);
                    mDocExpenseData.put("title", TitleExp);
                    mDocExpenseData.put("date", ExpDate.toString());
                    mDocExpenseData.put("ownerEmail", email);
                    mDocExpenseData.put("description", Desc);
                    mDocExpenseData.put("category", CategoryExp);
                    mDocExpenseData.put("eid", "e" + sha256(CombinedStrExp));
                    mDocExpenseRef.set(mDocExpenseData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CompanyDash.this, "Created New Expense!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CompanyDash.this, "FAILED to create new Expense!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CompanyDash.this, "Expense Already Exists!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CompanyDash.this, "FAILED to Create New Expense", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String sha256(String inputString) {
        return (Hashing.sha256().hashString(inputString, StandardCharsets.UTF_8).toString());
    }

    private void DeleteExpense(String Eid) {
        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/expenses/" + Eid);
        mDocRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CompanyDash.this, "Deleted Expense!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CompanyDash.this, "ERROR WHILE DELETING EXPENSE!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
