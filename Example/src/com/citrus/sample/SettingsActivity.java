    package com.citrus.sample;

    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.preference.EditTextPreference;
    import android.preference.ListPreference;
    import android.preference.Preference;
    import android.preference.PreferenceActivity;
    import android.preference.PreferenceCategory;

    /**
     * Created by Gautam on 28/9/15.
     */
    public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();

            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);

            initSummary();
        }

        @Override
        protected void onPause() {
            super.onPause();

            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    //update summary
            updatePrefsSummary(sharedPreferences, findPreference(key));
        }

        /*
       * Init summary
       */
        protected void initSummary() {
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                initPrefsSummary(getPreferenceManager().getSharedPreferences(),
                        getPreferenceScreen().getPreference(i));
            }
        }

        /*
         * Init single Preference
         */
        protected void initPrefsSummary(SharedPreferences sharedPreferences,
                                        Preference p) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory pCat = (PreferenceCategory) p;
                for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                    initPrefsSummary(sharedPreferences, pCat.getPreference(i));
                }
            } else {
                updatePrefsSummary(sharedPreferences, p);
            }
        }

        /**
         * Update summary
         *
         * @param sharedPreferences
         * @param pref
         */
        protected void updatePrefsSummary(SharedPreferences sharedPreferences,
                                          Preference pref) {

            if (pref == null)
                return;

            if (pref instanceof ListPreference) {
                // List Preference
                ListPreference listPref = (ListPreference) pref;
                listPref.setSummary(listPref.getEntry());

            } else if (pref instanceof EditTextPreference) {
                // EditPreference
                EditTextPreference editTextPref = (EditTextPreference) pref;
                editTextPref.setSummary(editTextPref.getText());

            }
        }
    }
