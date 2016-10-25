package com.fairphone.checkup.tests.gps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by maarten on 26/09/16.
 */

public class GPSTest extends Test {

    private static final String TAG = GPSTest.class.getSimpleName();

    View mTestView;

    LocationManager locationManager;
    LocationListener locationListener;

    public GPSTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.gps_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.gps_test_description;
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_gps_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void onPrepare() {
        locationManager = (LocationManager)getContext().getSystemService(LOCATION_SERVICE);
    }


    @Override
    protected void runTest() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            buildAlertMessageNoGps();
        }

        replaceView();

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onCleanUp() {
        locationManager.removeUpdates(locationListener);
        super.onCleanUp();
    }

    private void makeUseOfNewLocation(Location location) {
        ((TextView)findViewById(R.id.gps_satellites_value)).setText(""+location.getExtras().getInt("satellites", -1));
        ((TextView)findViewById(R.id.gps_accuracy_value)).setText(""+location.getAccuracy());
        ((TextView)findViewById(R.id.gps_latitude_value)).setText(""+location.getLatitude());
        ((TextView)findViewById(R.id.gps_longitude_value)).setText(""+location.getLongitude());
        ((TextView)findViewById(R.id.gps_altitude_value)).setText(""+location.getAltitude());
        ((TextView)findViewById(R.id.gps_bearing_value)).setText(""+location.getBearing());
        ((TextView)findViewById(R.id.gps_speed_value)).setText(""+location.getSpeed());
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        getContext().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        onTestFailure();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}