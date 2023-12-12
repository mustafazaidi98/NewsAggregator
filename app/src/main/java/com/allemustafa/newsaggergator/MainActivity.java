package com.allemustafa.newsaggergator;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allemustafa.newsaggergator.databinding.ActivityMainBinding;
import com.android.volley.VolleyError;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    NewsDataGetter dataGetter;
    private Menu opt_menu;
    private Map<String, List<NewsSource>> newsSources;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private final ArrayList<String> newsChannels = new ArrayList<>();
    private List<NewsArticles> newsArticles = new ArrayList<>();
    ActivityMainBinding binding;
    private ArrayAdapter<String> arrayAdapter;
    private List<NewsSource> lst;
    private ViewPager2 viewPager;
    private ArticlesAdapter articlesAdapter;
    private String selectedChannel;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(getString(R.string.newsGateway));
        mDrawerLayout = binding.drawerLayout;
        mDrawerList = binding.drawerList;
        articlesAdapter = new ArticlesAdapter(this, (ArrayList<NewsArticles>) newsArticles);
        viewPager = binding.viewpager;
        viewPager.setAdapter(articlesAdapter);
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,            /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        dataGetter = new NewsDataGetter(this);
        dataGetter.run();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    private void selectItem(int position) {
        viewPager.setBackground(null);
        String channelName = newsChannels.get(position);
        NewsSource ns =  lst.stream().filter(chnl -> chnl.getTitle().equalsIgnoreCase(channelName)).findFirst().orElse(null);
        String source = ns.getId();
        selectedChannel = channelName;
        dataGetter.run(source);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        int n = populateChannels(item.getTitle().toString());
        setTitle(item.getTitle()+" ("+n+")");
        return super.onOptionsItemSelected(item);
    }

    private int populateChannels(@NonNull String item) {
        selectedCategory = item;
        newsChannels.clear();
        lst = (List<NewsSource>) newsSources.get(item);
        HashSet<String> list = new HashSet<String>();
        lst.forEach(ns-> list.add(ns.getTitle()));
        if (lst != null) {
            newsChannels.addAll(list);
        }
        SetDrawerColors();
        arrayAdapter.notifyDataSetChanged();
        return lst.size();
    }

    public boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = (networkInfo != null && networkInfo.isConnectedOrConnecting());
        if(connected==false) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return connected;
    }
    public void UpdateArticles(List<NewsArticles> articles){
        this.newsArticles.clear();
        List<NewsArticles> firstTenElementsList = articles.stream().limit(10).collect(Collectors.toList());
        this.newsArticles.addAll(firstTenElementsList);
        articlesAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
        mDrawerLayout.closeDrawer(mDrawerList);
        setTitle( selectedChannel+ " (" + newsArticles.size() + ")");
    }
    public void SetDrawerColors(){
        ListView listView = binding.drawerList;
        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            Object item = arrayAdapter.getItem(i);
            View view = arrayAdapter.getView(i, null, listView);
            view.setBackgroundColor(Color.RED);
//            TextView textView = view.findViewById(R.id.text_view123);
//            textView.setTextColor(Color.RED);
        }
    }
    public void UpdateData(Map<String, List<NewsSource>> newsSources) {
        this.newsSources = newsSources;
        ArrayList<String> tempList = new ArrayList<>(newsSources.keySet());
        Collections.sort(tempList);
        for (String s : tempList)
            opt_menu.add(s);
        SetMenuItemColors();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, newsChannels);
        mDrawerList.setAdapter(arrayAdapter);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        int n = populateChannels("All");
        setTitle("News Gateway ("+n+")");
    }
    public void SetMenuItemColors(){
        for (int i = 0; i < opt_menu.size(); i++) {
            MenuItem menuItem = opt_menu.getItem(i);
            SpannableString spannableString = new SpannableString(menuItem.getTitle());
            if(menuItem.getTitle().toString().equalsIgnoreCase("All"))
                continue;
            for (String s : newsSources.keySet()) {
                if (menuItem.getTitle().toString().equals(s)) {
                    NewsSource ns = (NewsSource) newsSources.get(s).get(0);
                    spannableString.setSpan(new ForegroundColorSpan(ns.getTextColor()), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
            menuItem.setTitle(spannableString);
        }
    }
    public void InvalidLocationSelected(VolleyError error) {
        return;
    }
}