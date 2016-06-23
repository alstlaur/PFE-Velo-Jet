package com.ets.astl.pfe_velo_jet.managers;

public class BackupManager {

    private static BackupManager Instance = null;

    private BackupManager() {

    }

    public static BackupManager getInstance() {
        if (Instance == null) {
            Instance = new BackupManager();
        }
        return Instance;
    }

    public int getBackup() {
        //Retrieve the JSON file from the remote API

        return 0;
    }

    public int sendBackup() {
        //Send the JSON file to the remote API

        return 0;
    }
}
