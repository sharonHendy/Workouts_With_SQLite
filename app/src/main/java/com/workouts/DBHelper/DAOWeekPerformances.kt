package com.workouts.DBHelper

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.workouts.DTOs.WeekPerformance
import java.time.DayOfWeek
import java.util.*

class DAOWeekPerformances(db: DBHelper) {
    val TABLE_NAME = "Week_Performances"

   // val COL_ID = "Id"
    val COL_SUN = "SUN"
    val COL_MON = "MON"
    val COL_TUS = "TUS"
    val COL_WED = "WED"
    val COL_THU = "THU"
    val COL_FRI = "FRI"
    val COL_SAT = "SAT"
    val COL_START_OF_WEEK = "StartOfWeekDate"

    //private var currId = 1
    //private var numOfWP = 0
    private val DB : DBHelper = db

    /**
     * returns the week performance with the given id, if there is no week performance with this id,
     * returns week performance with 0 values.
     */
    fun getWeekPerformance( numOfWeek : Int) :WeekPerformance{
        var weekPer : WeekPerformance = WeekPerformance()
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_START_OF_WEEK = ?"
        var startOfWeek = getStartOfWeek(numOfWeek)
        val db: SQLiteDatabase = DB.writableDatabase

        val cursor : Cursor = db.rawQuery(selectQuery, arrayOf(""+startOfWeek))
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
     * deletes week performances with start date less than or equal to the given date.
     * @return true if successful, false otherwise.
     */
    fun deleteWeekPerformance( untilDate : Long): Boolean{
        val db: SQLiteDatabase = DB.writableDatabase
        val isSuccessful = db.delete(TABLE_NAME, "$COL_START_OF_WEEK <= ?" , arrayOf(""+untilDate))
        db.close()
        return isSuccessful != 0
    }

//    /**
//     * updates the id of the week performance to be the newId.
//     * @return true if successful, false otherwise.
//     */
//    fun updateId( id: Int, newId:Int): Boolean{
//        val values = ContentValues()
//        values.put(COL_ID, newId)
//        val db: SQLiteDatabase = DB.writableDatabase
//        val isSuccessful = db.update(TABLE_NAME,values,"$COL_ID = ?", arrayOf("" + id))
//        db.close()
//        return isSuccessful != 0
//    }

    private fun getStartOfWeek(week : Int): Long {
        //sets the calendar to show time of the first day of this week.
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, -(7 * (week -1)))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        return cal.timeInMillis
    }

    /**
     * adds a weeks performance. if the db already stores 4 week performances, deletes the oldest one
     *  for DBHelper use only.
     * @return true if successful, false otherwise.
     */
    fun addWeekPerformance(): Boolean{
        //if(numOfWP == 4) {

        //}
//        for(i in 3 downTo 1){ //updates the ids of the rest of the weeks
//            updateId(i, i+1)
//        }

        val startOfWeek = getStartOfWeek(1)

        deleteWeekPerformance(getStartOfWeek(3)) //deletes the forth week data

        //adds a new week performance.
        val values = ContentValues()
        values.put(COL_SUN, 0)
        values.put(COL_MON, 0)
        values.put(COL_TUS, 0)
        values.put(COL_WED, 0)
        values.put(COL_THU, 0)
        values.put(COL_FRI, 0)
        values.put(COL_SAT, 0)
        values.put(COL_START_OF_WEEK ,startOfWeek)

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

        val column : String = if(day == 1)
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

        //sets the calendar to show time of the first day of this week.
        val startOfWeek = getStartOfWeek(1)

        //checks if there is a week performance row in the table of this week.
        val db : SQLiteDatabase = DB.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_START_OF_WEEK = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(""+startOfWeek))
        if(!cursor.moveToFirst()){
            cursor.close()
            db.close()
            addWeekPerformance()
        }
        db.close()

        val newTime = time + getTimeOfDayInWeek(column, ""+startOfWeek)

        //adds the time to this week performance.
        val db1 : SQLiteDatabase = DB.writableDatabase
        val updateQuery = "UPDATE $TABLE_NAME SET $column = ? " +
                "WHERE $COL_START_OF_WEEK = ?"
        db1.execSQL(updateQuery, arrayOf(""+newTime, ""+ startOfWeek))
        db1.close()
    }

    fun getTimeOfDayInWeek(column : String, startOfWeek : String): Float {
        var time = 0f;
        val db : SQLiteDatabase = DB.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_START_OF_WEEK = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(startOfWeek))
        if(cursor.moveToFirst()){
            time = cursor.getFloat(cursor.getColumnIndexOrThrow(column))
        }
        db.close()
        return time
    }

    /**
     * sets all the columns to be 0.
     */
    fun clearData(){
        val db : SQLiteDatabase = DB.writableDatabase
        val updateQuery = "UPDATE $TABLE_NAME SET $COL_SUN = 0 , $COL_MON = 0, $COL_TUS = 0, $COL_WED = 0," +
                "$COL_THU = 0, $COL_FRI = 0, $COL_SAT = 0"
        db.execSQL(updateQuery)
        db.close()
    }
}