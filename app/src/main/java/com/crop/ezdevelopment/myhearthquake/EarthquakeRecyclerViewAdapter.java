package com.crop.ezdevelopment.myhearthquake;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.crop.ezdevelopment.myhearthquake.databinding.ListItemEarthquakeBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EarthquakeRecyclerViewAdapter extends RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder> {

    private final List<Earthquake> mEarthquakes;
    private static final SimpleDateFormat TIME_FORMAT =  new SimpleDateFormat("HH:mm",Locale.US);
    private static final NumberFormat MAGNITUDE_FORMAT = new DecimalFormat("0.0");

    public EarthquakeRecyclerViewAdapter( List<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListItemEarthquakeBinding binding = ListItemEarthquakeBinding.
                inflate(
                        LayoutInflater.from(viewGroup.getContext()),viewGroup,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Earthquake earthquake  = mEarthquakes.get(i);
        viewHolder.binding.setEarthquake(earthquake);
        viewHolder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mEarthquakes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final ListItemEarthquakeBinding binding;

        public Earthquake earthquake;

        public ViewHolder(ListItemEarthquakeBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setTimeformat(TIME_FORMAT);
            binding.setMagnitudeformat(MAGNITUDE_FORMAT);
        }

    }
}
