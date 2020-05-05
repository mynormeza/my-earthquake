package com.crop.ezdevelopment.myhearthquake;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class EarthquakeViewModel extends AndroidViewModel {
    private static final String TAG = "EarthquakeUpdate";
    private LiveData<List<Earthquake>> earthquakes;

    public EarthquakeViewModel(Application application){
        super(application);
    }

    public LiveData<List<Earthquake>> getEarthquakes(){
        if (earthquakes == null) {
            earthquakes = EarthquakeDatabaseAccessor
                    .getInstance(getApplication())
                    .earthquakeDAO()
                    .loadAllEarthquakes();
            loadEarthquakes();

        }
        return earthquakes;
    }

    public void loadEarthquakes(){
        EarthquakeUpdateJobService.scheduleUpdateJob(getApplication());
    }
}
