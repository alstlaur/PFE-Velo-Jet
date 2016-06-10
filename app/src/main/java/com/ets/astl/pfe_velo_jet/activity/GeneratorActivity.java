package com.ets.astl.pfe_velo_jet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ets.astl.pfe_velo_jet.R;
import com.ets.astl.pfe_velo_jet.entity.GlobalData;
import com.ets.astl.pfe_velo_jet.task.ProfileTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.concurrent.ExecutionException;

public class GeneratorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.generator_map);
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
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_generator) {
            //Current nav
            //Do nothing
        } else if (id == R.id.nav_logout) {
            //logout the user and return to the login activity
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
