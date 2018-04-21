package com.zeach.ofirmonis.zeach.Fragments;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.interfaces.FriendsListener;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class FriendsFragment extends Fragment implements View.OnClickListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final String TAG = FriendsFragment.class.getSimpleName();


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private android.support.v4.app.FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) rootView.findViewById(R.id.friends_container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mViewPager.beginFakeDrag(); //one solution for disable swipe
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.friends_tabs);
        tabLayout.setupWithViewPager(mViewPager);


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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    FriendsListFragment friendsList = new FriendsListFragment();
                    return friendsList;
                case 1:
                    SearchUsersListFragment searchUsersListFragment = new SearchUsersListFragment();

                    return searchUsersListFragment;
                case 2:
                    FriendsRequestsListFragment friendsRequestsListFragment = new FriendsRequestsListFragment();
                    return friendsRequestsListFragment;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return 3;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Friends";
                case 1:
                    return "Search Friends";
                case 2:
                    return "Friends Requests";
            }
            return null;
        }
    }


    @Override
    public void onClick(View v) {

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
        Log.d(TAG, "Fragment detached");
        super.onDetach();
    }
}
