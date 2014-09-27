package com.example.kevin.ribbit;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Kevin on 9/13/2014.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "okogqsYV6QQBCfrRL5AoXhIDyf2oF3cETc27NKXt", "tBNDXSecUxoZYUmGI36vqZsybiJxZGqMnmXhiYRo");
    }

}
