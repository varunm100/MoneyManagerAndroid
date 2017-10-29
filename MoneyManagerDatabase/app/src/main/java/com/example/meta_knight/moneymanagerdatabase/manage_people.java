package com.example.meta_knight.moneymanagerdatabase;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class manage_people extends AppCompatActivity {

    private String GlobalCompanyCUID = null;

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
            InitializeContentView();
            ShowAllPeople();
        } else {
            Toast.makeText(this, "GOT NULL BUNDLE!!! ERROR", Toast.LENGTH_SHORT).show();
        }
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
                        Toast.makeText(manage_people.this, TempMovie.getTitle(), Toast.LENGTH_SHORT).show();
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
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new MoviesAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
}
