package com.ets.astl.pfe_velo_jet.task;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.ets.astl.pfe_velo_jet.entity.GlobalData;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileTask extends AsyncTask<String, Void, Drawable> {

    GlobalData globalData;

    public ProfileTask(GlobalData global) {
        globalData = global;
    }

    @Override
    protected Drawable doInBackground(String... params) {
        try {
            InputStream stream = (InputStream) new URL(params[0]).getContent();
            Drawable drawable = Drawable.createFromStream(stream, "Photo");
            globalData.setProfileImage(drawable);
            return drawable;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
