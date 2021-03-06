package upenn.edu.playscription;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;


public class DashboardActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private ProgressBar progressBar;
    private Button viewStatsbutton;
    private Button logWeightbutton;
    private Button logActivitybutton;
    private Button addPrescriptionButton;
    private TextView currPrescription;
    private String username;
    private String activityType;
    private int duration;
    private int frequency;
    private int durationTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        username = getIntent().getStringExtra("USERNAME");
        durationTotal = getIntent().getIntExtra("durationTotal",-1);
        if (durationTotal >= 300) {
            durationTotal = 300;
            Toast.makeText(DashboardActivity.this, "Congratulations! You have reached 300 points!",
                    Toast.LENGTH_LONG).show();
        }
        if (durationTotal == -1) {
            durationTotal = 0;
            ParseQuery<ParseObject> activities = ParseQuery.getQuery("Activity");
            activities.whereEqualTo("username", username);
            try {
                JSONArray activityInts = activities.getFirst().fetch().getJSONArray("Durations");
                for (int i = 0; i < activityInts.length(); i++) {
                    try {
                        durationTotal += activityInts.getInt(i);
                    } catch (JSONException jse) {
                        jse.printStackTrace();
                    }
                }
            } catch (com.parse.ParseException pe) {
                pe.printStackTrace();
            }
        }

        final Context context = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        viewStatsbutton = (Button) findViewById(R.id.view_stats);
        viewStatsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, StatsActivity.class);
                i.putExtra("USERNAME", username);
                startActivity(i);
            }
        });

        logActivitybutton = (Button) findViewById(R.id.log_activity);
        logActivitybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, LogActivityActivity.class);
                i.putExtra("USERNAME", username);
                startActivity(i);
            }
        });

        logWeightbutton = (Button) findViewById(R.id.log_weight);
        logWeightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, LogWeightActivity.class);
                i.putExtra("USERNAME", username);
                startActivity(i);
            }
        });

        addPrescriptionButton = (Button) findViewById(R.id.add_prescription);
        addPrescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EnterPlayscriptionActivity.class);
                i.putExtra("USERNAME", username);
                startActivity(i);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this,"Progress: " + durationTotal + "/300",Toast.LENGTH_LONG).show();
            }
        });
        int percentage = (int) ((durationTotal/300.0) * 100.0);
        progressBar.setProgress(percentage);

        currPrescription = (TextView) findViewById(R.id.currPrescription);
        String text = "";
        try {
            ParseQuery<ParseObject> users = ParseQuery.getQuery("Playscription");
            users.whereEqualTo("username", username);
            text += "\n" + users.getFirst().fetch().getString("activityType") + " for "
                    + users.getFirst().fetch().getInt("duration") + " minutes, " +
                      users.getFirst().fetch().getInt("frequency") + "x per week";

            currPrescription.setText(text);
        }
        catch (Exception e) {
            Toast toast = Toast.makeText(context, "Database error", Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        }

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
            case 4:
                mTitle = getString(R.string.title_section4);
                Intent i = new Intent(DashboardActivity.this, ViewMessagesActivity.class);
                i.putExtra("USERNAME", username);
                startActivity(i);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                break;
            case 7:
                mTitle = getString(R.string.title_section7);
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
            getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

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
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((DashboardActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
