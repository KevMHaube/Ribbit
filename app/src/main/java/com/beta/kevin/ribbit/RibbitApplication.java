package com.beta.kevin.ribbit;

import android.app.Application;

import com.beta.kevin.ribbit.ui.MainActivity;
import com.beta.kevin.ribbit.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Created by Kevin on 9/13/2014.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this,
                "okogqsYV6QQBCfrRL5AoXhIDyf2oF3cETc27NKXt",
                "tBNDXSecUxoZYUmGI36vqZsybiJxZGqMnmXhiYRo"
        );

        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

}
