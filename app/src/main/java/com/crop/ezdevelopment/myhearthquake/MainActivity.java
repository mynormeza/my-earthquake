package com.crop.ezdevelopment.myhearthquake;

import android.app.SearchManager;
import android.app.SearchableInfo;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements EarthquakeListFragment.OnListFragmentInteractionListener {
    private final static String TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT";
    private static final int MENU_PREFERENCES = Menu.FIRST+1;
    private static final int SHOW_PREFERENCES = 1;

    EarthquakeListFragment mEarthquakeListFragment;
    EarthquakeViewModel earthquakeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.view_pager);
        if (viewPager != null){
            PagerAdapter pagerAdapter = new EarthquakeTabsPagerAdapter(getSupportFragmentManager());

            viewPager.setAdapter(pagerAdapter);
            TabLayout tabLayout = findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(viewPager);
        }
//        FragmentManager fm = getSupportFragmentManager();
//
//        if (savedInstanceState == null){
//            FragmentTransaction ft = fm.beginTransaction();
//            mEarthquakeListFragment = new EarthquakeListFragment();
//            ft.add(R.id.main_activity_frame,mEarthquakeListFragment,TAG_LIST_FRAGMENT);
//
//            ft.commitNow();
//        }else {
//            mEarthquakeListFragment = (EarthquakeListFragment) fm.findFragmentByTag(TAG_LIST_FRAGMENT);
//        }
        earthquakeViewModel = new ViewModelProvider(this).get(EarthquakeViewModel.class);
        /*Date now = Calendar.getInstance().getTime();
        List<Earthquake> dummyQuakes = new ArrayList<Earthquake>(0);
        dummyQuakes.add(new Earthquake("0", now, "San Jose", null, 7.3, null));
        dummyQuakes.add(new Earthquake("1", now, "LA", null, 6.5, null));
        mEarthquakeListFragment.setmEarthquakes(dummyQuakes);*/

    }

    @Override
    public void onListFragmentRefreshRequested() {
        updateEarthquakes();
    }

    private void updateEarthquakes() {
        earthquakeViewModel.loadEarthquakes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);

        SearchManager searchManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchableInfo searchableInfo = searchManager.getSearchableInfo(
                new ComponentName(getApplicationContext(),SearchResultActivity.class)
        );

        SearchView searchView = (SearchView) menu.findItem(R.id.seach_view).getActionView();
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.settings_menu_item :
                Intent intent = new Intent(this,PreferencesActivity.class);
                startActivityForResult(intent,SHOW_PREFERENCES);
                return true;
        }
        return false;
    }

    class EarthquakeTabsPagerAdapter extends FragmentPagerAdapter{


        public EarthquakeTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return new EarthquakeListFragment();
                case 1:
                    return new EarthquakeMapFragment();
                    default:
                        return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.tab_list);
                case 1:
                    return getString(R.string.tab_map);
                    default:
                        return null;
            }
        }
    }
}
