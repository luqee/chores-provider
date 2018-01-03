package com.luqi.choresprovider;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements FragmentHome.FragmentHomeListener, FragmentWorking.FragmentWorkingListener,FragmentDirections.FragmentDirectionsListener, FragmentRegister.FragmentRegisterListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    public static String TAG = "MainActivity";
    Utils mUtils;
    Context mContext;
    GoogleApiClient googleApiClient;

    private boolean mResolvingError = false;
    Location mCurrentLocation;
    Boolean mRequestingLocationUpdates = true;
    String mLastUpdateTime;

    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "getting_location_updates";
    private static final String LOCATION_KEY = "location";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_update_time";

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

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

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mCurrentLocation != null) {
            Log.d(TAG, "Location object not null ::: " + mCurrentLocation.toString());
//            LocationRequest locationRequest = createLocationRequest();
            mUtils.savePreferences(Utils.USER_LATITUDE, String.valueOf(mCurrentLocation.getLatitude()));
            mUtils.savePreferences(Utils.USER_LONGITUDE, String.valueOf(mCurrentLocation.getLongitude()));
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationRequest locationRequest = createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                googleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "google api error");
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mUtils.savePreferences(Utils.USER_LATITUDE, String.valueOf(location.getLatitude()));
        mUtils.savePreferences(Utils.USER_LONGITUDE, String.valueOf(location.getLongitude()));
    }

    @Override
    public void onSetStatus(String status) {
        if (mUtils.getFromPreferences(Utils.PROPERTY_CURRENT_STATUS) != status){
            setStatus(status);
        }
    }

    public void setStatus(final String status){
        new AsyncTask<Void, Void, JSONObject>(){

            @Override
            protected JSONObject doInBackground(Void... params) {
                Log.d(TAG, "In do in background, setting status::"+ status);
                HashMap<String, String> nameValuePairs = new HashMap<>();
                JSONParser parser = new JSONParser();
                nameValuePairs.put("user_id", mUtils.getFromPreferences(Utils.USER_ID));
                nameValuePairs.put("status", status);

                Log.d(TAG, "create namevalue pairs");
                JSONObject jsonObject = parser.makeHttpRequest(mUtils.getCurrentIPAddress() +"tatua/api/v1.0/auth/provider/changeStatus", "POST",nameValuePairs);
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    String response = jsonObject.getString("message");

                    if (response.equals("Success")){
                        Log.d(TAG, "Successfull update ; ; "+jsonObject.getString("current_status"));
                        //show progress bar
                        mUtils.savePreferences(Utils.PROPERTY_CURRENT_STATUS, jsonObject.getString("current_status"));
                        //remove progress
                    }else if (response.equals("error")){
                        Log.d(TAG, "Error in registration");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(null, null, null);
    }

    @Override
    public void registrationComplete() {
        Log.d(TAG, "Registration complete");
        mUtils.savePreferences(Utils.REGISTRED, "True");
        getSupportFragmentManager().popBackStack();
        Fragment fragment = new FragmentHome();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_content_frame, fragment, FragmentHome.TAG);
        transaction.addToBackStack(FragmentHome.TAG);
        transaction.commit();
    }

    @Override
    public void onButtonShowCode() {
        Log.d(TAG, "Showing the code to be scanned");
        Fragment fragment = new FragmentQRCode();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_content_frame, fragment, FragmentQRCode.TAG);
        transaction.addToBackStack(FragmentQRCode.TAG);
        transaction.commit();
    }

    @Override
    public void onReportSubmit(final String dsc, final String cst) {
        new AsyncTask<Void, Void, JSONObject>(){

            @Override
            protected JSONObject doInBackground(Void... params) {
                Log.d(TAG, "Starting registration");
                HashMap<String, String> nameValuePairs = new HashMap<>();
                JSONParser parser = new JSONParser();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                Log.d(TAG,"The time finised is::"+timeStamp);
                nameValuePairs.put("f_time", timeStamp);
                nameValuePairs.put("desc", dsc);
                nameValuePairs.put("cost", cst);
                nameValuePairs.put("t_id", mUtils.getFromPreferences(Utils.TRANSACTION_ID));
                Log.d(TAG, "create namevalue pairs");
                JSONObject jsonObject = parser.makeHttpRequest(mUtils.getCurrentIPAddress() +"tatua/api/v1.0/auth/provider/submitWork", "POST",nameValuePairs);
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    String response = jsonObject.getString("message");

                    if (response.equals("Success")){
                        Log.d(TAG, "Successfull submit report ");
                        //show progress bar
                        mUtils.savePreferences(Utils.PROPERTY_CURRENT_STATUS, "");
                        //remove progress
                        getSupportFragmentManager().popBackStack();
                        Fragment fragment = new FragmentHome();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.main_content_frame, fragment, FragmentHome.TAG);
                        transaction.addToBackStack(FragmentHome.TAG);
                        transaction.commit();
                    }else if (response.equals("error")){
                        Log.d(TAG, "Error in registration");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(null, null, null);
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity)getActivity()).onDialogDismissed();
        }
    }
    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!googleApiClient.isConnecting() &&
                        !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }
}
