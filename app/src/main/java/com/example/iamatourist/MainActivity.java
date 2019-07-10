package com.example.iamatourist;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private final int CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Connect the class to the layout
        setContentView(R.layout.activity_main);
        //Connect the toolbar to the class
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Locate the floating action button
        fab = findViewById(R.id.fab);
        //Make the camera open on clicking the fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {

                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    }
                } else {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, 0);
                }
            }
        });
        //On a new app launch, start to the gallery screen
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, (new GalleryFragment()));
            transaction.commit();
        }
        //Assign the drawer to the class
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //Create the navigation header
        NavigationView navigationView = findViewById(R.id.nav_view);
        //Create a toggling action that changes whether the drawer is open or closed
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //Connect the toggle to the drawer as a listener
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Set the listener for the navigation drawer
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * This enables the fab if the app has got permission from the user to use the camera
     * @param requestCode the code, in this case 0
     * @param permissions the type of permission that it is asking for
     * @param grantResults the results of the permission checks
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fab.setEnabled(true);
                } else {
                    fab.setEnabled(false);
                }
                return;
            }
        }
    }

    /**
     * Uses a custom layout to display entry points for Time, Date and Location
     * @param imageLoc The location of the Image file
     * @return returns an Image with half the data filled in
     */
    private Image firstDialog(final Uri imageLoc) {
        final Image image = new Image();
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_first);
        final ImageView imagePrev = dialog.findViewById(R.id.image_preview);
        imagePrev.setImageURI(imageLoc);

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
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                secondDialog(image);
            }
        });

        dialog.show();

        return image;
    }

    /**
     * Uses a custom layout to display entry points for Title, desc and Tags
     * @param img The image with the details from the previous screen
     * @return Returns the image as an item
     */
    private Image secondDialog(final Image img) {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_second);

        final ImageView image = dialog.findViewById(R.id.image_preview);
        image.setImageBitmap(img.getPhoto());

        TextView title = dialog.findViewById(R.id.EditTitle);
        TextView desc = dialog.findViewById(R.id.EditDesc);
        TextView tags = dialog.findViewById(R.id.EditTags);
        Button submit = dialog.findViewById(R.id.submit_button);
        ImageButton close = dialog.findViewById(R.id.cancel_button);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri loc = img.getFileLoc();
                File img = new File(loc.getPath());
                if (img.exists()) {
                    img.delete();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

        return img;
    }

    /**
     * This override closes the drawer if it's open, or otherwise calls the original onBackPressed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * This override controls the navigation by switching the fragment displayed when an item
     * on the drawer is selected
     * @param item The item selected by the user
     * @return Returns true, presumably to mark a success
     */
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

    /**
     * This override prevents the data being lost when the activity is rebuilt,
     * for example, when the screen is rotated
     * @param outState the current state of the activity
     * @param outPersistentState the state, in a persistent manner
     */
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
