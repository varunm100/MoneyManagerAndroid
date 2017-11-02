package com.example.meta_knight.moneymanagerdatabase;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.firestore.*;
import es.dmoral.toasty.Toasty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.*;

public class StatisticsGraphActivity extends AppCompatActivity {
    private String GlobalCompanyCUID = null;
    private String GlobalUID = null;
    private String GlobalEmail = null;

    private ArrayList<Float> yDataDistro = new ArrayList<>();
    private ArrayList<String> xDataDistro = new ArrayList<>();

    PieChart PieChartExpenseDistro;

    float TotalExpenses = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_graph);

        Bundle bundleExtra = getIntent().getExtras();
        if (bundleExtra != null) {
            GlobalCompanyCUID = bundleExtra.getString("companyID");
            GlobalUID = bundleExtra.getString("uid");
            GlobalEmail = bundleExtra.getString("email");
        } else {
            Toasty.error(this, "FOUND NULL BUNDLE").show();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu MainMenu = bottomNavigationView.getMenu();
        MenuItem currentItem = MainMenu.getItem(1);
        currentItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ExpenseTab:
                        GotoCompanyDash();
                        break;
                    case R.id.GraphTab:
                        break;
                    case R.id.PeopleTab:
                        GotoPeopleTab();
                        break;
                    case R.id.HistoryTab:
                        GotoHistoryTab();
                        break;
                }
                return false;
            }
        });

        SetupCharts();
    }

    private void SetupCharts() {
        PieChartExpenseDistro = (PieChart) findViewById(R.id.PieChartExpenseDistro);
        PieChartExpenseDistro.setRotationEnabled(true);
        PieChartExpenseDistro.setHoleRadius(25f);
        PieChartExpenseDistro.setUsePercentValues(true);
        PieChartExpenseDistro.setCenterTextSize(10);
        AddGraphListeners();
        PieChartExpenseDistro.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void AddDataPieDistro(ArrayList<Integer> colors) {
        /*xDataDistro.add("travel");
        xDataDistro.add("food");
        xDataDistro.add("electronics");
        xDataDistro.add("software");
        xDataDistro.add("other");*/
        ArrayList<PieEntry> TempYEntry = new ArrayList<>();
        ArrayList<String> TempXEntry = new ArrayList<>();
        if (yDataDistro.isEmpty() && xDataDistro.isEmpty()) {
            return;
        }
        Float [] TempYArr = yDataDistro.toArray(new Float[yDataDistro.size()]);
        String [] TempXArr = xDataDistro.toArray(new String[xDataDistro.size()]);

        for (int i = 0; i < TempYArr.length; ++i) {
            TempYEntry.add(new PieEntry(TempYArr[i], TempXArr[i]));
        }
        for (int i = 0; i < TempXArr.length; ++i) {
            TempXEntry.add(TempXArr[i]);
        }

        PieDataSet pieDataSet = new PieDataSet(TempYEntry, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        PieChartExpenseDistro.setData(pieData);
        PieChartExpenseDistro.invalidate();
    }

    private void AddGraphListeners() {
        CollectionReference mDocRef = FirebaseFirestore.getInstance().collection("companies/" + GlobalCompanyCUID + "/expenses/");
        mDocRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                List <DocumentSnapshot> mDocRefExpenses = new ArrayList<>();
                mDocRefExpenses = documentSnapshots.getDocuments();
                xDataDistro.clear();
                yDataDistro.clear();
                float TravelNum = 0.0f;
                float FoodNum = 0.0f;
                float ElectronicsNum = 0.0f;
                float SoftwareNum = 0.0f;
                float OtherNum = 0.0f;

                ArrayList<Integer> TempColors = new ArrayList<>();
                for (DocumentSnapshot i : mDocRefExpenses) {
                    if (GetStatusFromInt(i.getDouble("status")) == "paid") {
                        if (GetCategoryFromInt(i.getString("category")) == "travel") {
                            TravelNum+=i.getDouble("amount");
                        } else if (GetCategoryFromInt(i.getString("category")) == "food") {
                            FoodNum+=i.getDouble("amount");
                        } else if (GetCategoryFromInt(i.getString("category")) == "electronics") {
                            ElectronicsNum+=i.getDouble("amount");
                        } else if (GetCategoryFromInt(i.getString("category")) == "software") {
                            SoftwareNum+=i.getDouble("amount");
                        } else if (GetCategoryFromInt(i.getString("category")) == "other") {
                            OtherNum+=i.getDouble("amount");
                        }
                    }
                }
                if (TravelNum > 0.0f) {
                    yDataDistro.add(TravelNum);
                    xDataDistro.add("travel" + "\n₹" + String.valueOf(TravelNum));
                    TempColors.add(GetColorFromStatus("travel"));
                }
                if (FoodNum > 0.0f) {
                    yDataDistro.add(FoodNum);
                    xDataDistro.add("food" + "\n₹" + String.valueOf(FoodNum));
                    TempColors.add(GetColorFromStatus("food"));
                }
                if (ElectronicsNum > 0.0f) {
                    yDataDistro.add(ElectronicsNum);
                    xDataDistro.add("electronics" + "\n₹" + String.valueOf(ElectronicsNum));
                    TempColors.add(GetColorFromStatus("electronics"));
                }
                if (SoftwareNum > 0.0f) {
                    yDataDistro.add(SoftwareNum);
                    xDataDistro.add("software" + "\n₹" + String.valueOf(SoftwareNum));
                    TempColors.add(GetColorFromStatus("software"));
                }
                if (OtherNum > 0.0f) {
                    yDataDistro.add(OtherNum);
                    xDataDistro.add("other" + "\n₹" + String.valueOf(OtherNum));
                    TempColors.add(GetColorFromStatus("other"));
                }
                TotalExpenses = TravelNum + FoodNum + ElectronicsNum + SoftwareNum + OtherNum;
                TextView TotalText = (TextView) findViewById(R.id.totalAmount);
                TotalText.setText("Total Amount • ₹" + String.valueOf(TotalExpenses));
                AddDataPieDistro(TempColors);
            }
        });
    }

    private void GotoCompanyDash() {
        Intent CompanyDashIn = new Intent(StatisticsGraphActivity.this, CompanyDash.class);
        CompanyDashIn.putExtra("companyID", GlobalCompanyCUID);
        CompanyDashIn.putExtra("email", GlobalEmail);
        CompanyDashIn.putExtra("uid", GlobalUID);
        CompanyDashIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(CompanyDashIn);
    }

    private void GotoHistoryTab() {
        Intent GoToHistoryTabIntent = new Intent(StatisticsGraphActivity.this, HistoryView.class);
        GoToHistoryTabIntent.putExtra("companyID", GlobalCompanyCUID);
        GoToHistoryTabIntent.putExtra("uid", GlobalUID);
        GoToHistoryTabIntent.putExtra("email", GlobalEmail);
        GoToHistoryTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(GoToHistoryTabIntent);
    }

    private void GotoPeopleTab() {
        Intent GoToHistoryTabIntent = new Intent(StatisticsGraphActivity.this, manage_people.class);
        GoToHistoryTabIntent.putExtra("companyID", GlobalCompanyCUID);
        GoToHistoryTabIntent.putExtra("uid", GlobalUID);
        GoToHistoryTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(GoToHistoryTabIntent);
    }

    private Integer GetColorFromStatus(String InputCategory) {
        if (InputCategory == "travel") {
            return Color.parseColor("#FF4B7C");
        } else if (InputCategory == "food") {
            return Color.parseColor("#FFC66B");
        } else if (InputCategory == "electronics") {
            return Color.parseColor("#54E040");
        } else if (InputCategory == "software") {
            return Color.parseColor("#22CAFC");
        } else if (InputCategory == "other") {
            return Color.parseColor("#4279ED");
        } else {
            return null;
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
}
