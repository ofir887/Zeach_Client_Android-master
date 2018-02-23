package com.zeach.ofirmonis.zeach.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    Button btn;
    private EditText Name;
    private EditText Gender;
    private Button getFromGallery;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_OK = -1;
    private ImageView image;
    private User ZeachUser;
    private View rootView;
    private Bitmap bit = null;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private static final String ACTION_UPDATE_USER_PROFILE = "update_user_profile";
    private BroadcastReceiver mProfileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        this.ZeachUser = AppController.getInstance().getUser();
        this.btn = (Button) this.rootView.findViewById(R.id.button3);
        this.Name = (EditText) this.rootView.findViewById(R.id.name_field);
        this.getFromGallery = (Button) this.rootView.findViewById(R.id.browse);
        this.image = (ImageView) this.rootView.findViewById(R.id.imageView2);
        this.getFromGallery.setOnClickListener(this);
        this.btn.setOnClickListener(this);
        setElements();


        return this.rootView;
    }

    public void setElements() {
        if (this.ZeachUser.getName().length() > 0) {
            this.Name.setText(this.ZeachUser.getName());
            this.Name.setOnKeyListener(null);
        }
        if (this.ZeachUser.getProfilePictureUri() != null) {
            if (this.ZeachUser.getFacebookUID() != null && !this.ZeachUser.getProfilePictureUri().startsWith("/Users/")) {
                new AppController.DownloadImageTask(this.image).execute(this.ZeachUser.getProfilePictureUri().toString());
                AlertDialog.Builder uploadPhotoDialog = new AlertDialog.Builder(getContext());
                uploadPhotoDialog.setTitle("Upload Alert");
                uploadPhotoDialog.setMessage("Upload Facebook photo ?");
                uploadPhotoDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addImageToStorage();
                        dialog.cancel();
                    }
                });
                uploadPhotoDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                uploadPhotoDialog.show();
            } else {
                Log.i(TAG, "Loading photo from storage");
                mStorageRef = mStorage.getReference(this.ZeachUser.getProfilePictureUri());
                mStorageRef.getBytes(4096 * 4096).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bitmap = AppController.SetCircleMarkerIcon(bitmap);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                        image.setImageBitmap(smallMarker);
                        image.invalidate();
                    }
                });
            }
        } else {
            Log.i(TAG, "User not from facebook. showing default icon image");
            String profilePictureUri = "/PersonIcon.png";
            this.ZeachUser.setProfilePictureUri(profilePictureUri);
            mStorageRef = mStorage.getReference(profilePictureUri);
            mStorageRef.getBytes(4096 * 4096).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bitmap = AppController.SetCircleMarkerIcon(bitmap);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    image.setImageBitmap(smallMarker);
                    image.invalidate();
                }
            });
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            if (requestCode == 1) {
                Uri returnUri = data.getData();
                Log.i(TAG, String.format("file path:[%s]", returnUri.toString()));
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.image.setImageBitmap(bitmapImage);
                addImageToStorage();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btn) {
            this.ZeachUser.setName(this.Name.getText().toString()); // set the updated name
            //    AppController.getInstance().setUser(this.ZeachUser);
            //TODO - send broadcast
            sendBroadcast();
            if (getActivity().getClass().getSimpleName().equals("MainActivity")) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PreferencesFragment()).commit();
                onDestroy();
            }

        }
        if (v == getFromGallery) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_USER_PROFILE);
        intent.putExtra("name", ZeachUser.getName());
        intent.putExtra("photo_url", ZeachUser.getProfilePictureUri());
        getContext().sendBroadcast(intent);
    }

    public void addImageToStorage() {
        String imagePath = "/Users/" + ZeachUser.getUID();
        ZeachUser.setProfilePictureUri(imagePath);
        mStorageRef = mStorage.getReference().child(imagePath);
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bmp = image.getDrawingCache();
        bmp = AppController.SetCircleMarkerIcon(bmp);
        Bitmap smallMarker = Bitmap.createScaledBitmap(bmp, 200, 200, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        smallMarker.compress(Bitmap.CompressFormat.PNG, 0, out);
        byte[] data = out.toByteArray();
        UploadTask uploadTask = mStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i(TAG, "Failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "Success");
                sendBroadcast();
                AlertDialog.Builder uploadPhotoDialog = new AlertDialog.Builder(getContext());
                uploadPhotoDialog.setTitle("Upload Process Completed");
                uploadPhotoDialog.setMessage("Success");
                uploadPhotoDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                uploadPhotoDialog.show();
            }
        });
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
        Log.d("nir", "nir1222");

        AppController.getInstance().setUser(this.ZeachUser);
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProfileReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_USER_PROFILE);
            getContext().registerReceiver(mProfileReceiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        getContext().unregisterReceiver(mProfileReceiver);
        super.onPause();
    }
}
