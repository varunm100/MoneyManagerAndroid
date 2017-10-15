package com.example.meta_knight.moneymanagerdatabase;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.util.*;


public class PersonDash extends AppCompatActivity {
    private String email = null;
    private String name = null;
    private String uid = null;
    private String PhotoURL = null;


    private List<CompanyItem> companyList = new ArrayList<>();

    private RecyclerView recyclerView;
    private CompaniesAdapter mAdapter;
    List <CompanyItem> CompList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            email = bundle.getString("email");
            name = bundle.getString("name");
            uid = bundle.getString("uid");
            PhotoURL = bundle.getString("PhotoURL");
            Toast.makeText(PersonDash.this, email + "---" + name + "---" + uid, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(PersonDash.this, "Got Null Bundle", Toast.LENGTH_SHORT).show();
        }
        UserExist(uid);*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_dash);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new CompaniesAdapter(CompList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        ViewHelper();
    }

    private void ViewHelper() {
        CompanyItem Company = new CompanyItem("VarTech","varunm100@gmail.com");
        CompList.add(Company);
        Company = new CompanyItem("Arkham Stations","batmansuper@gmail.com");
        CompList.add(Company);
        Company = new CompanyItem("VarTech","varunm100@gmail.com");
        CompList.add(Company);
        Company = new CompanyItem("Zebi","babu@zebi.co");
        CompList.add(Company);
        Company = new CompanyItem("Personal","varunm11111@gmail.com");
        CompList.add(Company);
        Log.wtf("CALLED","mAdapter NOT WORKING YAYA");
        mAdapter.notifyDataSetChanged();
    }

    private void CreateUser(String UserId, String mailId, String UserName, String PhotURI) {
        DocumentReference aDocRef = FirebaseFirestore.getInstance().document("users/" + UserId);
        Map<String, Object> UserData = new HashMap<>();
        UserData.put("uid", UserId);
        UserData.put("email", mailId);
        UserData.put("name", UserName);
        UserData.put("photo", PhotURI);
        aDocRef.set(UserData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PersonDash.this, "Added New User!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PersonDash.this, "FAILED TO ADD USER!!!", Toast.LENGTH_SHORT).show();
            }
        });

        DocumentReference aCollRef = FirebaseFirestore.getInstance().document("users/" + UserId + "/companies/" + "null");
        Map<String, Object> EmptyCompany = new HashMap<>();
        EmptyCompany.put("CID", "null");
        aCollRef.set(EmptyCompany).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PersonDash.this, "Added Null Company", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PersonDash.this, "FAILED TO ADD NULL COMPANY", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String TempComp01 = null;
    private String CompanyUserId = null;
    private void CreateCompany(String CompanyName) {
        TempComp01 = CompanyName;
        CompanyUserId = sha256("c" + uid + TempComp01);
        DocumentReference TempCompReg = FirebaseFirestore.getInstance().document("companies/" + CompanyUserId);
        TempCompReg.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    DocumentReference CompanyRef = FirebaseFirestore.getInstance().document("companies/" + CompanyUserId);
                    Map<String, Object> CompanyDetails = new HashMap<>();
                    CompanyDetails.put("cname", TempComp01);
                    CompanyDetails.put("cid", CompanyUserId);
                    CompanyRef.set(CompanyDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PersonDash.this, "Created cname and CID", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PersonDash.this, "FAILED to Create cname and CID", Toast.LENGTH_SHORT).show();
                        }
                    });
                    DocumentReference ListPeople = FirebaseFirestore.getInstance().document("companies/" + CompanyUserId + "/people/" + uid);
                    Map<String, Object> PersonAdd = new HashMap<>();
                    PersonAdd.put("puid", uid);
                    PersonAdd.put("pname", name);
                    ListPeople.set(PersonAdd).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PersonDash.this, "Created people Collection", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PersonDash.this, "FAILED TO create people collection", Toast.LENGTH_LONG).show();
                        }
                    });

                    DocumentReference addCompanyList = FirebaseFirestore.getInstance().document("users/" + uid + "/companies/" + CompanyUserId);
                    Map<String, Object> AddPersonList = new HashMap<>();
                    AddPersonList.put("cid", CompanyUserId);
                    AddPersonList.put("cname", TempComp01);
                    addCompanyList.set(AddPersonList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PersonDash.this, "Added Company Collection", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PersonDash.this, "FAILED TO ADD COMPANY COLLECTION", Toast.LENGTH_SHORT).show();
                        }
                    });

                    DocumentReference addCatList = FirebaseFirestore.getInstance().document("companies/categories");
                    Map<String, Object> AddCatList = new HashMap<>();
                    AddCatList.put("NumCat", "5");
                    AddCatList.put("1", "travel");
                    AddCatList.put("2", "food");
                    AddCatList.put("3", "software licenses");
                    AddCatList.put("4", "electronics");
                    AddCatList.put("5","other");
                    addCatList.set(AddCatList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PersonDash.this, "Added Category List!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PersonDash.this, "FAILED to create Category List", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(PersonDash.this, "Failed to Verify if document Exists", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String cuidTemp = null;
    private void JoinCompany(String cuid) {
        cuidTemp = cuid;
        DocumentReference personComp = FirebaseFirestore.getInstance().document("companies/" + cuid);
        personComp.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Toast.makeText(PersonDash.this, "Company DOES NOT exist", Toast.LENGTH_SHORT).show();
                    DocumentReference addPersonCompany = FirebaseFirestore.getInstance().document("users/" + uid + "/companies/" + cuidTemp);
                    Map<String, Object> addPersonCompanyData = new HashMap<>();
                    addPersonCompanyData.put("cid", cuidTemp);
                    addPersonCompany.set(addPersonCompanyData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PersonDash.this, "Joined Compnay users/", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PersonDash.this, "FAILED TO JOIN COMPANY users/", Toast.LENGTH_SHORT).show();
                        }
                    });

                    DocumentReference addCompanyPerson = FirebaseFirestore.getInstance().document("companies/" + cuidTemp + "/people/" + uid);
                    Map<String, Object> addCompanyPersonMap = new HashMap<>();
                    addCompanyPersonMap.put("pname", name);
                    addCompanyPersonMap.put("puid", uid);
                    addCompanyPerson.set(addCompanyPersonMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PersonDash.this, "Joined Company", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PersonDash.this, "FAILED TO JOIN COMPANY", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(PersonDash.this, "Company Exist Not Creating NEW Company", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(PersonDash.this, "FAILED to Create Company", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UserExist(final String UserID) {
        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/" + UserID);
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Toast.makeText(PersonDash.this, "User Exists!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonDash.this, "User DOES NOT Exists!", Toast.LENGTH_SHORT).show();
                    CreateUser(uid, email, name, PhotoURL);
                }
            }
        });
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
                    mDocExpenseRef.set(mDocExpenseData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PersonDash.this, "Created New Expense!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PersonDash.this, "FAILED to create new Expense!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(PersonDash.this, "Expense Already Exists!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PersonDash.this, "FAILED to Create New Expense", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String sha256(String inputString) {
        return (Hashing.sha256().hashString(inputString, StandardCharsets.UTF_8).toString());
    }

    public void CreateCompanyClicked(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View AlertLayout = inflater.inflate(R.layout.create_company_dialog, null);
        final EditText CompName = AlertLayout.findViewById(R.id.comp_nameC);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Create New Company");
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
                String CompNameFinalStr = CompName.getText().toString();
                CreateCompany(CompNameFinalStr);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void JoinCompanyClicked(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View AlertLayout = inflater.inflate(R.layout.join_company_dialog, null);
        final EditText CompID = AlertLayout.findViewById(R.id.comp_sID);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Join A Company");
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
                String CompId = CompID.getText().toString();
                JoinCompany(CompId);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

}
