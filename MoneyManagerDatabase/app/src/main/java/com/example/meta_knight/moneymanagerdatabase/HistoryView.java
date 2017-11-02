package com.example.meta_knight.moneymanagerdatabase;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.firestore.*;
import es.dmoral.toasty.Toasty;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryView extends AppCompatActivity {
    private String GlobalCompCUID = null;
    private String uid = null;
    private String email = null;

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);


        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            GlobalCompCUID = bundleExtras.getString("companyID");
            uid = bundleExtras.getString("uid");
            email = bundleExtras.getString("email");

            InitializeContentView();
            ShowAllHistory();
        } else {
            Toasty.error(HistoryView.this, "FOUND NULL BUNDLE!").show();
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
                        GotoStatsGraph();
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

    private void ShowAllHistory() {
        if (GlobalCompCUID == null) {
            return;
        }
        CollectionReference mCollectionRef = FirebaseFirestore.getInstance().collection("companies/" + GlobalCompCUID + "/history/");
        mCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    if (i.getString("date") != null && i.getString("ownerEmail") != null && i.getString("type") != null && i.getString("title") != null) {
                        Date ExpenseDate = null;
                        DateFormat MainDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            ExpenseDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(i.getString("date"));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        TempMovie = new Movie(i.getString("title"), i.getString("ownerEmail"), MainDateFormat.format(ExpenseDate) + "\n" + i.getString("type").toUpperCase());
                        movieList.add(TempMovie);
                    } else {
                        Toasty.error(HistoryView.this, "GOT SOME NULL VALUES").show();
                    }
                }
                if (!movieList.isEmpty()) {
                    addHistoryCards();
                }
            }
        });
    }

    private void addHistoryCards() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter.notifyDataSetChanged();
    }

    private void InitializeContentView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new MoviesAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.canScrollVertically();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Movie SelectedMovie = movieList.get(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                final Movie SelectedMovie = movieList.get(position);
            }
        }));
        recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);
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

    private void GotoStatsGraph() {
        Intent GotoStatsGraphIntent = new Intent(HistoryView.this, StatisticsGraphActivity.class);
        GotoStatsGraphIntent.putExtra("companyID", GlobalCompCUID);
        GotoStatsGraphIntent.putExtra("email", email);
        GotoStatsGraphIntent.putExtra("uid", uid);
        GotoStatsGraphIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(GotoStatsGraphIntent);
    }
}
