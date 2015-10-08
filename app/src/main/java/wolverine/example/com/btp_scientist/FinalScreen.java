package wolverine.example.com.btp_scientist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import wolverine.example.com.btp_scientist.FragmentPagerAdapter.TabsPagerAdapter;


public class FinalScreen extends ActionBarActivity {

    private SessionManager session;
    private static final String TAG = "junk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);

        // session manager
        session = new SessionManager(getApplicationContext());

        setUpTabs(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //            save the selected tab's index so it's re-selected on orientation change
        outState.putInt("tabIndex", getSupportActionBar().getSelectedNavigationIndex());
    }

    private void setUpTabs(Bundle savedInstanceState) {

        ActionBar actionBar = getSupportActionBar();
        //noinspection deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation,deprecation
        actionBar.setNavigationMode(actionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab tab_one = actionBar.newTab();
        ActionBar.Tab tab_two = actionBar.newTab();
        ActionBar.Tab tab_three = actionBar.newTab();

        HomeFragment firstFragment = new HomeFragment();
        tab_one.setText("Home")
                .setContentDescription("The first tab")
                .setTabListener(
                        new TabsPagerAdapter<HomeFragment>(
                                firstFragment));

        LiveChatFragment secondFragment = new LiveChatFragment();
        tab_two.setText("All Querries")
                .setContentDescription("The second tab")
                .setTabListener(
                        new TabsPagerAdapter<LiveChatFragment>(
                                secondFragment));

        ProfilFragment thirdFragment = new ProfilFragment();
        tab_three
                .setText("Profile")
                .setContentDescription("The third tab")
                .setTabListener(
                        new TabsPagerAdapter<ProfilFragment>(
                                thirdFragment));


        actionBar.addTab(tab_one);

        actionBar.addTab(tab_two);

        actionBar.addTab(tab_three);

        if (savedInstanceState != null) {
            Log.i(TAG, "setting selected tab from saved bundle");
            //get the saved selected tab's index and set that tab as selected

            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tabIndex", 0));
        }
    }

}
