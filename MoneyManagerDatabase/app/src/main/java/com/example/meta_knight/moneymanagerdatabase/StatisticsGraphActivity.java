package com.example.meta_knight.moneymanagerdatabase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class StatisticsGraphActivity extends AppCompatActivity {
    private String GlobalCompanyCUID = null;
    private String GlobalUID = null;
    private String GlobalEmail = null;

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
            Toast.makeText(this, "Found Null Bundle", Toast.LENGTH_SHORT).show();
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

}
