package com.example.moviequiz;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "mydb";
	private static final int DATABASE_VERSION = 1;
	public SQLiteDatabase mDb;
	private Context mContext;

	// Data

	public DbAdapter(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = ctx;
		ctx.deleteDatabase(DATABASE_NAME);
		this.mDb = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		createTableMovies(db);
		createTableStars(db);
		createTableStarsInMovies(db);


	}

	private void createTableMovies(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE movies ( id INTEGER PRIMARY KEY autoincrement, TITLE TEXT NOT NULL, year INTEGER, director TEXT);");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					mContext.getAssets().open("movies.csv")));
			String line;
			while ((line = in.readLine()) != null) {
				String[] items = line.split("\\$");
				ContentValues values = new ContentValues();
				values.put("id", Integer.parseInt(items[0]));
				values.put("title", items[1]);
				values.put("year", Integer.parseInt(items[2]));
				values.put("director", items[3]);
				db.insert("movies", null, values);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void createTableStars(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE stars ( id INTEGER PRIMARY KEY autoincrement, first_name TEXT NOT NULL, last_name TEXT NOT NULL);");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					mContext.getAssets().open("stars.csv")));
			String line;
			while ((line = in.readLine()) != null) {
				String[] items = line.split("\\$");
				ContentValues values = new ContentValues();
				values.put("id", Integer.parseInt(items[0]));
				values.put("first_name", items[1]);
				values.put("last_name", items[2]);
				db.insert("stars", null, values);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void createTableStarsInMovies(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE stars_in_movies ( id INTEGER PRIMARY KEY autoincrement, star_id INTEGER NOT NULL, movie_id INTEGER NOT NULL);");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					mContext.getAssets().open("stars_in_movies.csv")));
			String line;
			while ((line = in.readLine()) != null) {
				String[] items = line.split("\\$");
				ContentValues values = new ContentValues();
				values.put("star_id", Integer.parseInt(items[0]));
				values.put("movie_id", Integer.parseInt(items[1]));
				db.insert("stars_in_movies", null, values);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS movies");
		db.execSQL("DROP TABLE IF EXISTS stars");
		db.execSQL("DROP TABLE IF EXISTS stars_in_movies");

		onCreate(db);
	}
	public void executeQuery(String s) {
	}

}
