package br.com.maracujasoftware.ohayoo_beautifulsmiles;

import android.app.Application;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/**
 * Created by julio on 04/09/2016.
 */
public class MainApplication extends Application implements IAdobeAuthClientCredentials {

    private static final String CREATIVE_SDK_CLIENT_SECRET = "bfb54879-fd3e-4f2a-8055-2513db89b913";
    private static final String CREATIVE_SDK_CLIENT_ID = "2ec41c0f02ab4bb497187b5377e496bd";

    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }
}
