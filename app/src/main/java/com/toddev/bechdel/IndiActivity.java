package com.toddev.bechdel;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * Created by David Margolin on 5/13/2016.
 * A View Controller for displaying movie details
 * OG Retriever is used to retrieve details from bechdeltest.com
 */
public class IndiActivity extends AppCompatActivity {
    String id;
    String imdb;
    CheckBox bechview1, bechview2, bechview3;
    TextView titleview;
    TextView ratingview;
    String ajsonurl;
    TextView plotview;
    TextView awardsview;
    ImageView posterview;
    String poster, rating, year, plot, awards, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.indi_layout);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        titleview = (TextView) findViewById(R.id.titleview);
        ratingview = (TextView) findViewById(R.id.ratingview);
        plotview = (TextView) findViewById(R.id.plotview);
        awardsview = (TextView) findViewById(R.id.awardsview);
        bechview1 = (CheckBox) findViewById(R.id.bechview1);
        bechview2 = (CheckBox) findViewById(R.id.bechview2);
        bechview3 = (CheckBox) findViewById(R.id.bechview3);
        posterview = (ImageView) findViewById(R.id.posterview);
        title = intent.getStringExtra("title");
        poster = intent.getStringExtra("poster");
        rating = intent.getStringExtra("rating");
        year = intent.getStringExtra("year");
        plot = intent.getStringExtra("plot");
        awards = intent.getStringExtra("awards");
        imdb = intent.getStringExtra("id");
        ajsonurl = "http://bechdeltest.com/api/v1/getMovieByImdbId?imdbid=" + imdb;
        titleview.setText(title + " (" + year + ")");
        ratingview.setText("Rating: " + rating + "/10");
        plotview.setText(plot);
        awardsview.setText(awards);

        //  yearview.setText();
        Picasso.with(this).load(poster.replace("300", "500")).placeholder(R.drawable.placeholder).fit().into(posterview);
        Log.i("id", id);
        new OGRetriever().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name) + " Test");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }

    private class OGRetriever extends AsyncTask<String, Void, String> {
        String bechdelrating;

        @Override
        protected String doInBackground(String... params) {

            try {

                InputStream ainput = new URL(ajsonurl).openStream();
                Reader areader = new InputStreamReader(ainput, "UTF-8");
                ResultItems arrayset = new Gson().fromJson(areader, ResultItems.class);
                bechdelrating = arrayset.getRating();
            } catch (Exception exception) {
                Log.i("indi failed", exception.toString());
            }
            return bechdelrating;
        }

        @Override
        protected void onPostExecute(String result) {
            if (Integer.parseInt(result) > 0) {
                bechview1.setChecked(true);
                if (Integer.parseInt(result) > 1) {
                    bechview2.setChecked(true);
                    if (Integer.parseInt(result) > 2) {
                        bechview3.setChecked(true);
                    }
                }

            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
