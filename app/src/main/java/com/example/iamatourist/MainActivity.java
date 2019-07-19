package com.example.iamatourist;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GalleryFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        SlideshowFragment.OnFragmentInteractionListener,
        TripsFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener {

    private FloatingActionButton fab;
    private final int CAMERA_REQUEST_CODE = 100;
    private boolean hasCamera = true;
    private Trip currentTrip = null;
    private File saveLoc = null;
    private File cameraFile = null;
    private String appLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Connect the class to the layout
        setContentView(R.layout.activity_main);
        //Connect the toolbar to the class
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_home));

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            appLanguage = Locale.getDefault().getLanguage();
        } else {
            appLanguage = extras.getString("appLang");
        }

        //Locate the floating action button
        fab = findViewById(R.id.fab);
        /* Check the system build has a camera, if not, then disable and hide the fab
         * This is to prevent a case of the program attempting to launch a camera that isn't there.
         * This should help prevent crashes.
         */
        PackageManager packageManager = this.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            fab.setEnabled(false);
            fab.hide();
            hasCamera = false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Permission granted?
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Permission granted?
            }
        }

        //Make the camera open on clicking the fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {

                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    }} else {*/
                if (currentTrip != null) {
                    saveLoc = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/touristApp/" + currentTrip.getTitle());
                    //cameraFile = new File(saveLoc.getPath() + "TestImage.jpg");
                    cameraFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "TestImage.jpg");
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                    startActivityForResult(i, CAMERA_REQUEST_CODE);
                } else {
                    tripDialog();
                }
            }//}
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



    public String getAppLanguage() {
        return appLanguage;
    }

    public void setAppLanguage(String newLanguage) {
        if (!this.appLanguage.equals(newLanguage)) {
            this.appLanguage = newLanguage;
            Resources res = this.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale(this.appLanguage.toLowerCase()));
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra("appLang", appLanguage);
            startActivity(refresh);
            finish();
        }
    }

    /**
     * This enables the fab if the app has got permission from the user to use the camera
     *
     * @param requestCode  the code, in this case 0
     * @param permissions  the type of permission that it is asking for
     * @param grantResults the results of the permission checks
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasCamera) {
                        fab.setEnabled(true);
                    }
                } else {
                    fab.setEnabled(false);
                }
                return;
            }
        }
    }

    /**
     * Uses a custom layout to display entry points for Time, Date and Location
     */
    private void firstDialog() {
        final Image image = new Image();
        final Context context = this;
        Bitmap img = BitmapFactory.decodeFile(cameraFile.getPath());

        final Dialog dialog = new Dialog(context);

        Integer rotation = this.getResources().getConfiguration().orientation;

        if (rotation == Configuration.ORIENTATION_PORTRAIT) {
            dialog.setContentView(R.layout.dialog_first);
        } else {
            dialog.setContentView(R.layout.dialog_first_land);
        }
        dialog.getWindow().

                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final ImageView imagePrev = dialog.findViewById(R.id.image_preview_1);
        imagePrev.setImageBitmap(img);

        Button date = dialog.findViewById(R.id.date_btn_img);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Open date picker dialog
            }
        });
        Button time = dialog.findViewById(R.id.time_btn_img);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Open time picker dialog
            }
        });
        Button loc = dialog.findViewById(R.id.loc_btn_img);
        Button auto = dialog.findViewById(R.id.auto_btn);
        ImageButton next = dialog.findViewById(R.id.next_btn_img_1);
        ImageButton close = dialog.findViewById(R.id.cancel_button_1);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File img = new File(cameraFile.getPath());
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
    }

    /**
     * Uses a custom layout to display entry points for Title, desc and Tags
     *
     * @param img The image with the details from the previous screen
     * @return Returns the image as an item
     */
    private Image secondDialog(final Image img) {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_second);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final ImageView image = dialog.findViewById(R.id.image_preview_1);
        image.setImageBitmap(img.getPhoto());

        TextView title = dialog.findViewById(R.id.title_edit_img);
        TextView desc = dialog.findViewById(R.id.desc_edit_img);
        TextView tags = dialog.findViewById(R.id.tags_edit_img);
        Button submit = dialog.findViewById(R.id.submit_btn_img);
        ImageButton close = dialog.findViewById(R.id.cancel_button_2);

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

    private void tripDialog() {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_trip);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText titleBox = dialog.findViewById(R.id.edit_title_trip);
        final Button dateButton = dialog.findViewById(R.id.date_button_trip);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO display date picker dialog
            }
        });
        final Button locButton = dialog.findViewById(R.id.loc_button_trip);
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO display location options
            }
        });
        ImageButton cancelButton = dialog.findViewById(R.id.cancel_button_trip);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button submit = dialog.findViewById(R.id.submit_btn_trip);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTrip = new Trip();
                currentTrip.setTitle(titleBox.getText().toString());
                getSupportActionBar().setTitle(currentTrip.getTitle());
                dialog.dismiss();
                //TODO publish trip to global variable and make persistent
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                firstDialog();
            }
        }
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
     *
     * @param item The item selected by the user
     * @return Returns true, presumably to mark a success
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String title = "";
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment f = new Fragment();

        if (id == R.id.nav_home) {
            fab.show();
            f = new GalleryFragment();
            if (currentTrip != null) {
                title = currentTrip.getTitle();
            } else {
                title = getResources().getString(R.string.menu_home);
            }
        } else if (id == R.id.nav_Search) {
            fab.show();
            f = new SearchFragment();
            title = getResources().getString(R.string.menu_search);
        } else if (id == R.id.nav_trips) {
            fab.show();
            f = new TripsFragment();
            title = getResources().getString(R.string.menu_trips);
        } else if (id == R.id.nav_slideshow) {
            fab.hide();
            f = new SlideshowFragment();
            title = getResources().getString(R.string.menu_slideshow);
        } else if (id == R.id.nav_settings) {
            fab.hide();
            f = new SettingsFragment();
            title = getResources().getString(R.string.menu_settings);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, f);
        transaction.addToBackStack(null);
        transaction.commit();
        getSupportActionBar().setTitle(title);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This override prevents the data being lost when the activity is rebuilt,
     * for example, when the screen is rotated
     *
     * @param savedInstanceState the current state of the activity
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("savedLanguage", appLanguage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String language = savedInstanceState.getString("savedLanguage");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}