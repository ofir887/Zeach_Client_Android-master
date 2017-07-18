package com.zeach.ofirmonis.zeach;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static com.zeach.ofirmonis.zeach.R.layout.content_main;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Button LogoutButton;
    private FirebaseAuth mAuth;
   // private DatabaseReference data;
    //private UserNew ZeachUser;
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new MapFragment());
        this.mAuth = FirebaseAuth.getInstance();
      //  this.data = FirebaseDatabase.getInstance().getReference();
        this.spinner = (ProgressBar)findViewById(R.id.progress_bar);
        this.spinner.setVisibility(View.GONE);
      //  this.ZeachUser = AppSavedObjects.getInstance().getUser();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,new MapFragment()).commit();


        mAuth = FirebaseAuth.getInstance();
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

        } else if (id == R.id.profile) {
                Log.d("fragment", "favorite fragment pressed");
                fragmentManager.beginTransaction().replace(R.id.content_frame,new ProfileFragment()).commit();

        } else if (id == R.id.feedback) {

        } else if (id == R.id.nav_send) {
//4564
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
