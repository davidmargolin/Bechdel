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
                    i.setData(Uri.parse("http://www.omdbapi.com"));
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
            notifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isenabled = (Boolean) newValue;
                    SharedPreferences.Editor e = mPrefs.edit();
                    e.putBoolean("suggestions", isenabled);
                    e.commit();

                    Intent intent1 = new Intent(getActivity(), NotificationReciever.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getActivity(), 0, intent1,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager am = (AlarmManager) getActivity()
                            .getSystemService(getActivity().ALARM_SERVICE);
                    if (isenabled) {
                        notifications.setChecked(true);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, 20);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY, pendingIntent);

                        //enable notifications
                    } else {
                        notifications.setChecked(false);
                        am.cancel(pendingIntent);
                        //disable notifications
                    }
                    return false;
                }
            });
        }


    }

}
