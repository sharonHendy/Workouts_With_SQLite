package com.workouts.DBHelper

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.workouts.DTOs.WeekPerformance
import java.time.DayOfWeek

class DAOWeekPerformances(db: DBHelper) {
    val TABLE_NAME = "Week_Performances"

    val COL_ID = "Id"
    val COL_SUN = "SUN"
    val COL_MON = "MON"
    val COL_TUS = "TUS"
    val COL_WED = "WED"
    val COL_THU = "THU"
    val COL_FRI = "FRI"
    val COL_SAT = "SAT"

    //private var currId = 1
    //private var numOfWP = 0
    private val DB : DBHelper = db

    /**
     * returns the week performance with the given id, if there is no week performance with this id,
     * returns week performance with 0 values.
     */
    fun getWeekPerformance( id :Int) :WeekPerformance{
        var weekPer : WeekPerformance = WeekPerformance()
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_ID = ?"
        val db: SQLiteDatabase = DB.writableDatabase
        val cursor : Cursor = db.rawQuery(selectQuery, arrayOf(""+id))
        if(cursor.moveToFirst()){
            weekPer.SUN = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_SUN))
            weekPer.MON = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_MON))
            weekPer.TUS = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_TUS))
            weekPer.WED = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_WED))
            weekPer.THU = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_THU))
            weekPer.FRI = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_FRI))
            weekPer.SAT = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_SAT))
        }
        db.close()
        return weekPer
    }

    /**
     * deletes the week performance with this id.
     * @return true if successful, false otherwise.
     */
    fun deleteWeekPerformance( id : Int): Boolean{
        val db: SQLiteDatabase = DB.writableDatabase
        val isSuccessful = db.delete(TABLE_NAME, "$COL_ID =?" , arrayOf(""+id))
        db.close()
        return isSuccessful != 0
    }

    /**
     * updates the id of the week performance to be the newId.
     * @return true if successful, false otherwise.
     */
    fun updateId( id: Int, newId:Int): Boolean{
        val values = ContentValues()
        values.put(COL_ID, newId)
        val db: SQLiteDatabase = DB.writableDatabase
        val isSuccessful = db.update(TABLE_NAME,values,"$COL_ID = ?", arrayOf("" + id))
        db.close()
        return isSuccessful != 0
    }

    /**
     * adds a weeks performance. if the db already stores 4 week performances, deletes the oldest one
     * and updates the ids. for DBHelper use only.
     * @return true if successful, false otherwise.
     */
    fun addWeekPerformance(): Boolean{
        //if(numOfWP == 4) {
            deleteWeekPerformance(4) //deletes the forth week data todo if exists?
        //}
        for(i in 3 downTo 1){ //updates the ids of the rest of the weeks
            updateId(i, i+1)
        }

        //adds a new week performance.
        val values = ContentValues()
        values.put(COL_ID, 1)
        values.put(COL_SUN, 0)
        values.put(COL_MON, 0)
        values.put(COL_TUS, 0)
        values.put(COL_WED, 0)
        values.put(COL_THU, 0)
        values.put(COL_FRI, 0)
        values.put(COL_SAT, 0)

        val db = DB.writableDatabase
        val isSuccessful = db.insert(TABLE_NAME, null, values)
        db.close()

//        if(isSuccessful != -1L && numOfWP <4) //increase numOfWP if needed
//            numOfWP += 1

        return isSuccessful != -1L
    }

    /**
     * adds the time to the column of the given day. for DBHelper use only.
     * if there's no week performance stored yet, adds one and adds the time to it.
     * @param day 1..7
     *        time to add in millis
     */
    fun addTimeToWeekPerformance( day : Int, time : Float){

        var column : String = if(day == 1)
            COL_SUN
        else if (day == 2)
            COL_MON
        else if(day == 3)
            COL_TUS
        else if(day == 4)
            COL_WED
        else if(day == 5)
            COL_THU
        else if(day == 6)
            COL_FRI
        else
            COL_SAT

        //checks if there if a week performance row in the table, if not adds one.
        val db : SQLiteDatabase = DB.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        if(!cursor.moveToFirst()){
            cursor.close()
            db.close()
            addWeekPerformance()
        }
        val updateQuery = "UPDATE $TABLE_NAME SET $column = $column + ? " +
                "WHERE $COL_ID = 1"
        db.execSQL(updateQuery, arrayOf(""+time))
        db.close()
    }

    /**
     * sets all the columns to be 0 (except the id).
     */
    fun clearData(){
        val db : SQLiteDatabase = DB.writableDatabase
        val updateQuery = "UPDATE $TABLE_NAME SET $COL_SUN = 0 , $COL_MON = 0, $COL_TUS = 0, $COL_WED = 0," +
                "$COL_THU = 0, $COL_FRI = 0, $COL_SAT = 0"
        db.execSQL(updateQuery)
        db.close()
    }
}