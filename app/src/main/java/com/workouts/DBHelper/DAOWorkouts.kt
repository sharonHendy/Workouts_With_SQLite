package com.workouts.DBHelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.workouts.DTOs.Exercise
import com.workouts.DTOs.Workout
import java.lang.Exception
import java.lang.IllegalArgumentException

class DAOWorkouts(db : DBHelper) {
    val TABLE_NAME = "Workouts"

    val COL_ID = "Id"
    val COL_NAME = "Name"
    val COL_TOTAL_SEC = "TotalSeconds"
    val COL_TOTAL_MIN = "TotalMinutes"
    val COL_TOTAL_HOU = "TotalHours"
    val COL_IS_FAVORITE = "IsFavorite"
    val COL_TIMES_PLAYED = "TimePlayed"
    val COL_EXERCISES = "Exercises"

    private var currId = 0
    private val DB : DBHelper = db

    /**
     * returns hash set of all workouts.
     */
    fun getAllWorkouts() : HashSet<Workout>{
        val lstWorkouts : HashSet<Workout> = HashSet<Workout>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db : SQLiteDatabase = DB.writableDatabase
        val cursor:Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()){
            do{
                var workout : Workout? = null
                try { //throws exception if one of the columns doesn't exist (not supposed to happen)
                    workout = Workout()
                    workout.name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
                    workout.totalSeconds = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_SEC))
                    workout.totalMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_MIN))
                    workout.totalHours = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_HOU))
                    workout.isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_FAVORITE)) !=0
                    workout.timePlayed = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TIMES_PLAYED))
                    workout.exercises = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCISES))
                }catch (e : IllegalArgumentException){
                    e.printStackTrace()
                }
                if(workout != null)
                    lstWorkouts.add(workout)
            }while (cursor.moveToNext())
        }
        return lstWorkouts
    }

    /**
     * returns hash set of favorite workouts.
     */
    fun getFavoriteWorkouts() : HashSet<Workout>{
        val lstWorkouts : HashSet<Workout> = HashSet<Workout>()
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_IS_FAVORITE = ?"
        val db : SQLiteDatabase = DB.writableDatabase
        val cursor:Cursor = db.rawQuery(selectQuery, arrayOf("TRUE")) //todo ????
        if (cursor.moveToFirst()){
            do{
                var workout : Workout? = null
                try { //throws exception if one of the columns doesn't exist
                    workout = Workout()
                    workout.name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
                    workout.totalSeconds = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_SEC))
                    workout.totalMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_MIN))
                    workout.totalHours = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_HOU))
                    workout.isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_FAVORITE)) !=0
                    workout.timePlayed = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TIMES_PLAYED))
                    workout.exercises = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCISES))
                }catch (e : IllegalArgumentException){
                    e.printStackTrace()
                }
                if(workout != null)
                    lstWorkouts.add(workout)
            }while (cursor.moveToNext())
        }
        db.close()
        return lstWorkouts
    }

    /**
     * returns hash set of workouts' names.
     */
    fun getWorkoutsNames() : HashSet<String>{
        val lstNames : HashSet<String> = HashSet<String>()
        val selectQuery = "SELECT $COL_NAME FROM $TABLE_NAME"
        val db : SQLiteDatabase = DB.writableDatabase
        val cursor:Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()){
            do{
                lstNames.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)))
            }while (cursor.moveToNext())
        }
        db.close()
        return lstNames
    }

    /**
     * adds a new workout to the table
     * @return false if unsuccessful
     */
    fun addWorkout(workout: Workout) : Boolean{
        val values = ContentValues()
        values.put(COL_ID, currId)
        values.put(COL_NAME, workout.name)
        values.put(COL_TOTAL_SEC, workout.totalSeconds)
        values.put(COL_TOTAL_MIN, workout.totalMinutes)
        values.put(COL_TOTAL_HOU, workout.totalHours)
        values.put(COL_IS_FAVORITE, workout.isFavorite)
        values.put(COL_TIMES_PLAYED, workout.timePlayed)
        values.put(COL_EXERCISES, workout.exercises)

        val db : SQLiteDatabase = DB.writableDatabase
        var isSuccessful : Long = db.insert(TABLE_NAME, null, values)
        db.close()
        if(isSuccessful != -1L)
            currId += 1
        return isSuccessful != -1L

    }

    /**
     * increments the workouts time played column by 1
     */
    fun addToTimePlayed(workoutName : String){
        val db : SQLiteDatabase = DB.writableDatabase
        val updateQuery = "UPDATE $TABLE_NAME SET $COL_TIMES_PLAYED = $COL_TIMES_PLAYED + 1 " +
                "WHERE $COL_NAME = ?"
        db.execSQL(updateQuery, arrayOf(workoutName)) //todo ????
        db.close()
    }

    /**
     * adds the exercises id to the list of exercises in the exercises column of the given workout,
     * returns 0 if unsuccessful.
     */
    fun addExerciseToWorkout(workoutName: String, exerciseId : Int) : Int{
        val db : SQLiteDatabase = DB.writableDatabase
        var currExercises : String? = getExercisesOfWorkoutStr(workoutName)
        if (currExercises == null)
            return 0
        currExercises = currExercises + "," + exerciseId
        val values = ContentValues()
        values.put(COL_EXERCISES, currExercises)
        return db.update(TABLE_NAME, values, "$COL_NAME = ?", arrayOf(workoutName)) //returns the number of rows affected
    }

    /**
     * returns the value of the exercises column of the workout, or null if the workout doesn't exists.
     */
    fun getExercisesOfWorkoutStr(workoutName: String) : String?{
        var exercisesStr : String? = null
        val selectQuery = "SELECT $COL_EXERCISES FROM $TABLE_NAME WHERE $COL_NAME = ?"
        val db : SQLiteDatabase = DB.writableDatabase
        val cursor : Cursor = db.rawQuery(selectQuery, arrayOf(workoutName)) //todo??
        if (cursor.moveToFirst()){
            exercisesStr = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCISES))
        }
        db.close()
        return exercisesStr
    }

    /**
     * returns a list of total seconds, minutes, hours of the given workout.
     */
    fun getSecMinHouOfWorkout(workoutName: String) : MutableList<Int>{
        var totalSec : Int = -1
        var totalMin : Int = -1
        var totalHou : Int = -1
        val selectQuery = "SELECT $COL_TOTAL_SEC, $COL_TOTAL_MIN, $COL_TOTAL_HOU " +
                "FROM $TABLE_NAME WHERE $COL_NAME = ?"
        val db : SQLiteDatabase = DB.writableDatabase
        val cursor : Cursor = db.rawQuery(selectQuery, arrayOf(workoutName)) //todo??
        if (cursor.moveToFirst()){
            totalSec = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_SEC))
            totalMin = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_MIN))
            totalHou = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_HOU))
        }
        db.close()
        return mutableListOf(totalSec, totalMin, totalHou)
    }


    /**
     * updates the total time, total seconds, total minutes and total hours of the given workout.
     * @param timeToAdd in format MIN:SEC
     * @return false if unsuccessful
     */
    fun addToTotalTimeOfWorkout(workoutName: String, timeToAdd : String) : Boolean{
        var lstOfTime : MutableList<Int> = getSecMinHouOfWorkout(workoutName)
        if(lstOfTime[0] == -1 || lstOfTime[1] == -1 || lstOfTime[2] == -1)
            return false

        //extracts the minutes and seconds from timeToAdd
        val minutesStr : String = timeToAdd.substring(0,2)
        var secondsStr : String = ""
        if(minutesStr.substring(minutesStr.length - 1).equals(" ")){
            secondsStr  = timeToAdd.substring(4)
        }
        else{
            secondsStr = timeToAdd.substring(5)
        }
        val minutesInt : Int = h_extractInt(minutesStr)
        val secondsInt : Int = h_extractInt(secondsStr)

        lstOfTime[0] += secondsInt
        lstOfTime[1] += minutesInt
        h_fixTime(lstOfTime)

        val db : SQLiteDatabase = DB.writableDatabase
        val values : ContentValues = ContentValues()
        values.put(COL_TOTAL_SEC, lstOfTime[0])
        values.put(COL_TOTAL_MIN, lstOfTime[1])
        values.put(COL_TOTAL_HOU, lstOfTime[2])
        val isSuccessful = db.update(TABLE_NAME, values, "$COL_NAME = ?", arrayOf(workoutName))
        return isSuccessful != 0
    }

    /**
     * returns the total time of the given workout in format - XX:XX:XX
     */
    fun getTotalTimeOfWorkout(workoutName: String) : String?{
        var lstOfTime : MutableList<Int> = getSecMinHouOfWorkout(workoutName)
        if(lstOfTime[0] == -1 || lstOfTime[1] == -1 || lstOfTime[2] == -1)
            return null
        return ""+padd(lstOfTime[2]) +":"+ padd(lstOfTime[1]) +":"+ padd(lstOfTime[0])
    }

    /**
     * fixes the total time if number of seconds or number of minutes is above 59.
     * @param MutableList of (seconds, minutes, hours)
     */
    private fun h_fixTime(lstTime : MutableList<Int>){
        if (lstTime[0] > 59){
            lstTime.set(1, lstTime[1] + 1)
            lstTime.set(0, lstTime[0] % 60)
        }
        if(lstTime[1] > 59){
            lstTime.set(2, lstTime[2] + 1)
            lstTime.set(1, lstTime[1] % 60)
        }
    }

    /**
     * parses string of 2 chars to int.
     * @param int of 2 chars
     */
    private fun h_extractInt(numStr : String) : Int{
        var numInt : Int = 0
        if(numStr.substring(numStr.length - 1).equals(" ")){
            numInt = numStr.substring(0,1).toInt()
        }
        else{
            numInt = numStr.toInt()
        }

        return numInt
    }

    /**
     * returns a string of the given int' padds it with 0 if it is less than 10.
     * @param time >=0
     */
    private fun padd(time :Int) : String{
        if(time / 10 == 0)
            return "0" + time
        return "" + time
    }

    /**
     * deletes the workout from the DB. for DBHelper use only.
     * @return true if successful, false otherwise.
     */
    fun deleteWorkout(workoutName: String) : Boolean{
        val db: SQLiteDatabase = DB.writableDatabase
        var isSuccessful = db.delete(TABLE_NAME, "$COL_NAME =?" , arrayOf(workoutName))
        db.close()
        return isSuccessful != 0
    }

    /**
     * sets the timePlayed value to 0.
     * @return true if successful, false otherwise.
     */
    fun resetTimePlayed(workoutName: String):Boolean{
        val values = ContentValues()
        values.put(COL_TIMES_PLAYED, 0)
        val db: SQLiteDatabase = DB.writableDatabase
        var isSuccessful = db.update(TABLE_NAME, values, "$COL_NAME = ?", arrayOf(workoutName))
        db.close()
        return isSuccessful != 0
    }

}