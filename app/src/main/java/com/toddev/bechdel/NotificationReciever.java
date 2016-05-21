package com.toddev.bechdel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by David Margolin on 5/16/2016.
 */
public class NotificationReciever extends BroadcastReceiver {
    Context notifcontext;
    OmdbResultItems p;
    SharedPreferences prefs;
    int selected;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub


        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        notifcontext = context;
        new RandomMovie().execute();

    }


    private class RandomMovie extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                Gson gson = new Gson();
                String json = prefs.getString("fulldatabase", null);
                Type type = new TypeToken<ArrayList<ResultItems>>() {
                }.getType();
                ArrayList<ResultItems> aarray = gson.fromJson(json, type);
                Log.i("success", "inforecievedfromdatabase");
                Random rand = new Random();
                int randomNumber = rand.nextInt(aarray.size());
                String jsonurl = "http://www.omdbapi.com/?i=tt" + aarray.get(randomNumber).getImdbid() + "&plot=full&r=json";
                InputStream input = new URL(jsonurl).openStream();
                Reader reader = new InputStreamReader(input, "UTF-8");
                p = new Gson().fromJson(reader, OmdbResultItems.class);
                p.setImdbID(aarray.get(randomNumber).getImdbid());
            } catch (Exception exception) {
                Log.i("notif retrieval failed", exception.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            NotificationManager notificationManager = (NotificationManager) notifcontext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Intent detailactivity = new Intent(notifcontext, IndiActivity.class);
            detailactivity.putExtra("id", p.getImdbID());
            detailactivity.putExtra("title", p.getTitle());
            detailactivity.putExtra("poster", p.getPoster());
            detailactivity.putExtra("rating", p.getImdbID());
            detailactivity.putExtra("plot", p.getPlot());
            detailactivity.putExtra("year", p.getYear());
            detailactivity.putExtra("awards", p.getAwards());
            detailactivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(notifcontext, 0,
                    detailactivity, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                    notifcontext)
                    .setSmallIcon(R.drawable.ic_movie)
                    .setContentTitle("Daily Movie Suggestion:")
                    .setContentText(p.getTitle())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            notificationManager.notify(0, mNotifyBuilder.build());

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
