package com.zeach.ofirmonis.zeach.Activities;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Fragments.FavoriteBeachesFragment;
import com.zeach.ofirmonis.zeach.Fragments.FriendsFragment;
import com.zeach.ofirmonis.zeach.Fragments.ProfileFragment;
import com.zeach.ofirmonis.zeach.Fragments.MapFragment;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Objects.ZeachUser;
import com.zeach.ofirmonis.zeach.Services.BackgroundService;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ZeachUser zeachUser;

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.spinner = (ProgressBar) findViewById(R.id.progress_bar);
        Intent backgroundService = new Intent(this, BackgroundService.class);
        startService(backgroundService);
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
        View header = navigationView.getHeaderView(0);
        TextView navigationName = (TextView) header.findViewById(R.id.profileName);
        //  String name = AppController.getInstance().getUser().getName();
        //   navigationName.setText(this.ZeachUser.getName());
        navigationView.setNavigationItemSelectedListener(this);
        String user = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("user", "");
        try {
            JSONObject jsn = new JSONObject(user);
            navigationName.setText(jsn.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        CircleImageView image = (CircleImageView) header.findViewById(R.id.imageViewP);
        new AppController.DownloadImageTask(image).execute("https://graph.facebook.com/10209101466959698/picture?height=200&width=200&migration_overrides=%7Boctober_2012%3Atrue%7D");

    }

    public void setNameAtDrawer(View view) {
        //  TextView navigationName = (TextView).findViewById(R.id.userName);
        //  navigationName.setText(AppController.getInstance().getUser().getName());
    }

    public void getUser() {
        //this.zeachUser = AppController.getInstance().getUser();
        while (this.zeachUser == null) {
            this.zeachUser = AppController.getInstance().getUser();
            this.spinner.setVisibility(View.VISIBLE);
        }
        this.spinner.setVisibility(View.GONE);

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, BackgroundService.class));
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
