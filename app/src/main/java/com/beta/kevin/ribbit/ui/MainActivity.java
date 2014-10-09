package com.beta.kevin.ribbit.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.beta.kevin.ribbit.utils.ParseConstants;
import com.beta.kevin.ribbit.R;
import com.beta.kevin.ribbit.adapters.SectionsPagerAdapter;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final int TAKE_PHOTO_REQ = 0;
    public static final int GET_PHOTO_REQ = 1;
    public static final int TAKE_VIDEO_REQ = 2;
    public static final int GET_VIDEO_REQ = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    public static final int FILE_SIZE_LIMIT = 1024*1024*10; //10 MB

    protected Uri mMediaUri;


    private DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch(i) {
                case 0:
                    takePhoto();
                    break;
                case 1:
                    Intent getPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getPhotoIntent.setType("image/*");
                    startActivityForResult(getPhotoIntent, GET_PHOTO_REQ);
                    break;
                case 2:
                    takeVideo();
                    break;
                case 3:
                    Intent getVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, getString(R.string.warning_size_video), Toast.LENGTH_LONG).show();
                    startActivityForResult(getVideoIntent, GET_VIDEO_REQ);
                    break;
            }
        }

        private void takeVideo() {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
            if (mMediaUri == null) {
                Toast.makeText(MainActivity.this,
                        getString(R.string.error_external_storage),
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQ);
            }
        }

        private void takePhoto() {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            if (mMediaUri == null) {
                Toast.makeText(MainActivity.this,
                        getString(R.string.error_external_storage),
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQ);
            }
        }

        private Uri getOutputMediaFileUri(int mediaType) {

            if (isExternalStorageAvailable()) {
                String appName = MainActivity.this.getString(R.string.app_name);

                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_PICTURES), appName);

                if (! mediaStorageDir.exists()) {
                    if (! mediaStorageDir.mkdirs()) {
                        return null;
                    }
                }

                File mediaFile;
                Date now = new Date();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                String path = mediaStorageDir.getPath() + File.separator;
                if (mediaType == MEDIA_TYPE_IMAGE) {
                    mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                } else if (mediaType == MEDIA_TYPE_VIDEO) {
                    mediaFile = new File(path + "VID_" + timestamp + ".mp4");
                } else {
                    return null;
                }

                return Uri.fromFile(mediaFile);

            } else {
                return null;
            }

        }

        private boolean isExternalStorageAvailable() {
            String state = Environment.getExternalStorageState();

            if(state.equals(Environment.MEDIA_MOUNTED)) {
                return true;
            } else {
                return false;
            }

        }
    };

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;


    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void CameraOptionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, mDialogListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        ParseAnalytics.trackAppOpened(getIntent());
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser == null) {
            navigateToLogin();
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(mSectionsPagerAdapter.getIcon(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == GET_PHOTO_REQ || requestCode == GET_VIDEO_REQ) {

                if (data == null) {
                    Toast.makeText(this, getString(R.string.error_unexpected), Toast.LENGTH_LONG).show();
                } else {
                    mMediaUri = data.getData();
                }

                if (requestCode == GET_VIDEO_REQ) {
                    int fileSize = 0;

                    InputStream inputStream = null;

                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, getString(R.string.error_file), Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, getString(R.string.error_file), Toast.LENGTH_LONG).show();
                        return;
                    } finally {

                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    if (fileSize >= FILE_SIZE_LIMIT) {
                        Toast.makeText(this, getString(R.string.error_file_size), Toast.LENGTH_LONG).show();
                        return;
                    }
                }

            } else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);

            String fileType;
            if(requestCode == GET_PHOTO_REQ || requestCode == TAKE_PHOTO_REQ) {
                fileType = ParseConstants.TYPE_IMAGE;
            } else {
                fileType = ParseConstants.TYPE_VIDEO;
            }

            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            startActivity(recipientsIntent);

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.error_unexpected), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_add_friend:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera:
                CameraOptionsAlert();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    private void AlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.error_unexpected))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
