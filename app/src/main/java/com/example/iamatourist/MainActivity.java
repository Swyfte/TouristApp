package com.example.iamatourist;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private final int LOCATION_REQUEST_CODE = 103;

    private FloatingActionButton fab;
    private boolean hasCamera = true;
    private Trip currentTrip = null;
    private String saveLoc;
    private String appLanguage;
    private Bitmap cameraPhoto;
    private Image currentImage = null;

    boolean canCamera = false;
    boolean canWFiles = false;
    boolean canRFiles = false;
    boolean canLocation = false;

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
        PackageManager packageManager = this.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            fab.setEnabled(false);
            fab.hide();
            hasCamera = false;
        }

        //Make the camera open on clicking the fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTrip != null) {
                    try {
                        doPermissions(CAMERA_REQUEST_CODE);
                        //if (canCamera) openCameraIntent();
                        openCameraIntent();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "IOException caught", Toast.LENGTH_SHORT).show();
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

    private void doPermissions(Integer reqCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (reqCode) {
                case CAMERA_REQUEST_CODE: {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                Manifest.permission.CAMERA)) {
                            //What do?
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    CAMERA_REQUEST_CODE);
                        }
                    } else {
                        //?
                    }
                }
                case READ_REQUEST_CODE: {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Snackbar.make(this.findViewById(R.id.thisLayout),
                                    "Storage permissions required for this app to work",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ActivityCompat.requestPermissions(MainActivity.this,
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    WRITE_REQUEST_CODE);
                                        }
                                    });
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    READ_REQUEST_CODE);
                        }
                    }
                }
                case WRITE_REQUEST_CODE: {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Snackbar.make(this.findViewById(R.id.thisLayout),
                                    "Storage permissions required for this app to work",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ActivityCompat.requestPermissions(MainActivity.this,
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    WRITE_REQUEST_CODE);
                                        }
                                    });
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    WRITE_REQUEST_CODE);
                        }
                    }
                }
                case LOCATION_REQUEST_CODE: {
                    if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            //Location is not urgent
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    LOCATION_REQUEST_CODE);
                        }
                    }
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Permission granted?
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Permission granted?
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Permission granted?
            }
        }

    }

    /**
     * This method, and makeImageFile() were sourced from:
     * https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
     * <p>
     * The purpose of this method is to handle the app switching to the camera, and facilitating the return.
     *
     * @throws IOException if the file cannot be made
     */
    private void openCameraIntent() throws IOException {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                doPermissions(WRITE_REQUEST_CODE);
                //if (canWFiles) photoFile = makeImageFile();
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
                Toast.makeText(this, "Photofile = null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method is also sourced from:
     * https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
     * <p>
     * Its purpose is to create a temporary file where the image is saved.
     * The child of the base DCIM directory is name of the app, followed by the trip name.
     *
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
                        canCamera = true;
                    }
                } else {
                    fab.setEnabled(false);
                    canCamera = false;
                }
                return;
            }
            case READ_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canRFiles = true;
                } else {
                    canRFiles = false;
                }
            }

            case WRITE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canWFiles = true;
                } else {
                    canWFiles = false;
                }
            }
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canLocation = true;
                } else {
                    canLocation = false;
                }
            }
        }
    }

    /**
     * Uses a custom layout to display entry points for Time, Date and Location
     */
    private void firstDialog() {
        final Context context = this;
        final Bitmap photo = cameraPhoto;

        final Dialog dialog = new Dialog(context);

        int rotation = this.getResources().getConfiguration().orientation;

        if (rotation == Configuration.ORIENTATION_PORTRAIT) {
            dialog.setContentView(R.layout.dialog_first);
        } else {
            dialog.setContentView(R.layout.dialog_first_land);
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final ImageView imagePrev = dialog.findViewById(R.id.image_preview_1);
        imagePrev.setImageBitmap(photo);

        final Button date = dialog.findViewById(R.id.date_btn_img);

        /*
         * Due to the different constructors in different API levels, I have two different ways
         * of initialising this date picker.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            date.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog);
                    dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                            date.setText(String.format(Locale.getDefault(), "%d-%d-%d", d, m + 1, y));
                        }
                    });
                    dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dpd.show();
                }
            });
        } else {
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                    date.setText(String.format(Locale.getDefault(), "%d-%d-%d", d, m + 1, y));
                                }
                            }, 2001, 0, 0);
                    dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dpd.show();
                }
            });
        }

        final Button timeBtn = dialog.findViewById(R.id.time_btn_img);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timeBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR);
                    int min = c.get(Calendar.MINUTE);
                    TimePickerDialog tpd = new TimePickerDialog(MainActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int h, int m) {
                                    timeBtn.setText(String.format(Locale.getDefault(), "%d:%d", h, m));
                                }
                            }, hour, min, true);
                    tpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    tpd.show();
                }
            });
        } else {
            timeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog tpd = new TimePickerDialog(MainActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int h, int m) {
                                    timeBtn.setText(String.format(Locale.getDefault(), "%d:%d", h, m));
                                }
                            }, 0, 0, true);
                    tpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    tpd.show();
                }
            });
        }
        final EditText loc = dialog.findViewById(R.id.loc_btn_img);

        ////Funcionality does not work in API < 21////
        Button auto = dialog.findViewById(R.id.auto_btn);
        auto.setVisibility(View.INVISIBLE);

        ImageButton next = dialog.findViewById(R.id.next_btn_img_1);
        ImageButton close = dialog.findViewById(R.id.cancel_button_1);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImage = null;
                dialog.dismiss();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date t = new Date();
                if (!timeBtn.getText().equals(getResources().getString(R.string.select_time))) {
                    DateFormat df = new SimpleDateFormat("hh:mm", Locale.getDefault());
                    try {
                        t = df.parse(timeBtn.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Date d = new Date();
                if (!date.getText().equals(getResources().getString(R.string.select_date))) {
                    DateFormat df = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
                    try {
                        d = df.parse(date.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                String l = loc.getText().toString();
                currentImage = new Image(photo, d, t, l);
                dialog.dismiss();
                secondDialog();
            }
        });
        dialog.show();
    }

    /**
     * Sourced from https://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address/27834110#27834110
     * Modified to return the location, instead of the lat and long
     *
     * NOTE: No longer being used; Location changed to String.
     * Code remaining in case of subsequent changes.
     *
     * @param context the app's context
     * @param s the string name of the location
     * @return the top result of the locations list.
     */
    private Address getLocFromString(Context context, String s) {
        Geocoder g = new Geocoder(context);
        List<Address> addresses;
        Address location = null;
        try {
            addresses = g.getFromLocationName(s, 5);
            if (addresses == null) {
                return null;
            }
            location = addresses.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Uses a custom layout to display entry points for Title, desc and Tags
     */
    private void secondDialog() {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_second);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView image = dialog.findViewById(R.id.image_preview_2);
        image.setImageBitmap(currentImage.getPhoto());

        final TextView title = dialog.findViewById(R.id.title_edit_img);
        final TextView desc = dialog.findViewById(R.id.desc_edit_img);
        final TextView tags = dialog.findViewById(R.id.tags_edit_img);
        final Button submit = dialog.findViewById(R.id.submit_btn_img);
        final ImageButton close = dialog.findViewById(R.id.cancel_button_2);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImage = null;
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImage.setTitle(title.getText().toString());
                currentImage.setDesc(desc.getText().toString());
                ArrayList<String> tagsList = getTags(tags.getText().toString());
                if (tagsList != null) {
                    currentImage.setTags(tagsList);
                }
                currentTrip.addImage(currentImage);
                currentImage = null;
                dialog.dismiss();
            }
        });
        dialog.show();

        this.currentTrip.addImage(currentImage);
    }

    private ArrayList<String> getTags(String tagsList) {
        int i = 0;
        ArrayList<String> tags = new ArrayList<>();
        while (i < tagsList.length()) {
            String tag = "";
            char c = tagsList.charAt(i);
            if (!(c == ',')) {
                String letter = String.valueOf(c);
                tag = tag + letter;
                i++;
            } else {
                tags.add(tag);
                i += 2;
            }
        }
        if (tags.size() > 0) {
            return tags;
        } else {
            return null;
        }
    }

    private void tripDialog() {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_trip);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText titleBox = dialog.findViewById(R.id.edit_title_trip);
        final Button dateButton = dialog.findViewById(R.id.date_button_trip);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dateButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog);
                    dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                            dateButton.setText(String.format(Locale.getDefault(), "%d-%d-%d", d, m + 1, y));
                        }
                    });
                    dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dpd.show();
                }
            });
        } else {
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                    dateButton.setText(String.format(Locale.getDefault(), "%d-%d-%d", d, m + 1, y));
                                }
                            }, 2001, 0, 0);
                    dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dpd.show();
                }
            });
        }
        final EditText locEdit = dialog.findViewById(R.id.loc_edit_trip);
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
                Date d = new Date();
                if (!dateButton.getText().equals(getResources().getString(R.string.select_date))) {
                    DateFormat df = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
                    try {
                        d = df.parse(dateButton.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                String t = titleBox.getText().toString();
                String l = locEdit.getText().toString();
                currentTrip = new Trip(t,d,l);
                Objects.requireNonNull(getSupportActionBar()).setTitle(currentTrip.getTitle());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            doPermissions(READ_REQUEST_CODE);
            //if (canRFiles) {
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
            //}
            firstDialog();
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