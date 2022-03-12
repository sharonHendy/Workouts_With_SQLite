package com.workouts.DBHelper

import android.content.ContentValues
import android.database.Cursor
import com.workouts.DTOs.Combo

class DAOCombos(db: DBHelper)  {
    val TABLE_NAME = "Combos"

    val COL_ID = "Id"
    val COL_NAME = "Name"
    val COL_WORKOUTS = "Workouts"

    //private var currId = 1
    private val DB : DBHelper = db

    /**
     * returns the combo with the given id, or null if it doesn't exists.
     */
    fun getCombo(id: Int): Combo?{
        var combo : Combo? = null
        val db  = DB.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_ID = ?"
        val cursor : Cursor = db.rawQuery(selectQuery, arrayOf("" + id))
        if(cursor.moveToFirst()){
            val comboName = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
            val comboWorkouts = cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUTS))
            combo = Combo(comboName, comboWorkouts.split(',') as MutableList<String>)
        }
        db.close()
        return combo
    }

    /**
     * returns the combo with the given id, or null if it doesn't exists.
     */
    fun getCombo(name: String): Combo?{
        var combo : Combo? = null
        val db  = DB.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_NAME = ?"
        val cursor : Cursor = db.rawQuery(selectQuery, arrayOf("" + name))
        if(cursor.moveToFirst()){
            val comboName = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
            val comboWorkouts = cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUTS))
            combo = Combo(comboName, comboWorkouts.split(',').toMutableList())
        }
        db.close()
        return combo
    }

    /**
     * adds the combo to the db. for DBHelper use only.
     * @return true if successful, false otherwise.
     */
    fun addCombo(combo : Combo) : Boolean{
        val values = ContentValues()
//        values.put(COL_ID, currId)
        values.put(COL_NAME, combo.name)
        values.put(COL_WORKOUTS, combo.workouts.joinToString(","))
        val db  = DB.writableDatabase
        val isSuccessful = db.insert(TABLE_NAME, null, values)
        db.close()
        if(isSuccessful == -1L)
            return false
        //currId +=1
        return true
    }


    /**
     * returns the workout string of the combo with this id. or null if it doesn't exists.
     */
    fun getWorkoutsStrOfCombo( id : Int) : String?{
        var workouts : String? = null
        val selectQuery = "SELECT $COL_WORKOUTS FROM $TABLE_NAME WHERE $COL_ID = ?"
        val db  = DB.writableDatabase
        val cursor : Cursor = db.rawQuery(selectQuery, arrayOf("" + id))
        if(cursor.moveToFirst())
            workouts = cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUTS))
        return workouts
    }

    /**
     * returns the workout string of the combo with this id. or null if it doesn't exists.
     */
    fun getWorkoutsStrOfCombo(comboName : String) : String?{
        var workouts : String? = null
        val selectQuery = "SELECT $COL_WORKOUTS FROM $TABLE_NAME WHERE $COL_NAME = ?"
        val db  = DB.writableDatabase
        val cursor : Cursor = db.rawQuery(selectQuery, arrayOf(comboName))
        if(cursor.moveToFirst())
            workouts = cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUTS))
        return workouts
    }
}