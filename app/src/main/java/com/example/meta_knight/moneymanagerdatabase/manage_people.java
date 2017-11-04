package com.example.meta_knight.moneymanagerdatabase;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import es.dmoral.toasty.Toasty;

import java.util.ArrayList;
import java.util.List;

public class manage_people extends AppCompatActivity {

    private String GlobalCompanyCUID = null;
    private String email = null;
    private String uid = null;

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_people);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            GlobalCompanyCUID = bundleExtras.getString("companyID");
            uid = bundleExtras.getString("uid");
            email = bundleExtras.getString("email");
            InitializeContentView();
            ShowAllPeople();
        } else {
            Toasty.error(this, "GOT NULL BUNDLE!").show();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu MainMenu = bottomNavigationView.getMenu();
        MenuItem currentItem = MainMenu.getItem(2);
        currentItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ExpenseTab:
                        GotoCompanyDash();
                        break;
                    case R.id.GraphTab:
                        GotoStatsGraph();
                        break;
                    case R.id.PeopleTab:
                        break;
                    case R.id.HistoryTab:
                        StartHistoryViewActivity();
                        break;
                }
                return false;
            }
        });
    }

    private void ShowAllPeople() {
        if (GlobalCompanyCUID == null) {
            return;
        }
        CollectionReference CollRef = FirebaseFirestore.getInstance().collection("companies/" + GlobalCompanyCUID + "/people/");
        CollRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots.isEmpty()) {
                    return;
                }
                List<DocumentSnapshot> DocSnapVec = documentSnapshots.getDocuments();
                Movie TempMovie = null;
                List<Movie> TempCompanyList = new ArrayList<>();
                movieList.clear();
                for (DocumentSnapshot i : DocSnapVec) {
                    if (i.getString("pname") != null && i.getString("role") != null && i.getString("pemail") != null) {
                        TempMovie = new Movie(i.getString("pname"), i.getString("pemail"), i.getString("role").toUpperCase());
                        movieList.add(TempMovie);
                    }
                }
                if (!movieList.isEmpty()) {
                    addPeopleCards();
                }
            }
        });
    }

    private void addPeopleCards() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter.notifyDataSetChanged();
    }

    private void InitializeContentView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        mAdapter = new MoviesAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Movie SelectMovie = movieList.get(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                final Movie SelectMovie = movieList.get(position);
            }
        }));
        recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);
    }

    private void StartHistoryViewActivity() {
        Intent StartHistoryActivity = new Intent(manage_people.this, HistoryView.class);
        StartHistoryActivity.putExtra("companyID", GlobalCompanyCUID);
        StartHistoryActivity.putExtra("uid", uid);
        StartHistoryActivity.putExtra("email", email);
        StartHistoryActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(StartHistoryActivity);
    }

    private void GotoCompanyDash() {
        Intent CompanyDashIntent = new Intent(manage_people.this, CompanyDash.class);
        CompanyDashIntent.putExtra("companyID", GlobalCompanyCUID);
        CompanyDashIntent.putExtra("email", email);
        CompanyDashIntent.putExtra("uid", uid);
        CompanyDashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(CompanyDashIntent);
    }

    private void GotoStatsGraph() {
        Intent GotoStatGraphIntent = new Intent(manage_people.this, StatisticsGraphActivity.class);
        GotoStatGraphIntent.putExtra("companyID", GlobalCompanyCUID);
        GotoStatGraphIntent.putExtra("email", email);
        GotoStatGraphIntent.putExtra("uid", uid);
        GotoStatGraphIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(GotoStatGraphIntent);
    }
}
