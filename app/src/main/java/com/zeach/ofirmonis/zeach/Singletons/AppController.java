package com.zeach.ofirmonis.zeach.Singletons;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.zeach.ofirmonis.zeach.Constants.FirebaseConstants;
import com.zeach.ofirmonis.zeach.Objects.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by ofirmonis on 18/07/2017.
 */

public class AppController {
    private static AppController mInstance;

    public static JSONArray arr;

    public com.zeach.ofirmonis.zeach.Objects.User User;

    protected AppController() {
    }

    public com.zeach.ofirmonis.zeach.Objects.User getUser() {
        return User;
    }

    public void setUser(com.zeach.ofirmonis.zeach.Objects.User user) {
        User = user;
    }

    public static void createInstance() {
        mInstance = new AppController();
    }

    public static AppController getInstance() {
        if (null == mInstance) {
            mInstance = new AppController();
        }
        return mInstance;
    }

    public void getFacebookFriends() {
        //get friends list
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + this.User.getFacebookUID() + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject json = response.getJSONObject();
                        try {
                            JSONArray data1 = json.getJSONArray("data");
                            arr = data1;
                            addFacebookFriends(data1);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();


    }

    public void addFacebookFriends(JSONArray data1) {
        final DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        DatabaseReference searchUserId = data.getDatabase().getReference();
        for (int i = 0; i < data1.length(); i++) {
            Query UserId = null;
            try {
                UserId = searchUserId.child(FirebaseConstants.USERS).orderByChild(FirebaseConstants.FACEBOOK_UID).equalTo(data1.getJSONObject(i).getString("id"));
                UserId.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("found", dataSnapshot.toString());
                        com.zeach.ofirmonis.zeach.Objects.User desired = dataSnapshot.getValue(com.zeach.ofirmonis.zeach.Objects.User.class);
                        Friend f = new Friend(desired.getName(), desired.getUID(), desired.getProfilePictureUri());
                        data.child(String.format("%s/", FirebaseConstants.USERS)).child(getUser().getUID()).child(String.format("/%s", FirebaseConstants.FRIENDS_LIST)).child(desired.getUID()).setValue(f);
                        User.AddFriendToList(desired.getUID(), desired.getName(), desired.getProfilePictureUri());


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView = null;

        public DownloadImageTask(ImageView friendPhoto) {
            this.imageView = friendPhoto;
        }


        @SuppressWarnings("WrongThread")
        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }


    }


    public static Bitmap SetCircleMarkerIcon(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float) (width / 2)
                , (float) (height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    public static Bitmap addBorderToCircularBitmap(Bitmap srcBitmap, int borderWidth, int borderColor) {
        // Calculate the circular bitmap width with border
        int dstBitmapWidth = srcBitmap.getWidth() + borderWidth * 2;

        // Initialize a new Bitmap to make it bordered circular bitmap
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth, dstBitmapWidth, Bitmap.Config.ARGB_8888);

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(dstBitmap);
        // Draw source bitmap to canvas
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);

        // Initialize a new Paint instance to draw border
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);

        /*
            public void drawCircle (float cx, float cy, float radius, Paint paint)
                Draw the specified circle using the specified paint. If radius is <= 0, then nothing
                will be drawn. The circle will be filled or framed based on the Style in the paint.

            Parameters
                cx : The x-coordinate of the center of the cirle to be drawn
                cy : The y-coordinate of the center of the cirle to be drawn
                radius : The radius of the cirle to be drawn
                paint : The paint used to draw the circle
        */
        // Draw the circular border around circular bitmap
        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getWidth() / 2, // cy
                canvas.getWidth() / 2 - borderWidth / 2, // Radius
                paint // Paint
        );

        // Free the native object associated with this bitmap.
        srcBitmap.recycle();

        // Return the bordered circular bitmap
        return dstBitmap;
    }

    public static Bitmap addShadowToCircularBitmap(Bitmap srcBitmap, int shadowWidth, int shadowColor) {

        // Calculate the circular bitmap width with shadow
        int dstBitmapWidth = srcBitmap.getWidth() + shadowWidth * 2;
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth, dstBitmapWidth, Bitmap.Config.ARGB_8888);

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawBitmap(srcBitmap, shadowWidth, shadowWidth, null);

        // Paint to draw circular bitmap shadow
        Paint paint = new Paint();
        paint.setColor(shadowColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(shadowWidth);
        paint.setAntiAlias(true);

        // Draw the shadow around circular bitmap
        canvas.drawCircle(
                dstBitmapWidth / 2, // cx
                dstBitmapWidth / 2, // cy
                dstBitmapWidth / 2 - shadowWidth / 2, // Radius
                paint // Paint
        );

        srcBitmap.recycle();

        // Return the circular bitmap with shadow
        return dstBitmap;
    }

}
