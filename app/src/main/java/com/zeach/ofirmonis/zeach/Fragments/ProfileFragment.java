package com.zeach.ofirmonis.zeach.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Activities.ProfileActivity;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;

import java.io.IOException;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener{
    Button btn;
    private EditText Name;
    private EditText Gender;
    private Button getFromGallery;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_OK = -1;
    private ImageView image;
    private User ZeachUser;
    private View rootView;
    private Bitmap bit=null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView  =inflater.inflate(R.layout.fragment_profile,container,false);
        this.ZeachUser = AppController.getInstance().getUser();
        this.btn = (Button)this.rootView.findViewById(R.id.button3);
        this.Name = (EditText)this.rootView.findViewById(R.id.name_field);
        this.Gender = (EditText)this.rootView.findViewById(R.id.gender_field);
        this.getFromGallery = (Button)this.rootView.findViewById(R.id.browse);
        this.image = (ImageView)this.rootView.findViewById(R.id.imageView2);
        this.getFromGallery.setOnClickListener(this);
        this.btn.setOnClickListener(this);
        setElements();



        return this.rootView;
    }
    public void setElements(){
        if (this.ZeachUser.getName().length() > 0){
            this.Name.setText(this.ZeachUser.getName());
        }
        if (this.ZeachUser.getProfilePictureUri() !=null){
            new AppController.DownloadImageTask(this.image).execute(this.ZeachUser.getProfilePictureUri().toString());
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1) {
            if (requestCode == 1) {
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.image.setImageBitmap(bitmapImage);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btn){
            this.ZeachUser.setName(this.Name.getText().toString()); // set the updated name
            AppController.getInstance().setUser(this.ZeachUser);
            if (getActivity().getClass().getSimpleName().equals("MainActivity")) {
                //updateUserFromFragment();
               // AppController.getInstance().setUser(this.ZeachUser);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PreferencesFragment()).commit();
            }
            else if ((getActivity().getClass().getSimpleName().equals("ProfileActivity")))
                ((ProfileActivity)getActivity()).setCurrentItem(1);
           // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new PreferencesFragment()).commit();

            //  FragmentManager fragmentManager = getFragmentManager();
          //  fragmentManager.beginTransaction().replace(R.id.content_frame,new PreferencesFragment()).commit();
        }
        if (v == getFromGallery){
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }


    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onDetach() {
        Log.d("nir","nir1222");

        AppController.getInstance().setUser(this.ZeachUser);
        super.onDetach();
    }
}
