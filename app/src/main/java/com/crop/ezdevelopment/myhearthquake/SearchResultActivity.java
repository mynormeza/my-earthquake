package com.crop.ezdevelopment.myhearthquake;

import android.app.SearchManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private ArrayList<Earthquake> mEarthquakes =  new ArrayList<>();
    private EarthquakeRecyclerViewAdapter mEarthquakeAdapter =  new EarthquakeRecyclerViewAdapter(mEarthquakes);
    private MutableLiveData<String> searchQuery;
    private LiveData<List<Earthquake>> searchResult;
    private MutableLiveData<String> selectedSearchSuggestionId;
    private LiveData<Earthquake> selectedSearchSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.search_result_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mEarthquakeAdapter);

        searchQuery = new MutableLiveData<>();
        searchQuery.setValue(null);

        // Link the search query Live Data to the search results Live Data.
        // Configure Switch Map such that a change in the search query
        // updates the search results by querying the database.
        searchResult = Transformations.switchMap(searchQuery,
                query -> EarthquakeDatabaseAccessor
                        .getInstance(getApplicationContext())
                        .earthquakeDAO()
                        .searchEarthquakes(query));

        searchResult.observe(SearchResultActivity.this,searchQueryResultsObserver);

        selectedSearchSuggestionId = new MutableLiveData<>();
        selectedSearchSuggestionId.setValue(null);

        // Link the selected search suggestion ID Live Data to the
        // selected search suggestion Live Data.
        // Configure Switch Map such that a change in the ID of the
        // selected search suggestion, updates the Live Data that
        // returns the corresponding Earthquake by querying the database.
        selectedSearchSuggestion = Transformations.switchMap(selectedSearchSuggestionId,
                id -> EarthquakeDatabaseAccessor
                        .getInstance(getApplicationContext())
                        .earthquakeDAO()
                        .getEarthquake(id));

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())){
            selectedSearchSuggestion.observe(this,selectedSearchSuggestionObserver);
            setSelectedSearchSuggestionId(getIntent().getData());
        }else {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            setSearchQuery(query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())){
            setSelectedSearchSuggestionId(getIntent().getData());
        }else {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            setSearchQuery(query);
        }
    }

    private void setSearchQuery(String query){
        searchQuery.setValue(query);
    }

    private final Observer<List<Earthquake>> searchQueryResultsObserver = updatedEarthquakes ->{
        mEarthquakes.clear();
        if (updatedEarthquakes != null){
            mEarthquakes.addAll(updatedEarthquakes);
        }
        mEarthquakeAdapter.notifyDataSetChanged();
    };

    private void setSelectedSearchSuggestionId(Uri dataString){
        String id = dataString.getPathSegments().get(1);
        selectedSearchSuggestionId.setValue(id);
    }

    final Observer<Earthquake> selectedSearchSuggestionObserver
            = selectedSearchSuggestion -> {
        if (selectedSearchSuggestion != null){
            setSearchQuery(selectedSearchSuggestion.getDetails());
        }
    };
}
