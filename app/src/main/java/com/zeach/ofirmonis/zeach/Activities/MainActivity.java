package com.zeach.ofirmonis.zeach.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.Fragments.FeedbackFragment;
import com.zeach.ofirmonis.zeach.Singletons.AppController;
import com.zeach.ofirmonis.zeach.Fragments.FavoriteBeachesFragment;
import com.zeach.ofirmonis.zeach.Fragments.FriendsFragment;
import com.zeach.ofirmonis.zeach.Fragments.ProfileFragment;
import com.zeach.ofirmonis.zeach.Fragments.MapFragment;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.Services.BackgroundService;
import com.zeach.ofirmonis.zeach.Singletons.MapSingleton;


import de.hdodenhof.circleimageview.CircleImageView;

import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_STRING_ACTIVITY;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_USER;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.PERSON_ICON;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private User zeachUser;
    private View header;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private boolean mOpenMap;


    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USER: {
                    Log.d(TAG, "User message received");
                    User user = (User) intent.getSerializableExtra(IntentExtras.USER);
                    zeachUser = user;
                    Log.d(TAG, user.toString());
                    setDrawerProfileInforamtion();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isActive", true).commit();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isLoggedIn", true).commit();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        Intent backgroundService = new Intent(this, BackgroundService.class);
        startService(backgroundService);
        //
        if (activityReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            intentFilter.addAction(ACTION_USER);
            registerReceiver(activityReceiver, intentFilter);
        }

        mOpenMap = getIntent().getBooleanExtra(IntentExtras.MAP, false);
        Log.i(TAG, String.format("opening map:[%b]", mOpenMap));
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mOpenMap) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void setDrawerProfileInforamtion() {
        TextView navigationName = (TextView) header.findViewById(R.id.profileName);
        navigationName.setText(zeachUser.getName());
        final CircleImageView image = (CircleImageView) header.findViewById(R.id.imageViewP);
        if (zeachUser.getProfilePictureUri().startsWith("https://")) {
            mStorageRef = mStorage.getReference(PERSON_ICON);
        } else {
            mStorageRef = mStorage.getReference(zeachUser.getProfilePictureUri());
        }
        mStorageRef.getBytes(512 * 512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = AppController.SetCircleMarkerIcon(bitmap);
                bitmap = AppController.addBorderToCircularBitmap(bitmap, 5, Color.BLACK);
                bitmap = AppController.addShadowToCircularBitmap(bitmap, 4, Color.LTGRAY);
                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                image.setImageBitmap(smallMarker);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent();
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(activityReceiver);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isActive", false);
        stopService(new Intent(this, BackgroundService.class));
        Log.i(TAG, "on destroy");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i(TAG, "logout");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            for (UserInfo profile : user.getProviderData()) {

                Log.i(TAG, "Provider: " + profile.getProviderId());
            }

            FirebaseAuth.getInstance().signOut();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isLoggedIn", false).commit();
            Intent backgroundService = new Intent(this, BackgroundService.class);
            stopService(backgroundService);
            finish();
            LoginManager.getInstance().logOut();
            MapSingleton.createInstance();
            AppController.createInstance();
            Intent SignUpLogInActivity = new Intent(getApplicationContext(), SignUpLogInActivity.class);
            startActivity(SignUpLogInActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        MapFragment map = new MapFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.map) {
            Log.i(TAG, "fragment pressed");
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
            // Handle the camera action
        } else if (id == R.id.favorite_beaches) {
            Log.i(TAG, "favorite fragment pressed");
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FavoriteBeachesFragment()).commit();
        } else if (id == R.id.friends) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FriendsFragment()).commit();

        } else if (id == R.id.profile) {
            Log.i(TAG, "favorite fragment pressed");
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();

        } else if (id == R.id.feedback) {
            Log.i(TAG, "feedback fragment pressed");
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FeedbackFragment()).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
