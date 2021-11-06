package com.example.sandanalysis

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.Timestamp

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    Constants.DB_NAME,
    null,
    Constants.DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(Constants.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + Constants.TABLE_NAME)
        onCreate(db)
    }

    //    insert sample
    fun insertSample(
        image: String?,
        inn: String?,
        lat: Float?,
        long: Float?,
        addTimeStamp: String?,
        updatedTimeStamp: String?,

        ): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(Constants.C_INN, inn)
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_LAT, lat)
        values.put(Constants.C_LONG, long)
        values.put(Constants.C_ADD_TIMESTAMP, addTimeStamp)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTimeStamp)
        val id = db.insert(Constants.TABLE_NAME, null, values)
        db.close()
        return id

    }

    //    get all data
    @SuppressLint("Range")
    fun getAllSamples(orderBy: String): ArrayList<ModelSample> {
        val samplesList = ArrayList<ModelSample>()
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} ORDER BY $orderBy"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToNext()) {
            do {
                val modelSample = ModelSample(
                    "" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                    "" + cursor.getInt(cursor.getColumnIndex(Constants.C_INN)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADD_TIMESTAMP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))
                )
                samplesList.add(modelSample)
            } while (cursor.moveToNext())
        }
        db.close()
        return samplesList
    }

    //search data
    fun searchSamples(query: String): ArrayList<ModelSample> {
        val samplesList = ArrayList<ModelSample>()
        val selectQuery =
            "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_INN} LIKE'% $query%' "
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToNext()) {
            do {
                val modelSample = ModelSample(
                    "" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                    "" + cursor.getInt(cursor.getColumnIndex(Constants.C_INN)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADD_TIMESTAMP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))
                )
                samplesList.add(modelSample)
            } while (cursor.moveToNext())
        }
        db.close()
        return samplesList

    }


}