package com.luqi.choresprovider;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";
    Utils mUtils;
    Context mContext;

    private boolean mResolvingError = false;
    Location mCurrentLocation;
    Boolean mRequestingLocationUpdates = true;
    String mLastUpdateTime;

    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "getting_location_updates";
    private static final String LOCATION_KEY = "location";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_update_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mUtils = new Utils(mContext);
        setContentView(R.layout.activity_main);
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
        createView();
    }

    private void createView(){
        if(mUtils.getFromPreferences(Utils.REGISTRED) !="True"){
            Fragment fragment = new FragmentRegister();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.main_content_frame, fragment, FragmentRegister.TAG);
            transaction.addToBackStack(FragmentRegister.TAG);
            transaction.commit();
        }else{
            if(mUtils.getFromPreferences(Utils.PROPERTY_CURRENT_STATUS) =="Engaged"){
                Fragment fragment = new FragmentDirections();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.main_content_frame, fragment, FragmentDirections.TAG);
                transaction.addToBackStack(FragmentDirections.TAG);
                transaction.commit();
            }else if (mUtils.getFromPreferences(Utils.PROPERTY_CURRENT_STATUS) =="Working"){
                Fragment fragment = new FragmentWorking();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.main_content_frame, fragment, FragmentWorking.TAG);
                transaction.addToBackStack(FragmentWorking.TAG);
                transaction.commit();
            }else{
                Fragment fragment = new FragmentHome();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.main_content_frame, fragment, FragmentHome.TAG);
                transaction.addToBackStack(FragmentHome.TAG);
                transaction.commit();
            }
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
//                setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
//            updateUI();
        }
    }
}
