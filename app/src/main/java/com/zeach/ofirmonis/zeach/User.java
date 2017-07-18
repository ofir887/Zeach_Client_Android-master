package com.zeach.ofirmonis.zeach;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by ofirmonis on 17/06/2017.
 */

public class User implements Serializable{
    private String Name;
    private String Gender;
    private String UID;
    private long FacebookUID;
    private String Email;
    private String Provider;
    private String CurrentBeach;
    private HashMap<String,Friend> FriendsList = new HashMap<>();
    //private ArrayList<User> Friends = new ArrayList<>();
    private Map<String,User>  Friends = new HashMap<>();



    public User(String name, String gender, String UID, long facebookUID, String email, String provider) {
        Name = name;
        Gender = gender;
        this.UID = UID;
        FacebookUID = facebookUID;
        Email = email;
        Provider = provider;
    }
    public User(){

    }
    public HashMap<String,Friend> getFriends(){
        return this.FriendsList;
    }
    public void AddFriend(Friend friend){

        this.FriendsList.put(friend.getUID(),friend);


    }
    public User(String name, String gender, String uid, String provider, String email) {

        this.Name = name;
        this.Gender = gender;
        this.UID = uid;
        this.Provider = provider;
        this.Email = email;


    }
    public User(String email, String uid, String provider) {

        this.Email = email;
        this.Provider = provider;
        this.UID = uid;

    }

    public User(String name, String gender, String UID, long facebookUID, String email) {
        Name = name;
        Gender = gender;
        this.UID = UID;
        FacebookUID = facebookUID;
        Email = email;
    }

    public long getFacebookUID() {
        return FacebookUID;
    }

    public void setFacebookUID(long facebookUID) {
        FacebookUID = facebookUID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }




    public void setName(String name) {
        Name = name;
    }

    public void setGender(String gender) {
        Gender = gender;
    }


    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {

        return Name;
    }

    public String getGender() {
        return Gender;
    }

    public String getUID() {
        return UID;
    }

    @Override
    public String toString() {
        return "User{" +
                "Name='" + Name + '\'' +
                ", Gender='" + Gender + '\'' +
                ", UID='" + UID + '\'' +
                ", FacebookUID=" + FacebookUID +
                ", Email='" + Email + '\'' +
                ", Provider='" + Provider + '\'' +
                ", Friends=" + Friends +

                '}';
    }
}
