package com.example.meta_knight.moneymanagerdatabase;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;

import javax.microedition.khronos.opengles.GL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.media.CamcorderProfile.get;

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
        if (uid != null && GlobalCompCUID != null) {
            AddListenerSnapshot();
        } else {
            Log.wtf("UIDGLOBAL", "VALUE NULL NOOOO");
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu MainMenu = bottomNavigationView.getMenu();
        MenuItem currentItem = MainMenu.getItem(0);
        currentItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ExpenseTab:
                        break;
                    case R.id.GraphTab:
                        break;
                    case R.id.PeopleTab:
                        StartManagePeopleActivity();
                        break;
                    case R.id.HistoryTab:
                        StartHistoryViewActivity();
                        break;
                }
                return false;
            }
        });
    }
    private void AddListenerSnapshot() {
        if (uid != null && GlobalCompCUID != null) {
            DocumentReference mDocR = FirebaseFirestore.getInstance().document("users/" + uid + "/companies/" + GlobalCompCUID);
            Log.wtf("INFO", "users/" + uid + "/companies/" + GlobalCompCUID);
            mDocR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    GlobalRole = documentSnapshot.getString("role");
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
                                        TempDocSnapShot.setDescription(i.getString("description") + "\nOwner • " + i.getString("ownerEmail") + "\nCategory • " + GetCategoryFromInt(i.getString("category")) + " | Status • " + GetStatusFromInt(i.getDouble("status")));
                                        TempDocSnapShot.setPrice(i.getDouble("amount"));
                                        TempDocSnapShot.setEid(i.getString("eid"));
                                        TempDocSnapShot.setThumbnail(GetUriFromString(GetCategoryFromInt(i.getString("category"))));
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
                                            TempDocSnapShot.setDescription(i.getString("description") + "\nOwner • " + i.getString("ownerEmail") + "\nCategory • " + GetCategoryFromInt(i.getString("category")) + " | Status • " + GetStatusFromInt(i.getDouble("status")));
                                            TempDocSnapShot.setEid(i.getString("eid"));
                                            TempDocSnapShot.setPrice(i.getDouble("amount"));
                                            TempDocSnapShot.setThumbnail(GetUriFromString(GetCategoryFromInt(i.getString("category"))));
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
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CompanyDash.this, "Error While trying to get User Data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String GetCategoryFromInt(String InputString) {
        int InputInt = Integer.parseInt(InputString);
        if (InputInt == 1) {
            return "travel";
        } else if (InputInt == 2) {
            return "food";
        } else if (InputInt == 3) {
            return "electronics";
        } else if (InputInt == 4) {
            return "software";
        } else if (InputInt == 5) {
            return "other";
        } else {
            return "UNKNOWN";
        }
    }

    private String GetStatusFromInt(double InputInt) {
        if (InputInt == 1) {
            return "paid";
        } else if (InputInt == -1) {
            return "not paid";
        } else {
            return "UNKNOWN";
        }
    }

    private String GetUriFromString(String InputString) {
        if (InputString == "travel") {
            return "https://png.icons8.com/airport/dusk/64";
        } else if (InputString == "food") {
            return "https://png.icons8.com/food/dusk/64";
        } else if (InputString == "electronics") {
            return "https://png.icons8.com/motherboard/dusk/64";
        } else if (InputString == "software") {
            return "https://png.icons8.com/software-installer/dusk/64";
        } else if (InputString == "other") {
            return "http://guineeservices.com/wp-content/uploads/2015/05/Misc-Icon.png";
        } else {
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Icon-round-Question_mark.svg/200px-Icon-round-Question_mark.svg.png";
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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Item ItemSelected = cartList.get(position);
                LayoutInflater inflater = getLayoutInflater();
                View AlertLayout = inflater.inflate(R.layout.expense_details_dialog, null);
                final Spinner mSpinnerRef= AlertLayout.findViewById(R.id.status_spinner);
                List<String> listStatusSpinner = new ArrayList<String>();
                listStatusSpinner.add("paid");
                listStatusSpinner.add("not paid");
                ArrayAdapter<String> dataAdapterCategory = new ArrayAdapter<String>(CompanyDash.this,
                        android.R.layout.simple_spinner_item, listStatusSpinner);
                dataAdapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerRef.setAdapter(dataAdapterCategory);

                AlertDialog.Builder alert = new AlertDialog.Builder(CompanyDash.this);
                alert.setTitle("Edit Expense");
                alert.setView(AlertLayout);
                alert.setCancelable(false);
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alert.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mSpinnerRef.getSelectedItem().toString() == "paid") {
                            ChangeStatus(ItemSelected.getEid(), 1.0d);
                        } else if (mSpinnerRef.getSelectedItem().toString() == "not paid") {
                            ChangeStatus(ItemSelected.getEid(), -1.0d);
                        } else {
                            Toast.makeText(CompanyDash.this, "UNABLE TO CHANGE STATUS", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            }
            @Override
            public void onLongClick(View view, int position) {
                final Item ItemSelected = cartList.get(position);
            }
        }));
        recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);
    }

    Map<String, Object> InputDataDocFoo = new HashMap<>();
    private void CopyDocumentContents(String InputDocument, String OutputDocument) {
        final DocumentReference mDocRefInput = FirebaseFirestore.getInstance().document(InputDocument);
        final DocumentReference mDocRefOutput = FirebaseFirestore.getInstance().document(OutputDocument);

        mDocRefInput.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                InputDataDocFoo = documentSnapshot.getData();
                mDocRefOutput.set(InputDataDocFoo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    double NewStatus;
    String InputGlobEid;
    Date today;
    Calendar calenTemp;
    private void ChangeStatus(String InputGlobEidIn, final double NewStatusIn) {
        Date todayIn = Calendar.getInstance().getTime();
        Calendar calen = Calendar.getInstance();
        NewStatus = NewStatusIn;
        InputGlobEid = InputGlobEidIn;
        calen.setTime(todayIn);
        today = todayIn;
        calenTemp = calen;
        final DocumentReference mDocBeforeRef = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/history/" + "ee" + sha256(InputGlobEid + calen.getTimeInMillis()) + "/before/before");
        DocumentReference mDocCopyContents = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/expenses/" + InputGlobEid);
        mDocCopyContents.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getDouble("status") == NewStatusIn) {
                    Toast.makeText(CompanyDash.this, "No Changes Made", Toast.LENGTH_SHORT).show();
                    return;
                }
                InputDataDocFoo = documentSnapshot.getData();
                mDocBeforeRef.set(InputDataDocFoo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference mDocRefStatus = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/expenses/" + InputGlobEid);
                        mDocRefStatus.update("status", NewStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CompanyDash.this, "Updated Expense Status", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CompanyDash.this, "FAILED TO UPDATE EXPENSE STATUS", Toast.LENGTH_SHORT).show();
                            }
                        });

                        DocumentReference mDocBeforeRefa = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/history/" + "ee" + sha256(InputGlobEid + calenTemp.getTimeInMillis()) + "/after/after");
                        DocumentReference mDocCopyContentsa = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/expenses/" + InputGlobEid);
                        CopyDocumentContents(mDocCopyContentsa.getPath(), mDocBeforeRefa.getPath());

                        DocumentReference mDocGetRootExpenseData = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/history/" + "ee" + sha256(InputGlobEid + calenTemp.getTimeInMillis()));
                        Map <String, Object> mDocExpenseData = new HashMap<>();
                        mDocExpenseData.put("type", "edit");
                        mDocExpenseData.put("date", today.toString());
                        mDocExpenseData.put("ownerEmail", email);
                        mDocExpenseData.put("title", documentSnapshot.getString("title"));
                        mDocGetRootExpenseData.set(mDocExpenseData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CompanyDash.this, "FAILED TO ADD EDIT", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
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
        final Spinner ExpenseStatus = AlertLayout.findViewById(R.id.status_spinner);
        final Spinner ExpenseCategory = AlertLayout.findViewById(R.id.category_spinner);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Create New Expense");
        alert.setView(AlertLayout);
        alert.setCancelable(false);

        List<String> listStatus = new ArrayList<String>();
        listStatus.add("paid");
        listStatus.add("not paid");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listStatus);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ExpenseStatus.setAdapter(dataAdapter);

        List<String> listCategory = new ArrayList<String>();
        listCategory.add("travel");
        listCategory.add("food");
        listCategory.add("electronics");
        listCategory.add("software");
        listCategory.add("other");
        ArrayAdapter<String> dataAdapterCategory = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listCategory);
        dataAdapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ExpenseCategory.setAdapter(dataAdapterCategory);

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
                final double MainStatusSpinner;
                final int CategoryMainCreate;
                if (ExpenseStatus.getSelectedItem().toString() == "paid") {
                    MainStatusSpinner = 1.0d;
                } else if (ExpenseStatus.getSelectedItem().toString() == "not paid") {
                    MainStatusSpinner = -1.0d;
                } else {
                    MainStatusSpinner = -100000d;
                }

                if (ExpenseCategory.getSelectedItem().toString() == "travel") {
                    CategoryMainCreate = 1;
                } else if (ExpenseCategory.getSelectedItem().toString() == "food") {
                    CategoryMainCreate = 2;
                } else if (ExpenseCategory.getSelectedItem().toString() == "electronics") {
                    CategoryMainCreate = 3;
                } else if (ExpenseCategory.getSelectedItem().toString() == "software") {
                    CategoryMainCreate = 4;
                } else if (ExpenseCategory.getSelectedItem().toString() == "other") {
                    CategoryMainCreate  = 5;
                } else {
                    CategoryMainCreate = -100000;
                }
                if (ExpenseNameFinal.trim().length() > 0 && ExpensePriceFinal.trim().length() > 0 && ExpenseDescFinal.trim().length() > 0) {
                    Date TodayDate = Calendar.getInstance().getTime();
                    CreateExpense(Double.parseDouble(ExpensePriceFinal), ExpenseDescFinal, ExpenseNameFinal, CategoryMainCreate, TodayDate, GlobalCompCUID, MainStatusSpinner);
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
    private double InStatus;
    private void CreateExpense(double Amount, String Description, String title, int Category, Date ExpenseDate, final String InputCid, double InputStatus) {
        AmountExp = Amount;
        Desc = Description;
        TitleExp = title;
        CategoryExp = Category;
        ExpDate = ExpenseDate;
        InCuid = InputCid;
        CombinedStrExp = String.valueOf(Amount) + Description + title + String.valueOf(Category) + ExpenseDate.toString() + InputCid;
        InStatus = InputStatus;

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
                    mDocExpenseData.put("category", String.valueOf(CategoryExp));
                    mDocExpenseData.put("eid", "e" + sha256(CombinedStrExp));
                    mDocExpenseData.put("status", InStatus);
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
                    Calendar calen = Calendar.getInstance();
                    calen.setTime(ExpDate);
                    DocumentReference mDocRef = FirebaseFirestore.getInstance().document("companies/" + InCuid + "/history/" + "ae" + sha256(CombinedStrExp + calen.getTimeInMillis()));
                    mDocExpenseData.put("type", "append");
                    mDocExpenseData.put("date", ExpDate.toString());
                    mDocExpenseData.put("expName", TitleExp);
                            mDocRef.set(mDocExpenseData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CompanyDash.this, "Added Data in History", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CompanyDash.this, "Failed to add Data to history", Toast.LENGTH_SHORT).show();
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
    private double AmountExpD;
    private String DescD;
    private String TitleExpD;
    private String CategoryExpD;
    private String ExpDateD;
    private String InCuidD;
    private String CompletedDeleteEID;
    private Date ExpDataObjD;
    private String CombinedStrExpD;
    private double mInStatus;
    private void DeleteExpense(String Eid) {
        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/expenses/" + Eid);
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> mDocExpenseData = new HashMap<>();
                    AmountExpD = documentSnapshot.getDouble("amount");
                    TitleExpD = documentSnapshot.getString("title");
                    ExpDateD = documentSnapshot.getString("date");
                    email = documentSnapshot.getString("ownerEmail");
                    DescD = documentSnapshot.getString("description");
                    CategoryExpD = documentSnapshot.getString("category");
                    CompletedDeleteEID = documentSnapshot.getString("eid");
                    mInStatus = documentSnapshot.getDouble("status");
                    try {
                        ExpDataObjD = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(ExpDateD);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    CombinedStrExp = String.valueOf(AmountExpD) + DescD + TitleExpD + String.valueOf(CategoryExpD) + ExpDateD + GlobalCompCUID;
                    mDocExpenseData.put("amount", AmountExpD);
                    mDocExpenseData.put("title", TitleExpD);
                    mDocExpenseData.put("date", ExpDateD.toString());
                    mDocExpenseData.put("ownerEmail", email);
                    mDocExpenseData.put("description", DescD);
                    mDocExpenseData.put("category", CategoryExpD);
                    mDocExpenseData.put("eid", CompletedDeleteEID);
                    mDocExpenseData.put("status", mInStatus);
                    Calendar calen = Calendar.getInstance();
                    calen.setTime(ExpDataObjD);
                    DocumentReference mDocRef = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/history/" + "de" + sha256(CombinedStrExp + calen.getTimeInMillis()));
                    Date TodayDate = Calendar.getInstance().getTime();
                    mDocExpenseData.put("type", "delete");
                    mDocExpenseData.put("date", TodayDate.toString());
                    mDocExpenseData.put("expName", TitleExpD);
                            mDocRef.set(mDocExpenseData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CompanyDash.this, "Added Data in History", Toast.LENGTH_SHORT).show();
                            DocumentReference mDocRefDeleteDocument = FirebaseFirestore.getInstance().document("companies/" + GlobalCompCUID + "/expenses/" + CompletedDeleteEID);
                            mDocRefDeleteDocument.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CompanyDash.this, "Failed to add Data to history", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CompanyDash.this, "ERROR While trying to delete expense", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CompanyDash.this, "ERROR WHILE ADDING TO HISTORY", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void StartHistoryViewActivity() {
        Intent StartHistoryView = new Intent(CompanyDash.this, HistoryView.class);
        StartHistoryView.putExtra("companyID", GlobalCompCUID);
        StartHistoryView.putExtra("uid", uid);
        StartHistoryView.putExtra("email", email);
        StartHistoryView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(StartHistoryView);
    }

    private void StartManagePeopleActivity() {
        Intent ManagePeopleIntent = new Intent(CompanyDash.this, manage_people.class);
        ManagePeopleIntent.putExtra("companyID", GlobalCompCUID);
        ManagePeopleIntent.putExtra("uid", uid);
        ManagePeopleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(ManagePeopleIntent);
    }
}
