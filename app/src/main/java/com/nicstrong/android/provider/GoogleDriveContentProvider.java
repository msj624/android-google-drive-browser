package com.nicstrong.android.provider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.google.api.services.drive.model.File;
import com.nicstrong.android.drive.DriveService;
import com.nicstrong.android.drive.FieldBuilder;
import roboguice.content.RoboContentProvider;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GoogleDriveContentProvider extends RoboContentProvider {
    private static final Logger logger = Logger.getLogger(GoogleDriveContentProvider.class.getName());

    private static final int FILES = 1;
    private static final int FILE_ID = 2;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(GoogleDriveContract.AUTHORITY, "files", FILES);
        URI_MATCHER.addURI(GoogleDriveContract.AUTHORITY, "files/*", FILE_ID);
    }

    @Inject
    DriveService driveService;

    @Override
    public boolean onCreate() {
        super.onCreate();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case FILES:
                return GoogleDriveContract.File.CONTENT_DIR_TYPE;
            case FILE_ID:
                return GoogleDriveContract.File.CONTENT_ITEM_TYPE;
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case FILES:
                return filesQuery(uri, match, projection, selection, selectionArgs, sortOrder);
            case FILE_ID:
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case FILES:

            case FILE_ID:
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Cursor filesQuery(Uri uri, int match, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String fields = null;
        if (projection != null && projection.length > 0) {
            FieldBuilder fieldBuilder = new FieldBuilder();
            fieldBuilder.withItems(projection);
            fields = fieldBuilder.build();
        }

        try {
            driveService.refreshToken(getContext());
            List<File> files = driveService.findFiles(selection, fields);
            for (File file: files) {
                logger.info("File: " + file.getTitle());
                for (Map.Entry<String, Object> entry: file.entrySet()) {
                    logger.info("   " + entry.getKey() + " => " + entry.getValue());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}
