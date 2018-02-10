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

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Fragments.FavoriteBeachesFragment;
import com.zeach.ofirmonis.zeach.Fragments.FriendsFragment;
import com.zeach.ofirmonis.zeach.Fragments.ProfileFragment;
import com.zeach.ofirmonis.zeach.Fragments.MapFragment;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.Services.BackgroundService;

import java.lang.reflect.Type;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private User zeachUser;
    private static final String ACTION_USER = "User";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private ProgressBar spinner;
    private View header;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USER: {
                    Log.d(TAG, "lets see");
                    User user = (User) intent.getSerializableExtra("User");
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
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        this.spinner = (ProgressBar) findViewById(R.id.progress_bar);
        Intent backgroundService = new Intent(this, BackgroundService.class);
        startService(backgroundService);
        //
        if (activityReceiver != null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            intentFilter.addAction(ACTION_USER);
            //Map the intent filter to the receiver
            registerReceiver(activityReceiver, intentFilter);
        }

        //
        // this.spinner.setVisibility(View.VISIBLE);
        //  getUser();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
        //mAuth = FirebaseAuth.getInstance();
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
        mStorageRef = mStorage.getReference(zeachUser.getProfilePictureUri());
        //Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(mStorageRef).into(image);
        mStorageRef.getBytes(4096 * 4096).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = AppController.SetCircleMarkerIcon(bitmap);
                bitmap = AppController.addBorderToCircularBitmap(bitmap, 5, Color.BLACK);
                bitmap = AppController.addShadowToCircularBitmap(bitmap, 4, Color.LTGRAY);
                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                image.setImageBitmap(smallMarker);
            }
        });
        //  Bitmap smallMarker = Bitmap.createScaledBitmap(aBitmap, 200, 200, true);
        //  image.setImageBitmap(smallMarker);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(activityReceiver);
        stopService(new Intent(this, BackgroundService.class));
        Log.d(TAG, "on destroy");
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
            Log.d("logout", "logout");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            for (UserInfo profile : user.getProviderData()) {

                Log.d("Provider", "Provider: " + profile.getProviderId());
            }

            // Log.d("log" , mAuth.getCurrentUser().getProviderId());
            FirebaseAuth.getInstance().signOut();
            finish();
            LoginManager.getInstance().logOut();

            Intent SignInLogInActivity = new Intent(getApplicationContext(), SignInLogInActivity.class);
            startActivity(SignInLogInActivity);
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
            Log.d("fragment", "fragment pressed");
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();

            // Handle the camera action
        } else if (id == R.id.favorite_beaches) {
            Log.d("fragment", "favorite fragment pressed");

            fragmentManager.beginTransaction().replace(R.id.content_frame, new FavoriteBeachesFragment()).commit();

        } else if (id == R.id.friends) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FriendsFragment()).commit();

        } else if (id == R.id.profile) {
            Log.d("fragment", "favorite fragment pressed");
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();

        } else if (id == R.id.feedback) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*
    public void UpdateUserInfo(){
       // this.data = FirebaseDatabase.getInstance().getReference();
        AppController.getInstance().setUser(this.ZeachUser);
        Log.d("singleton",AppController.getInstance().getUser().toString());
        Map<String,ZeachUser> user = new HashMap<String,ZeachUser>();
        user.put(this.ZeachUser.getUID(),this.ZeachUser);
        this.data.child("Users").child(this.ZeachUser.getUID()).setValue(this.ZeachUser);
        //Add seperate parent ! need to check if this is good or can out this on Users in nested map
        //data.child("Users").child(this.User.getUID()).child("Friends").push().child("ofir");
        /*
        Intent profileActivity = new Intent(getActivity(),ProfileActivity.class);
        profileActivity.putExtra("User",User);
        getActivity().finish();
        startActivity(profileActivity);

    }*/

}
