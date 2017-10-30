package com.example.meta_knight.moneymanagerdatabase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HistoryView extends AppCompatActivity {
    private String GlobalCompCUID = null;
    private String uid = null;
    private String email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);


        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            GlobalCompCUID = bundleExtras.getString("companyID");
            uid = bundleExtras.getString("uid");
            email = bundleExtras.getString("email");
        } else {
            Toast.makeText(this, "Found Null Bundle", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu MainMenu = bottomNavigationView.getMenu();
        MenuItem currentItem = MainMenu.getItem(3);
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
                        break;
                }
                return false;
            }
        });
    }

    private void GotoPeopleTab() {
        Intent GotoCompanyPeopleIntent = new Intent(HistoryView.this, manage_people.class);
        GotoCompanyPeopleIntent.putExtra("companyID", GlobalCompCUID);
        GotoCompanyPeopleIntent.putExtra("uid", uid);
        GotoCompanyPeopleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(GotoCompanyPeopleIntent);
    }

    private void GotoCompanyDash() {
        Intent GotoCompanyDash = new Intent(HistoryView.this, CompanyDash.class);
        GotoCompanyDash.putExtra("companyID", GlobalCompCUID);
        GotoCompanyDash.putExtra("email", email);
        GotoCompanyDash.putExtra("uid", uid);
        GotoCompanyDash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(GotoCompanyDash);
    }
}
