package com.example.iamatourist;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GalleryFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        SlideshowFragment.OnFragmentInteractionListener,
        TripsFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener {

    private final int CAMERA_REQUEST_CODE = 100;
    private final int READ_REQUEST_CODE = 101;
    private final int WRITE_REQUEST_CODE = 102;

    private FloatingActionButton fab;
    private boolean hasCamera = true;
    private Trip currentTrip = null;
    private String saveLoc;
    private File cameraFile = null;
    private String appLanguage;
    private Bitmap cameraPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Connect the class to the layout
        setContentView(R.layout.activity_main);
        //Connect the toolbar to the class
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.menu_home));

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
        doPermissions();

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
                    try {
                        openCameraIntent();
                    }
                    catch (IOException e) {
                        Toast.makeText(MainActivity.this,"IOException caught", Toast.LENGTH_SHORT).show();
                    }
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

    private void doPermissions() {
        PackageManager packageManager = this.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            fab.setEnabled(false);
            fab.hide();
            hasCamera = false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //Show explanation
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_REQUEST_CODE);
                }
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Snackbar.make(this.findViewById(R.id.thisLayout), "Storage permissions required for this app to work", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_REQUEST_CODE);
                        }
                    });
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Permission granted?
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Permission granted?
            }
        }
    }

    /**
     * This method, and makeImageFile() were sourced from:
     * https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
     *
     * The purpose of this method is to handle the app switching to the camera, and facilitating the return.
     * @throws IOException if the file cannot be made
     */
    private void openCameraIntent() throws IOException {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = makeImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "File make failed", Toast.LENGTH_SHORT).show();
                System.out.println(ex);
                return;
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", makeImageFile());
                i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(i, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(this,"Photofile = null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method is also sourced from:
     * https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
     *
     * Its purpose is to create a temporary file where the image is saved.
     * The child of the base DCIM directory is name of the app, followed by the trip name.
     * @return Returns the image save location as a File
     * @throws IOException
     */
    private File makeImageFile() throws IOException {
        String fileName = "TempImage";
        File StorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), ("/TouristApp/" + currentTrip.getTitle()));
        StorageDir.mkdirs();
        File image = File.createTempFile(
                fileName, ".jpg", StorageDir
        );
        saveLoc = "file:" + image.getAbsolutePath();
        return image;
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
            case READ_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
                    //Do... The task?
                } else {
                    //Disable functionality, display "This app will not work" warning
                }
            }
        }
    }

    /**
     * Uses a custom layout to display entry points for Time, Date and Location
     */
    private void firstDialog() {
        final Image image = new Image();
        final Context context = this;
        Bitmap img = cameraPhoto;


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
    private void secondDialog(final Image img) {
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

        this.currentTrip.addImage(img);
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
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)  {
            Uri imageUri = Uri.parse(saveLoc);
            File file = new File(imageUri.getPath());
            try {
                InputStream ims = new FileInputStream(file);
                cameraPhoto = BitmapFactory.decodeStream(ims);
            } catch (FileNotFoundException e) {
                return;
            }

            MediaScannerConnection.scanFile(MainActivity.this,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String s, Uri uri) {

                }
            });
        }
        firstDialog();
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