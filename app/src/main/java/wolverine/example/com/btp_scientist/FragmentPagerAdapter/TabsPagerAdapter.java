package wolverine.example.com.btp_scientist.FragmentPagerAdapter;

/**
 * Created by Wolverine on 25-06-2015.
 */
import android.app.ListFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.util.Log;

import wolverine.example.com.btp_scientist.R;

/*the listener interface - handles the tab selections
        www.101apps.co.za*/
public class TabsPagerAdapter<T extends Fragment>
        implements ActionBar.TabListener, android.app.ActionBar.TabListener {

    private Fragment fragment;
    private static final String TAG = "junk";

    public TabsPagerAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
        Log.i(TAG, "Tab " + tab.getText() + " ReSelected");
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.replace(R.id.container,fragment, null);
        Log.i(TAG, "Tab " + tab.getText() + " selected");

    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
        Log.i(TAG, "Tab " + tab.getText() + " UnSelected");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
    }

    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }
}









