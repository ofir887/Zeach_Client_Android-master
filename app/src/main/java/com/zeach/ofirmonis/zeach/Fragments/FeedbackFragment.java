package com.zeach.ofirmonis.zeach.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;

import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Singletons.AppController;
import com.zeach.ofirmonis.zeach.Objects.User;


/**
 * Created by ofirmonis on 24/02/2018.
 */

public class FeedbackFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = FeedbackFragment.class.getSimpleName();
    private CheckBox mAccurateCheckbox;
    private CheckBox mFriendlyCheckbox;
    private RatingBar mRatingBar;
    private User mUser;
    private Button mFinishButton;
    private static final String ACTION_UPDATE_USER_FEEDBACK = "update_user_feedback";

    private BroadcastReceiver mFeedbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        mUser = AppController.getInstance().getUser();
        mFinishButton = rootView.findViewById(R.id.finish_button);
        mFinishButton.setOnClickListener(this);
        mAccurateCheckbox = rootView.findViewById(R.id.accurate_checkbox);
        mFriendlyCheckbox = rootView.findViewById(R.id.friendly_checkbox);
        mRatingBar = rootView.findViewById(R.id.ratingBar);
        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onClick(View v) {
        if (v == mFinishButton) {
            Log.i(TAG, "clicked");
            sendBroadcast();
        }


    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_USER_FEEDBACK);
        intent.putExtra("is_accurate", mAccurateCheckbox.isChecked());
        intent.putExtra("easy_to_use", mFriendlyCheckbox.isChecked());
        intent.putExtra("rating", mRatingBar.getRating());
        getContext().sendBroadcast(intent);
        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder feedbackDialog = new AlertDialog.Builder(getContext());
        feedbackDialog.setTitle("Feedback sent !");
        feedbackDialog.setMessage("Thank you for answering !");
        feedbackDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
                onDestroy();
            }
        });
        feedbackDialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDetach() {
        Log.d(TAG, "Screen detach");
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFeedbackReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_USER_FEEDBACK);
            getContext().registerReceiver(mFeedbackReceiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        getContext().unregisterReceiver(mFeedbackReceiver);
        super.onPause();
    }
}
