package com.crop.ezdevelopment.myhearthquake;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class EarthquakeListFragment extends Fragment {

    public interface OnListFragmentInteractionListener{
        void onListFragmentRefreshRequested();
    }
    private OnListFragmentInteractionListener mListener;

    private ArrayList<Earthquake> mEarthquakes = new ArrayList<>();

    private EarthquakeRecyclerViewAdapter recyclerViewAdapter = new EarthquakeRecyclerViewAdapter(mEarthquakes);
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    protected EarthquakeViewModel earthquakeViewModel;

    private int mMinimunMagnitude = 0;

    public EarthquakeListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recyclerViewAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateEarthquake();
            }
        });
    }

    protected void updateEarthquake(){
        if (mListener != null)
            mListener.onListFragmentRefreshRequested();
    }

    public void setmEarthquakes(List<Earthquake> earthquakes){
        updateFromPreferences();

        mEarthquakes.clear();
        recyclerViewAdapter.notifyDataSetChanged();

        for (Earthquake earthquake : earthquakes){
            if (earthquake.getMagnitude() >= mMinimunMagnitude){
                if (!mEarthquakes.contains(earthquake)){
                    mEarthquakes.add(earthquake);
                    recyclerViewAdapter.notifyItemInserted(mEarthquakes.indexOf(earthquake));
                }
            }

        }

        if (mEarthquakes != null && mEarthquakes.size()>0){
            for (int i = mEarthquakes.size() - 1; i>=0; i--){
                if (mEarthquakes.get(i).getMagnitude() < mMinimunMagnitude){
                    mEarthquakes.remove(i);
                    recyclerViewAdapter.notifyItemRemoved(i);
                }
            }
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        earthquakeViewModel = ViewModelProviders.of(getActivity()).get(EarthquakeViewModel.class);

        earthquakeViewModel.getEarthquakes().observe(this, new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(@Nullable List<Earthquake> earthquakes) {
                if (earthquakes != null){
                    setmEarthquakes(earthquakes);
                }
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(mPreferenceListener);
    }
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener  = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (PreferencesActivity.PREF_MIN_MAG.equals(s)){
                List<Earthquake> earthquakes = earthquakeViewModel.getEarthquakes().getValue();
                if (earthquakes != null){
                    setmEarthquakes(earthquakes);
                }
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateFromPreferences(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMinimunMagnitude = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG,"3"));
    }
}
