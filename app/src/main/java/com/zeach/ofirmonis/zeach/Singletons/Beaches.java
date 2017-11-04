package com.zeach.ofirmonis.zeach.Singletons;

import com.zeach.ofirmonis.zeach.Objects.Beach;

import java.util.ArrayList;

/**
 * Created by ofirmonis on 04/10/2017.
 */

public class Beaches {

    private static Beaches mInstance = null;
    private ArrayList<Beach> mBeaches;

    protected Beaches() {
        this.mBeaches = new ArrayList<>();
    }

    public void createInstance() {
        mInstance = new Beaches();

    }

    public Beaches getInstance() {
        return mInstance;
    }

    public boolean addBeach(Beach beach) {
        if (beach != null) {
            this.mBeaches.add(beach);
            return true;
        }
        return false;
    }

    public Beach getBeachByIndex(int i) {
        return mBeaches.get(i);
    }

}
