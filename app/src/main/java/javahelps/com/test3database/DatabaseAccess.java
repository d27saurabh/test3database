package javahelps.com.test3database;

/**
 * Created by admin on 10/16/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;


    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }


    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }


    public void open() {
        this.database = openHelper.getWritableDatabase();
    }


    public void close() {
        if (database != null) {
            this.database.close();
        }
    }


    public ArrayList<HouseDetails> housedetails() {
        ArrayList<HouseDetails> arraylist = new ArrayList<>();
            Cursor cursor = database.rawQuery("SELECT addressname,county,state,zipcode,price,latval,longval FROM detailsval" +
                    " WHERE state='" + StartActivity.mState.getText() +
                    "' and county ='" + StartActivity.mCity.getText() +
                    "' and zipcode =" + StartActivity.mZipcode.getText()
                    + ";", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                HouseDetails addressdetails1 = new HouseDetails(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                arraylist.add(addressdetails1);
                cursor.moveToNext();
            }
            cursor.close();

        return arraylist;
    }

    public ArrayList<LatLng> position_details(){
        ArrayList<LatLng> position =  new ArrayList<>();
            Cursor cursor_latlng = database.rawQuery("SELECT latval,longval FROM detailsval" +
                    " WHERE state='" + StartActivity.mState.getText() +
                    "' and county ='" + StartActivity.mCity.getText() +
                    "' and zipcode =" + StartActivity.mZipcode.getText()
                    + ";", null);
            cursor_latlng.moveToFirst();
            while (!cursor_latlng.isAfterLast()) {
                LatLng addressPosition = new LatLng(cursor_latlng.getFloat(0), cursor_latlng.getFloat(1));
                position.add(addressPosition);
                cursor_latlng.moveToNext();
            }
            cursor_latlng.close();

        return position;
    }


}
