package com.toddev.bechdel;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;

import java.util.Calendar;
/**
 * Created by David Margolin on 5/12/2016.
 * Settings
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // do something useful
                onBackPressed();
                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        SharedPreferences mPrefs;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefitems);
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Preference omdbapipref = (Preference) findPreference("omdbapi");
            Preference bechdeltestpref = (Preference) findPreference("bechdelapi");
            Preference bechdelwhatis = (Preference) findPreference("bechdelwhat");
            final SwitchPreference notifications = (SwitchPreference) findPreference("suggestions");
            omdbapipref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.themoviedb.org"));
                    startActivity(i);
                    return true;
                }
            });
            bechdeltestpref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://www.bechdeltest.com/api/v1/doc"));
                    getActivity().startActivity(i);
                    return true;
                }
            });
            bechdelwhatis.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://vimeo.com/85232647"));
                    getActivity().startActivity(i);
                    return true;
                }
            });
        }


    }

}
