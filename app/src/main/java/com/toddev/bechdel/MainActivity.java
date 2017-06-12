package com.toddev.bechdel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import jp.co.recruit_mp.android.widget.HeaderFooterGridView;
/**
 * Created by David Margolin on 5/8/2016.
 * A View Controller for the main activity
 * Downloads an the entire movie database on first run
 * and uses it to speed up further opens
 */
public class MainActivity extends AppCompatActivity {
    static String SearchLink = "http://bechdeltest.com/api/v1/getMoviesByTitle?title=";
    HeaderFooterGridView lv;
    FloatingSearchView search;
    String query;
    TextView header;
    ProgressBar progressBar;
    SharedPreferences prefs;
    ArrayList<OmdbResultItems> plistrand = new ArrayList<OmdbResultItems>();
    ArrayList<OmdbResultItems> plistsearch = new ArrayList<OmdbResultItems>();
    Searcher searcher = new Searcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_main);
        lv = (HeaderFooterGridView) findViewById(R.id.listView);
        search = (FloatingSearchView) findViewById(R.id.floating_search_view);
        header = (TextView) getLayoutInflater().inflate(R.layout.headerview, null);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        lv.addHeaderView(header);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String databasestring = prefs.getString("fulldatabase", null);
        if (databasestring == null) {
            new CreateDatabase().execute();
        } else {
            new RandomLister().execute();
        }
        //new RandomLister().execute();
        search.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    lv.setAdapter(new MovieAdapter(MainActivity.this, plistrand));
                    header.setText("Random Movies");
                } else {

                    query = newQuery;
                }
            }
        });
        search.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                //will never happen

            }

            @Override
            public void onSearchAction() {
                header.setText("");
                search.setSearchText(query);
                searcher.cancel(true);
                searcher = new Searcher();
                plistsearch = new ArrayList<OmdbResultItems>();
                lv.setAdapter(new MovieAdapter(MainActivity.this, plistsearch));
                searcher.execute();
            }
        });
        // Set an OnMenuItemClickListener to handle menu item clicks
        search.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Intent settingsintent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settingsintent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (search.isSearchBarFocused()) {
            search.clearFocus();
            search.closeMenu(true);
        } else {
            finish();
        }
    }

    private class RandomLister extends AsyncTask<String, Void, String> {
        int i;

        @Override
        protected String doInBackground(String... params) {

            try {
                Gson gson = new Gson();
                String json = prefs.getString("fulldatabase", null);
                Type type = new TypeToken<ArrayList<ResultItems>>() {
                }.getType();
                ArrayList<ResultItems> aarray = gson.fromJson(json, type);
                Log.i("success", "inforecievedfromdatabase");
                for (int i = 0; i < 8; i++) {
                    Random rand = new Random();
                    int randomNumber = rand.nextInt(aarray.size());
                    String jsonurl = "https://api.themoviedb.org/3/movie/tt" + aarray.get(randomNumber).getImdbid() + "?api_key=184d66260cf77a7bbf0df25cd475d698";
                    Log.i("test", aarray.get(randomNumber).getImdbid());
                    InputStream input = new URL(jsonurl).openStream();
                    Reader reader = new InputStreamReader(input, "UTF-8");
                    OmdbResultItems p = new Gson().fromJson(reader, OmdbResultItems.class);
                    p.setimdb_id(aarray.get(randomNumber).getImdbid());
                    plistrand.add(i, p);
                    aarray.remove(randomNumber);
                }

            } catch (Exception exception) {
                Log.i("this failed" + i, exception.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            lv.setAdapter(new MovieAdapter(MainActivity.this, plistrand));


        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class CreateDatabase extends AsyncTask<String, Void, String> {
        String arraystring;

        @Override
        protected String doInBackground(String... params) {
            try {
                String ajsonurl = "http://bechdeltest.com/api/v1/getAllMovieIds";
                InputStream ainput = new URL(ajsonurl).openStream();
                Reader areader = new InputStreamReader(ainput, "UTF-8");
                ArrayList<ResultItems> arrayset = new Gson().fromJson(areader, new TypeToken<ArrayList<ResultItems>>() {
                }.getType());
                arraystring = new Gson().toJson(arrayset);
                Log.i("success", "arrayset created");


            } catch (Exception exception) {
                Log.i("createdatabase failed", exception.toString());
            }
            return arraystring;
        }

        @Override
        protected void onPostExecute(String result) {
            prefs.edit().putString("fulldatabase", arraystring).commit();
            new RandomLister().execute();
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class Searcher extends AsyncTask<String, Void, String> {
        int i;

        @Override
        protected String doInBackground(String... params) {

            try {
                InputStream input = new URL(SearchLink + query.replace(" ", "_")).openStream();
                Reader reader = new InputStreamReader(input, "UTF-8");
                List<ResultItems> resultarray = new Gson().fromJson(reader, new TypeToken<List<ResultItems>>() {
                }.getType());

                //omdb code
                for (int i = 0; i < resultarray.size(); i++) {
                    String jsonurl = "https://api.themoviedb.org/3/movie/tt" + resultarray.get(i).getImdbid() + "?api_key=184d66260cf77a7bbf0df25cd475d698";
                    input = new URL(jsonurl).openStream();
                    reader = new InputStreamReader(input, "UTF-8");
                    OmdbResultItems p = new Gson().fromJson(reader, OmdbResultItems.class);
                    p.setimdb_id(resultarray.get(i).getImdbid());
                    plistsearch.add(i, p);
                }

            } catch (Exception exception) {
                Log.i("this failed" + i, exception.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (plistsearch.isEmpty()) {
                OmdbResultItems p = new OmdbResultItems();
                p.setposter_path("404error");
                p.settitle("No Results Found");
                plistsearch.add(p);
            }
            progressBar.setVisibility(View.INVISIBLE);
            lv.setAdapter(new MovieAdapter(MainActivity.this, plistsearch));
            header.setText("\"" + query + "\"");
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
