package com.crop.ezdevelopment.myhearthquake;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

public class EarthquakeRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new EarthquakeRemoteViewsFactory(this);
    }

    class EarthquakeRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private List<Earthquake> mEarthquakes;

        public EarthquakeRemoteViewsFactory(Context context) {
            mContext = context;
        }

        public void onCreate() {
        }

        public void onDataSetChanged() {
            mEarthquakes = EarthquakeDatabaseAccessor.getInstance(mContext)
                    .earthquakeDAO().loadAllEarthquakeBlocking();
        }

        public int getCount() {
            if (mEarthquakes == null) return 0;
            return mEarthquakes.size();
        }
        public long getItemId(int index) {
            if (mEarthquakes == null) return index;
            return mEarthquakes.get(index).getDate().getTime();
        }

        public RemoteViews getViewAt(int index) {
            if (mEarthquakes != null) {
                // Extract the requested Earthquake.
                Earthquake earthquake = mEarthquakes.get(index);

                // Extract the values to be displayed.
                String id = earthquake.getId();
                String magnitude = String.valueOf(earthquake.getMagnitude());
                String details = earthquake.getDetails();

                // Create a new Remote Views object and use it to populate the
                // layout used to represent each earthquake in the list.
                RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                        R.layout.quake_widget);
                rv.setTextViewText(R.id.widget_magnitude, magnitude);
                rv.setTextViewText(R.id.widget_details, details);

                // Create a Pending Intent that will open the main Activity.
                Intent intent = new Intent(mContext, MainActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
                rv.setOnClickPendingIntent(R.id.widget_magnitude, pendingIntent);
                rv.setOnClickPendingIntent(R.id.widget_details, pendingIntent);

                return rv;
            } else {
                return null;
            }
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return true;
        }

        public RemoteViews getLoadingView() {
            return null;
        }

        public void onDestroy() {
        }
    }
}
