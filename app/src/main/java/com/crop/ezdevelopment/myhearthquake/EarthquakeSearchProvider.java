package com.crop.ezdevelopment.myhearthquake;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class EarthquakeSearchProvider extends ContentProvider {

    private static final int SEARCH_SUGGESTIONS = 1;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.crop.ezdevelopment.provider.myhearthquake",
                SearchManager.SUGGEST_URI_PATH_QUERY,SEARCH_SUGGESTIONS);
        uriMatcher.addURI("com.crop.ezdevelopment.provider.myhearthquake",
                SearchManager.SUGGEST_URI_PATH_QUERY + "/*",SEARCH_SUGGESTIONS);
        uriMatcher.addURI("com.crop.ezdevelopment.provider.myhearthquake",
                SearchManager.SUGGEST_URI_PATH_SHORTCUT,SEARCH_SUGGESTIONS);
        uriMatcher.addURI("com.crop.ezdevelopment.provider.myhearthquake",
                SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*",SEARCH_SUGGESTIONS);

    }

    @Override
    public boolean onCreate() {
        EarthquakeDatabaseAccessor.getInstance(getContext().getApplicationContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        if (uriMatcher.match(uri) == SEARCH_SUGGESTIONS){
            String searchQuery = "%" + uri.getLastPathSegment() +"%";
            EarthquakeDAO earthquakeDAO = EarthquakeDatabaseAccessor
                    .getInstance(getContext().getApplicationContext())
                    .earthquakeDAO();
            Cursor c = earthquakeDAO.generateSearchSuggestions(searchQuery);
            return c;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case SEARCH_SUGGESTIONS:
                return SearchManager.SUGGEST_MIME_TYPE;
                default:
                    throw new IllegalArgumentException("Unsupported URI: "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
