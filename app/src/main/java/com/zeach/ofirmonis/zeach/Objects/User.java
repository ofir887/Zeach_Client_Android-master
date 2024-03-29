package com.zeach.ofirmonis.zeach.Objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ofirmonis on 24/06/2017.
 */

public class User implements Serializable {
    private String Name;
    private String Email;
    private String UID;
    private String FacebookUID;
    private boolean importFacebookFriends = true;
    private String Provider;
    private String ProfilePictureUri;
    private Map<String, Friend> FriendsList = new HashMap<>();
    private Map<String, FavoriteBeach> FavoriteBeaches = new HashMap<>();
    private UserAtBeach CurrentBeach;
    private boolean isProfilePrivate;


    public User() {
        //
    }

    //For facebook signup
    public User(String name, String email, String UID, String provider, String profilePictureUri, String facebookUID) {
        this.Name = name;
        this.Email = email;
        this.UID = UID;
        this.Provider = provider;
        this.ProfilePictureUri = profilePictureUri;
        this.FriendsList = new HashMap<>();
        this.FavoriteBeaches = new HashMap<>();
        //  this.CurrentBeach = "Not In any Beach"; // need to change
        this.CurrentBeach = new UserAtBeach();
        this.FacebookUID = facebookUID;
        // this.CurrentBeach1 = new CurrentBeach();
        this.importFacebookFriends = true;
    }

    //For Firebase SignUp
    public User(String email, String UID, String provider) {
        this.Email = email;
        this.UID = UID;
        this.Provider = provider;
        this.ProfilePictureUri = null;
        this.Name = "";
        this.FriendsList = new HashMap<>();
        this.FavoriteBeaches = new HashMap<>();
        this.CurrentBeach = new UserAtBeach();
        //  this.CurrentBeach = "Not In any Beach"; // need to change
        // this.CurrentBeach1 = new CurrentBeach();
        this.FacebookUID = null;
        this.importFacebookFriends = false;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getProvider() {
        return Provider;
    }

    public void setProvider(String provider) {
        Provider = provider;
    }

    public String getProfilePictureUri() {
        return ProfilePictureUri;
    }

    public void setProfilePictureUri(String profilePictureUri) {
        ProfilePictureUri = profilePictureUri;
    }

    //not good need to think about child in has map it is in the same key!!
    public void AddFriendToList(String uid, String name, String photoUrl) {
        Friend friend = new Friend(name, uid, photoUrl);
        this.FriendsList.put(uid, friend);
        //this.FriendsList.put("FriendName",name);
    }

    public Map<String, Friend> getFriendsList() {
        return FriendsList;
    }

    public void setFriendsList(Map<String, Friend> friendsList) {
        FriendsList = friendsList;
    }

    public void AddBeachToList(FavoriteBeach aFavoriteBeach) {
        this.FavoriteBeaches.put(aFavoriteBeach.getmBeachKey(), aFavoriteBeach);

    }

    public Map<String, FavoriteBeach> getFavoriteBeaches() {
        return FavoriteBeaches;
    }

    public void setFavoriteBeaches(Map<String, FavoriteBeach> favoriteBeachesList) {
        FavoriteBeaches = favoriteBeachesList;
    }

    public UserAtBeach getCurrentBeach() {
        return CurrentBeach;
    }

    public void setCurrentBeach(UserAtBeach currentBeach) {
        CurrentBeach = currentBeach;
    }

    public String getFacebookUID() {
        return FacebookUID;
    }

    public void setFacebookUID(String facebookUID) {
        FacebookUID = facebookUID;
    }
    /*
    public String getCurrentBeach1(){
        return this.CurrentBeach1.getBeachName();
    }*/

    public boolean isProfilePrivate() {
        return isProfilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        isProfilePrivate = profilePrivate;
    }


    public boolean isImportFacebookFriends() {
        return importFacebookFriends;
    }

    public void setImportFacebookFriends(boolean importFacebookFriends) {
        this.importFacebookFriends = importFacebookFriends;
    }


    @Override
    public String toString() {
        return "ZeachUser{" +
                "Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                ", UID='" + UID + '\'' +
                ", FacebookUID='" + FacebookUID + '\'' +
                ", importFacebookFriends=" + importFacebookFriends +
                ", Provider='" + Provider + '\'' +
                ", ProfilePictureUri='" + ProfilePictureUri + '\'' +
                ", FriendsList=" + FriendsList +
                ", FavoriteBeaches=" + FavoriteBeaches +
                ", CurrentBeach='" + CurrentBeach + '\'' +
                ", isProfilePrivate=" + isProfilePrivate +

                '}';
    }
}



