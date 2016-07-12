package com.ets.astl.pfe_velo_jet.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ets.astl.pfe_velo_jet.R;
import com.ets.astl.pfe_velo_jet.entity.Path;
import com.ets.astl.pfe_velo_jet.managers.FileManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;

public class HistoryItemActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private Path path;

    private TextView tvDistance, tvSpeed, tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_item);

        int id = getIntent().getIntExtra("path_id", 0);
        path = FileManager.getInstance(this).getPath(id);

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

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.item_map);
        mapFragment.getMapAsync(this);

        View app_bar = findViewById(R.id.hi_app_bar_include);
        View content = app_bar.findViewById(R.id.hi_content_include);

        tvDistance = (TextView) content.findViewById(R.id.hi_distance);
        tvSpeed = (TextView) content.findViewById(R.id.hi_speed);
        tvTime = (TextView) content.findViewById(R.id.hi_time);

        tvDistance.setText(String.format(Locale.CANADA_FRENCH,"%.2f", path.getDistance() / 1000.0f));
        tvSpeed.setText(String.format(Locale.CANADA_FRENCH,"%.2f", path.getSpeed() * 3600.0f / 1000.0f));
        tvTime.setText(path.getTime());
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
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            //Current nav
            //Do nothing
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
        PolylineOptions rectOptions = new PolylineOptions()
                .color(Color.BLUE);

        for (int i = 0; i < path.getPoints().size(); i++) {
            rectOptions.add(path.getPoints().get(i));
        }

        // Get back the mutable Polyline
        Polyline polyline = googleMap.addPolyline(rectOptions);

        //move the camera to current location and zoom to building view
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(path.getPoints().get(path.getPoints().size() / 2), 15));
    }
}
