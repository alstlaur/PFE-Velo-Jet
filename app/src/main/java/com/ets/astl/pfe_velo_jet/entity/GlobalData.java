package com.ets.astl.pfe_velo_jet.entity;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

public class GlobalData extends Application {

    private GoogleApiClient client;
    private GoogleSignInAccount account;

    private Drawable profileImage = null;

    public GoogleApiClient getClient() {
        return client;
    }

    public void setClient(GoogleApiClient client) {
        this.client = client;
    }

    public GoogleSignInAccount getAccount() {
        return account;
    }

    public void setAccount(GoogleSignInAccount account) {
        this.account = account;
    }

    public Drawable getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Drawable profileImage) {
        this.profileImage = profileImage;
    }
}
