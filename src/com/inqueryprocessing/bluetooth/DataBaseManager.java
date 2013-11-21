package com.inqueryprocessing.bluetooth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseManager extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.inqueryprocessing/databases/";
	private static String DB_NAME = "InQuery";
	private SQLiteDatabase myDataBase;
	private SQLiteDatabase myData;
	private Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseManager(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			CopyFiles();
		}
	}

	private void CopyFiles() {
		try {
			InputStream is = myContext.getAssets().open(DB_NAME);
			File outfile = new File(DB_PATH, DB_NAME);
			outfile.getParentFile().mkdirs();
			outfile.createNewFile();

			if (is == null)
				throw new RuntimeException("stream is null");
			else {
				FileOutputStream out = new FileOutputStream(outfile);
				byte buf[] = new byte[128];
				do {
					int numread = is.read(buf);
					if (numread <= 0)
						break;
					out.write(buf, 0, numread);
				} while (true);

				is.close();
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// ---retrieve records---
	public Cursor selectQuery(String query) throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myData = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor mCursor = myData.rawQuery(query, null);
		mCursor.moveToFirst();
		myData.close();

		return mCursor;
	}

	// //////// For Insert And Update Data ////////
	public void insert_update(String query) throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myData = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
		myData.execSQL(query);
		myData.close();
	}
}