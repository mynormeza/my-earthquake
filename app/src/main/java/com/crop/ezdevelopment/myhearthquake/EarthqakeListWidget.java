package com.crop.ezdevelopment.myhearthquake;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class EarthqakeListWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        PendingResult pendingResult = goAsync();
        updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
    }

    @Override
    public void onEnabled(Context context) {
        final PendingResult pendingResult = goAsync();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName earthquakeListWidget = new ComponentName(context, EarthqakeListWidget.class);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(earthquakeListWidget);

        updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
    }

    static void updateAppWidgets(final Context context,
                                 final AppWidgetManager appWidgetManager,
                                 final int[] appWidgetIds,
                                 final PendingResult pendingResult) {
        Thread thread = new Thread() {
            public void run() {
                for (int appWidgetId: appWidgetIds) {
                    // Set up the intent that starts the Earthquake
                    // Remote Views Service, which will supply the views
                    // shown in the List View.
                    Intent intent = new Intent(context, EarthquakeRemoteViewService.class);

                    // Add the app widget ID to the intent extras.
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

                    // Instantiate the RemoteViews object for the App Widget layout.
                    RemoteViews views = new RemoteViews(context.getPackageName(),
                            R.layout.quake_collection_widget);

                    // Set up the RemoteViews object to use a RemoteViews adapter.
                    views.setRemoteAdapter(R.id.widget_list_view, intent);

                    // The empty view is displayed when the collection has no items.
                    views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_text);

                    // Notify the App Widget Manager to update the widget using
                    // the modified remote view.
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
                if (pendingResult != null)
                    pendingResult.finish();
            }
        };
        thread.start();
    }
}
