package com.crop.ezdevelopment.myhearthquake;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EarthquakeMapFragment extends Fragment implements OnMapReadyCallback {

    private int mMinimumMagnitude = 0;
    Map<String, Marker> mMarkers =new HashMap<>();
    List<Earthquake> mEarthquakes;
    private GoogleMap mMap;
    EarthquakeViewModel earthquakeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earthquake_map,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        earthquakeViewModel = ViewModelProviders.of(getActivity()).get(EarthquakeViewModel.class);

        earthquakeViewModel.getEarthquakes().observe(this, new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(@Nullable List<Earthquake> earthquakes) {
                if (earthquakes != null){
                    setEarthquakeMarkers(earthquakes);
                }
            }
        });
    }

    private void updateFromPreferences(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMinimumMagnitude = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG,"3"));

    }
    private void setEarthquakeMarkers(List<Earthquake> earthquakes){
        updateFromPreferences();

        mEarthquakes = earthquakes;
        if(mMap == null || earthquakes == null)return;

        Map<String, Earthquake> newEarthquakes = new HashMap<>();
        for (Earthquake earthquake : earthquakes) {
            if (earthquake.getMagnitude() >= mMinimumMagnitude) {
                newEarthquakes.put(earthquake.getId(), earthquake);
                if (!mMarkers.containsKey(earthquake.getId())) {
                    Location location = earthquake.getLocation();
                    Marker marker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(location.getLatitude(),
                                            location.getLongitude()))
                                    .title("M:" + earthquake.getMagnitude()));
                    mMarkers.put(earthquake.getId(), marker);
                }
            }
        }

        for (Iterator<String> iterator = mMarkers.keySet().iterator();
             iterator.hasNext();) {
            String earthquakeID = iterator.next();
            if (!newEarthquakes.containsKey(earthquakeID)) {
                mMarkers.get(earthquakeID).remove();
                iterator.remove();
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Register an OnSharedPreferenceChangeListener
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(mPListener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener mPListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            if (PreferencesActivity.PREF_MIN_MAG.equals(key)) {
                // Repopulate the Markers.
                List<Earthquake> earthquakes = earthquakeViewModel.getEarthquakes().getValue();
                if (earthquakes != null)
                    setEarthquakeMarkers(earthquakes);
            }
        }
    };
}
