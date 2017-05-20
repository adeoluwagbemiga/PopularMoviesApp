package com.adeoluwa.android.popularmoviesapp.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.adeoluwa.android.popularmoviesapp.data.PopularMoviesContract.*;

/**
 * Created by Merlyne on 5/9/2017.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "popularmovies.db";
    private static final int DATABASE_VERSION = 3;
    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + PopularMoviesEntry.TABLE_NAME + " (" +
                        PopularMoviesEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PopularMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        //PopularMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        PopularMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        PopularMoviesEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                        PopularMoviesEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                        PopularMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        PopularMoviesEntry.COLUMN_POSTER + " BLOB NOT NULL, " +
                        " UNIQUE (" + PopularMoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
