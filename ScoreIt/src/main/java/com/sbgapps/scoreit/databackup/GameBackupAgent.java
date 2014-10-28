package com.sbgapps.scoreit.databackup;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

/**
 * Created by sbaiget on 27/10/2014.
 */
public class GameBackupAgent extends BackupAgent {

    static final String FILES_BACKUP_KEY = "SAVED_GAMES";

    @Override
    public void onBackup(ParcelFileDescriptor parcelFileDescriptor,
                         BackupDataOutput backupDataOutput,
                         ParcelFileDescriptor parcelFileDescriptor2) throws IOException {

    }

    @Override
    public void onRestore(BackupDataInput backupDataInput, int i,
                          ParcelFileDescriptor parcelFileDescriptor) throws IOException {

    }
}
