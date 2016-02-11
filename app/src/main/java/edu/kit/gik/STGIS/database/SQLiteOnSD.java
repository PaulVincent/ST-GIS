package edu.kit.gik.STGIS.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class SQLiteOnSD extends SQLiteOpenHelper {

	private static final String DT = "SQLiteOnSDCard";

	private static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();

//	public SQLiteDatabase db;
	
	
	private static final String TAG = SQLiteOnSD.class.getSimpleName();

	private static final String DATABASE_NAME = "Services.db";
	private static final int DATABASE_VERSION = 1;

	// Name und Attribute der Tabelle "mood"
	public static final String _ID = "_id";
	public static final String WFS_TABLE_NAME = "WebFeatureServices";
	public static final String WFS_NAME = "Name";
	public static final String WFS_FEATURETYPE_COUNT = "FeatureTypeCOUNT";
	public static final String WFS_AVAILABILITY = "Availability";
	public static final String WFS_URL = "BaseURL";

	// Tabelle mood anlegen
	private static final String TABLE_WFS_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ WFS_TABLE_NAME + " (" + _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + WFS_NAME + " String, "
			+ WFS_URL + " STRING, " + WFS_AVAILABILITY + " INTEGER);";
	
	// Tabelle mood l�schen
	private static final String TABLE_WFS_DROP = "DROP TABLE "
			+ WFS_TABLE_NAME;

	public SQLiteOnSD(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		Log.d("databaseHelper", "generated");
		// TODO Auto-generated constructor stub
	}
	
	
	public void dropTable() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(TABLE_WFS_DROP);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
//		db.execSQL(TABLE_WFS_DROP);
		db.execSQL(TABLE_WFS_CREATE);
		
		Log.d("database", "generated");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(TAG, "Upgrade der Datenbank von Version " + oldVersion + " zu "
				+ newVersion + "; alle Daten werden geloescht");
		db.execSQL(TABLE_WFS_DROP);
		onCreate(db);
	}
	
	public void insert(String name, String url) {
		long rowId = -1;
		try {
			// Datenbank oeffnen
			SQLiteDatabase db = getWritableDatabase();
			// die zu speichernden Werte
			ContentValues values = new ContentValues();
			values.put(WFS_NAME, name);
			values.put(WFS_URL, url);//			// in die Tabelle mood type
			rowId = db.insert(WFS_TABLE_NAME, null, values);
		} catch (SQLiteException e) {
			Log.e(TAG, "insert()", e);
		} finally {
			Log.d(TAG, "insert(): rowId=" + rowId);
		}
	}

	public Cursor query() {
		// ggf. Datenbank oeffnen
		SQLiteDatabase db = getWritableDatabase();
		onCreate(db);
		return db.query(WFS_TABLE_NAME, new String[] {_ID, WFS_NAME, WFS_URL, WFS_AVAILABILITY}, null, null, null, null,
				WFS_NAME + " DESC");
	}

	public void update(long id, int smiley) {
		// ggf. Datenbank oeffnen
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
//		values.put(MOOD_MOOD, smiley);
//		int numUpdated = db.update(TABLE_NAME_MOOD, values, _ID + " = ?",
//				new String[] { Long.toString(id) });
//		Log.d(TAG, "update(): id=" + id + " -> " + numUpdated);
	}

	public int delete(long id) {
		// ggf. Datenbank oeffnen
		SQLiteDatabase db = getWritableDatabase();
		int numDeleted = db.delete(WFS_TABLE_NAME, _ID + " = ?",
				new String[] { Long.toString(id) });
		Log.d("anzahl", Integer.toString(numDeleted));
//		Log.d(TAG, "delete(): id=" + id + " -> " + numDeleted);
		return numDeleted;
	}

}
