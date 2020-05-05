package com.crop.ezdevelopment.myhearthquake;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class PreferencesActivity extends AppCompatActivity {
    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    public static final String USER_PREFRENCE = "USER_PREFERENCE";
    public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
    public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class PrefFragment extends PreferenceFragmentCompat{

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            setPreferencesFromResource(R.xml.userpreferences,null);
        }
    }
}
