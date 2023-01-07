package com.podosoft.zenela;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.podosoft.zenela.Adapters.PeopleAdapter;
import com.podosoft.zenela.Listeners.SearchResponseListener;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.SearchResponse;

public class SearchActivity extends AppCompatActivity {

    TextView tv_search_alert;
    Button random_people;
    ProgressBar progressBar;
    Toolbar toolbar;
    SearchView searchView;
    RequestManagerProfile managerProfile;

    RecyclerView peopleRecyclerView;
    PeopleAdapter peopleAdapter;

    SharedPreferences sharedPreferences = null;
    String email;
    Long principalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // init
        tv_search_alert = findViewById(R.id.tv_search_alert);
        searchView = findViewById(R.id.searchView);
        random_people = findViewById(R.id.random_people);
        progressBar = findViewById(R.id.progress);

        searchView.setIconified(false);

        sharedPreferences = this.getSharedPreferences("login", 0);
        email = sharedPreferences.getString("email", null);
        principalId = sharedPreferences.getLong("principalId", 0);

        // request
        managerProfile = new RequestManagerProfile(this);

        peopleRecyclerView = findViewById(R.id.recycler_search_people_result);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tv_search_alert.setVisibility(View.INVISIBLE);
                if (query.length() > 0)
                    showProgress();
                managerProfile.searchPeople(searchResponseListener, query, principalId);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tv_search_alert.setVisibility(View.INVISIBLE);
                if (newText.length() > 0)
                    showProgress();
                    managerProfile.searchPeople(searchResponseListener, newText, principalId);

                return true;
            }
        });

        random_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_search_alert.setVisibility(View.INVISIBLE);
                showProgress();
                managerProfile.searchRandomPeople(searchResponseListener, principalId);
            }
        });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    SearchResponseListener searchResponseListener = new SearchResponseListener() {
        @Override
        public void didFetch(SearchResponse response, String message) {
            hideProgress();
            peopleRecyclerView.setHasFixedSize(true);
            peopleRecyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 1));
            peopleAdapter = new PeopleAdapter(SearchActivity.this, response.getUsers(), principalId);
            peopleRecyclerView.setAdapter(peopleAdapter);
            if (response.getUsers().size() < 1){
                tv_search_alert.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void didError(String message) {
            hideProgress();
            Toast.makeText(SearchActivity.this, "❌", Toast.LENGTH_LONG).show();
        }
    };

    // hide ProgressBar
    private void hideProgress(){
        progressBar.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.height = 0;
        progressBar.setLayoutParams(layoutParams);

    }

    // show Progress
    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        progressBar.setLayoutParams(layoutParams);
    }
}