package com.nafeo.www.barivara;

import android.app.Application;
import com.firebase.client.Firebase;

/**
 * Created by Friends on 5/25/2017.
 */

public class Barivara extends Application {

    public void onCreate(){
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
