package com.ets.astl.pfe_velo_jet.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ets.astl.pfe_velo_jet.R;
import com.ets.astl.pfe_velo_jet.entity.GlobalData;
import com.ets.astl.pfe_velo_jet.entity.Path;
import com.ets.astl.pfe_velo_jet.managers.FileManager;
import com.ets.astl.pfe_velo_jet.task.ProfileTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.Time;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CaptureActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Button bStart, bStop, bSave;
    private Chronometer chrono;
    private TextView tDistance, tSpeed;

    private Path path = null;

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private Polyline polyline;

    private long timeWhenStopped = 0;
    private long nbCaptures = 0;

    private PowerManager pm;
    private PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GlobalData globalData = (GlobalData) getApplicationContext();

        View navView = navigationView.getHeaderView(0);
        TextView name = (TextView) navView.findViewById(R.id.user_name);
        name.setText(globalData.getAccount().getDisplayName());
        TextView email = (TextView) navView.findViewById(R.id.user_email);
        email.setText(globalData.getAccount().getEmail());
        ImageView image = (ImageView) navView.findViewById(R.id.user_image);

        if (globalData.getProfileImage() == null) {
            try {
                image.setImageDrawable(new ProfileTask(globalData).execute(globalData.getAccount().getPhotoUrl().toString()).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            image.setImageDrawable(globalData.getProfileImage());
        }

        /****** TESTING PURPOSE ********/
        path = new Path();
        path.setName("Bob Gainer");
        path.setDistance(5.50f);
        path.setDate(new Date());
        /*******************************/

        View app_bar = findViewById(R.id.main_f_include);
        View content = app_bar.findViewById(R.id.main_s_include);

        bStart = (Button) content.findViewById(R.id.start_button);
        bStop = (Button) content.findViewById(R.id.pause_button);
        bSave = (Button) content.findViewById(R.id.save_button);

        bStart.setOnClickListener(this);
        bStop.setOnClickListener(this);
        bSave.setOnClickListener(this);

        chrono = (Chronometer) content.findViewById(R.id.chronometer);
        tDistance = (TextView) content.findViewById(R.id.distance_label);
        tSpeed = (TextView) content.findViewById(R.id.speed_label);

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (!wl.isHeld()) {
            googleApiClient.disconnect();
        }
        Log.i("Velo-Jet", "connection stopped");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        googleApiClient.disconnect();
        if (wl.isHeld()) {
            wl.release();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_capture) {
            //open the capture fragment
            //Current nav
            //Do nothing
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_generator) {
            Intent intent = new Intent(this, GeneratorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 305);
        }
        googleMap.setMyLocationEnabled(true);
        this.googleMap = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 305: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                startCapture();
                break;
            case R.id.pause_button:
                stopCapture();
                break;
            case R.id.save_button:
                saveCapture();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //First call to get position
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 305);
        }

        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);

            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            // Instantiates a new Polyline object and adds points to define a rectangle
            PolylineOptions rectOptions = new PolylineOptions()
                    .color(Color.BLUE);

            // Get back the mutable Polyline
            polyline = googleMap.addPolyline(rectOptions);

            //move the camera to current location and zoom to building view
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

            //create LocationRequest for future location calls
            createLocationRequest();
        }
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Velo-Jet", "suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Velo-Jet", "failed");
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 305);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        path.getPoints().add(new LatLng(location.getLatitude(), location.getLongitude()));
        polyline.setPoints(path.getPoints());
        nbCaptures++;

        String speed = String.format(Locale.CANADA_FRENCH,"%.2f",calculateSpeed(location.getSpeed()));
        String distance = String.format(Locale.CANADA_FRENCH,"%.2f",calculateDistance(currentLocation.distanceTo(location)));

        tSpeed.setText(speed);
        tDistance.setText(distance);

        currentLocation = location;
    }

    private float calculateSpeed(float speed) {
        path.setSpeed((path.getSpeed() * (nbCaptures - 1)  + speed) / nbCaptures);

        return path.getSpeed() * (3600.0f / 1000.0f);
    }

    private float calculateDistance(float distance) {
        path.setDistance(path.getDistance() + distance);

        return path.getDistance() / 1000.0f;
    }

    /**** BUTTONS LISTENERS ****/

    private void startCapture() {
        Log.i("Velo-Jet", "Start capture");

        if (path == null) {
            path = new Path();
            path.setDate(new Date());
        }

        startLocationUpdates();

        chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chrono.start();

        bStart.setEnabled(false);
        bStop.setEnabled(true);
        bSave.setEnabled(false);

        wl.acquire();
    }

    private void stopCapture() {
        Log.i("Velo-Jet", "Stop capture");

        stopLocationUpdates();

        timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
        chrono.stop();

        bStart.setEnabled(true);
        bStop.setEnabled(false);
        bSave.setEnabled(true);

        wl.release();
    }

    private void saveCapture() {
        Log.i("Velo-Jet", "Save capture");

        if (path != null) {
            Time time = new Time(timeWhenStopped);
            path.setTime(time.toString());

            LayoutInflater li = LayoutInflater.from(this);
            View input = li.inflate(R.layout.input_box, null);

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setView(input);

            final EditText eInput = (EditText) input.findViewById(R.id.nameInput);

            alert.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            path.setName(eInput.getText().toString());
                        }
                    });

            AlertDialog dialog = alert.create();
            dialog.show();

            if (FileManager.getInstance(this).savePath(path) == 0) {
                Toast.makeText(getApplicationContext(), "La sauvegarde a réussie.", Toast.LENGTH_SHORT).show();
            }
        }

        bStart.setEnabled(true);
        bStop.setEnabled(false);
        bSave.setEnabled(false);
    }
}
