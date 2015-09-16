package sdkexample.citrus.com.testsdkapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.classes.AccessToken;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusUMResponse;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    static CitrusClient citrusClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        initSDK();
        initListeners();
    }

    private void initListeners() {

    }

    private void initSDK() {

        citrusClient = CitrusClient.getInstance(MainActivity.this);
        citrusClient.enableLog(Constants.enableLogging);

        citrusClient.init(Constants.SIGNUP_ID, Constants.SIGNUP_SECRET, Constants.SIGNIN_ID, Constants.SIGNIN_SECRET, Constants.VANITY, Constants.environment);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment  {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        @Bind(R.id.btnSignup)
        Button btnSignUp;
        @Bind(R.id.btnSignIn)
        Button btnSignIn;
        @Bind(R.id.btnGetBalance)
        Button btnGetBalance;
        @Bind(R.id.btnResetPassword)
        Button btnResetPassword;
        String emailID = "mangesh.kadam@citruspay.com";

        String password = "Mangesh@123";
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, rootView);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @OnClick(R.id.btnSignup)
        void signUp() {
            citrusClient.getSignUPToken(new Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken) {
                    Logger.d("TOKEN ***" + accessToken.getJSON().toString());
                }

                @Override
                public void error(CitrusError error) {

                }
            });
        }

        @OnClick(R.id.btnSignIn)
        void signIn() {
            citrusClient.signIn(emailID, password, new Callback<CitrusResponse>() {
                @Override
                public void success(CitrusResponse citrusResponse) {
                    Logger.d("SIGNIN TOKEN ***" + citrusResponse.getMessage());
                }

                @Override
                public void error(CitrusError error) {

                }
            });
        }

        @OnClick(R.id.btnGetBalance)
        void getBalance() {
            citrusClient.getBalance(new Callback<Amount>() {
                @Override
                public void success(Amount amount) {
                    Logger.d("Amount ****" + amount.getValue());
                }

                @Override
                public void error(CitrusError error) {

                }
            });
        }

        @OnClick(R.id.btnResetPassword)
        void resetPassword() {
            citrusClient.resetUserPassword(emailID, new Callback<CitrusUMResponse>() {
                @Override
                public void success(CitrusUMResponse citrusUMResponse) {
                    Logger.d("RESET PWD RESPONSE ***" + citrusUMResponse.getJSON().toString());
                }

                @Override
                public void error(CitrusError error) {

                }
            });
        }
    }

}
