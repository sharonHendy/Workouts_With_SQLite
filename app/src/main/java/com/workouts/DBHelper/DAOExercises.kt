package com.workouts.DBHelper

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.workouts.DTOs.Exercise

class DAOExercises(db : DBHelper) {
    val TABLE_NAME = "Exercises"

    val COL_ID = "Id"
    val COL_NAME = "Name"
    val COL_SEC = "Seconds"
    val COL_MIN = "Minutes"

    private var currId = 0
    private val DB : DBHelper = db

    /**
     * returns a hash set of all the exercises names
     */
    fun getExercisesNames(): HashSet<String>{
        val lstOfNames = HashSet<String>()
        val selectQuery = "SELECT $COL_NAME FROM $TABLE_NAME"
        val db : SQLiteDatabase = DB.writableDatabase
        val cursor : Cursor = db.rawQuery(selectQuery, null)
        if(cursor.moveToFirst()) {
            do {
                lstOfNames.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)))
            } while (cursor.moveToNext())
        }
        db.close()
        return lstOfNames
    }

    /**
     * adds a new exercise to the table.
     * @return true if successful, false otherwise.
     */
    fun addExercise(name: String, seconds: Int, minutes : Int):Boolean{
        val values = ContentValues()
        values.put(COL_ID, currId)
        values.put(COL_NAME, name)
        values.put(COL_SEC, seconds)
        values.put(COL_MIN, minutes)
        val db : SQLiteDatabase = DB.writableDatabase
        val isSuccessful = db.insert(TABLE_NAME, null, values)
        db.close()
        if(isSuccessful != -1L)
            currId += currId
        return isSuccessful != -1L
    }

    /**
     * returns the exercise with the given id.
     * @return null if there's no exercise with this id.
     */
    fun getExercise(id : Int): Exercise?{
        var exercise : Exercise? = null
        val selectQuery : String = "SELECT * FROM $TABLE_NAME WHERE $COL_ID = ?"
        val db = DB.writableDatabase
        val cursor : Cursor = db.rawQuery(selectQuery, arrayOf(""+id))
        if (cursor.moveToFirst()){
            exercise = Exercise()
            exercise.id = id
            exercise.name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
            exercise.seconds = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEC))
            exercise.minutes = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MIN))
        }
        db.close()
        return exercise
    }

    /**
     * deletes the exercise from the db.
     * @return true if successful, false otherwise.
     */
    fun deleteExercise(id: Int): Boolean{
        val db : SQLiteDatabase = DB.writableDatabase
        val isSuccessful = db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(""+id))
        db.close()
        return isSuccessful != 0
    }
}