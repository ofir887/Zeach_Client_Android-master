package com.zeach.ofirmonis.zeach;

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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private UserNew zeachUser;

    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.spinner = (ProgressBar)findViewById(R.id.progress_bar);
       // this.spinner.setVisibility(View.VISIBLE);
      //  getUser();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,new MapFragment()).commit();
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
        TextView navigationName = (TextView)header.findViewById(R.id.userName);
      //  String name = AppSavedObjects.getInstance().getUser().getName();
     //   navigationName.setText(this.ZeachUser.getName());
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void setNameAtDrawer(View view){
      //  TextView navigationName = (TextView).findViewById(R.id.userName);
      //  navigationName.setText(AppSavedObjects.getInstance().getUser().getName());
    }
    public void getUser(){
        //this.zeachUser = AppSavedObjects.getInstance().getUser();
        while (this.zeachUser == null){
            this.zeachUser = AppSavedObjects.getInstance().getUser();
            this.spinner.setVisibility(View.VISIBLE);
        }
        this.spinner.setVisibility(View.GONE);

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
            Log.d("logout","logout");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            for (UserInfo profile : user.getProviderData()) {

                Log.d("Provider", "Provider: " + profile.getProviderId());
            }

            // Log.d("log" , mAuth.getCurrentUser().getProviderId());
            FirebaseAuth.getInstance().signOut();
            finish();
            LoginManager.getInstance().logOut();

            Intent SignInLogInActivity = new Intent(getApplicationContext(),SignInLogInActivity.class);
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
            fragmentManager.beginTransaction().replace(R.id.content_frame,new MapFragment()).commit();

            // Handle the camera action
       } else
            if (id == R.id.favorite_beaches) {
            Log.d("fragment", "favorite fragment pressed");
            fragmentManager.beginTransaction().replace(R.id.content_frame,new ProfileFragment()).commit();

        } else if (id == R.id.friends) {
                fragmentManager.beginTransaction().replace(R.id.content_frame,new FriendsFragment()).commit();

        } else if (id == R.id.profile) {
                Log.d("fragment", "favorite fragment pressed");
                fragmentManager.beginTransaction().replace(R.id.content_frame,new ProfileFragment()).commit();

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
        AppSavedObjects.getInstance().setUser(this.ZeachUser);
        Log.d("singleton",AppSavedObjects.getInstance().getUser().toString());
        Map<String,UserNew> user = new HashMap<String,UserNew>();
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
