package com.example.iamatourist;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GalleryFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        SlideshowFragment.OnFragmentInteractionListener,
        TripsFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fab.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 0);
            }
        });
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, (new GalleryFragment()));
            transaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fab.setEnabled(true);
            }
        }
    }

    /**
     * Uses a custom dialog to display my popup interface
     * @param imageLoc
     * @return returns an image with half the data filled in
     */
    private image firstDialog(final Uri imageLoc) {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_first);
        final ImageView image = dialog.findViewById(R.id.image_preview);
        image.setImageURI(imageLoc);

        TextView date = dialog.findViewById(R.id.edit_date);
        TextView time = dialog.findViewById(R.id.edit_time);
        Button loc = dialog.findViewById(R.id.loc_button);
        Button auto = dialog.findViewById(R.id.auto_button);
        ImageButton next = dialog.findViewById(R.id.next_button);
        ImageButton close = dialog.findViewById(R.id.cancel_button);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File img = new File(imageLoc.getPath());
                if (img.exists()) {
                    img.delete();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

        return new image();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        Fragment f = new Fragment();

        if (id == R.id.nav_home) {
            f = new GalleryFragment();
        } else if (id == R.id.nav_Search) {
            f = new SearchFragment();
        } else if (id == R.id.nav_trips) {
            f = new TripsFragment();
        } else if (id == R.id.nav_slideshow) {
            f = new SlideshowFragment();
        } else if (id == R.id.nav_settings) {
            f = new SettingsFragment();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, f);
        transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle
            outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
