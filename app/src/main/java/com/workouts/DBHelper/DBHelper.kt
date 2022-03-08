package com.workouts.DBHelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.workouts.DTOs.Exercise
import com.workouts.DTOs.WeekPerformance

class DBHelper(Context : Context) : SQLiteOpenHelper(Context, DATABASE_NAME, null, DATABASE_VER) {

    companion object{
        var DATABASE_NAME = "Workouts.db"
        var DATABASE_VER = 1

        //workouts table
//        private val WORKOUT_TABLE_NAME = "Workouts"
//        private val COL_WORKOUT_ID = "Id"
//        private val COL_WORKOUT_NAME = "Name"
//        private val COL_WORKOUT_TOTAL_SEC = "TotalSeconds"
//        private val COL_WORKOUT_TOTAL_MIN = "TotalMinutes"
//        private val COL_WORKOUT_TOTAL_HOU = "TotalHours"
//        private val COL_WORKOUT_IS_FAVORITE = "IsFavorite"
//        private val COL_WORKOUT_TIMES_PLAYED = "TimePlayed"
//        private val COL_WORKOUT_EXERCISES = "Exercises"

//        //exercises table
//        private val EXERCISE_TABLE_NAME = "Exercises"
//        private val COL_EXERCISE_ID = "Id"
//        private val COL_EXERCISE_NAME = "Name"
//        private val COL_EXERCISE_TIME = "Time"
//
//        //week performance table
//        private val WEEK_PERFORMANCE_TABLE_NAME = "WeekPerformances"
//        private val COL_WEEK_PERFORMANCE_ID  = "Id"
//        private val COL_WEEK_PERFORMANCE_SUN = "Sun"
//        private val COL_WEEK_PERFORMANCE_MON = "Mon"
//        private val COL_WEEK_PERFORMANCE_TUS = "Tus"
//        private val COL_WEEK_PERFORMANCE_WED = "Wed"
//        private val COL_WEEK_PERFORMANCE_THU = "Thu"
//        private val COL_WEEK_PERFORMANCE_FRI = "Fri"
//        private val COL_WEEK_PERFORMANCE_SAT = "Sat"
    }

    val WORKOUTS = DAOWorkouts(this)
    val EXERCISES = DAOExercises(this)
    val WEEK_PERFORMANCES = DAOWeekPerformances(this)

    //p0 == db
    override fun onCreate(p0: SQLiteDatabase?) {
        val CREATE_TABLE_WORKOUTS_QUERY : String = ("CREATE TABLE ${WORKOUTS.TABLE_NAME} " +
                "(${WORKOUTS.COL_ID} INTEGER PRIMARY KEY," +
                "${WORKOUTS.COL_NAME} TEXT PRIMARY KEY," +
                "${WORKOUTS.COL_TOTAL_SEC} INTEGER," +
                "${WORKOUTS.COL_TOTAL_MIN} INTEGER," +
                "${WORKOUTS.COL_TOTAL_HOU} INTEGER," +
                "${WORKOUTS.COL_IS_FAVORITE} BOOLEAN," +
                "${WORKOUTS.COL_TIMES_PLAYED} INTEGER," +
                "${WORKOUTS.COL_EXERCISES} TEXT)"
                )
        val CREATE_TABLE_EXERCISES_QUERY : String = ("CREATE TABLE ${EXERCISES.TABLE_NAME} " +
                "(${EXERCISES.COL_ID} INTEGER PRIMARY KEY," +
                "${EXERCISES.COL_NAME} TEXT," +
                "${EXERCISES.COL_SEC} INTEGER" +
                "${EXERCISES.COL_MIN} INTEGER)"
                )
        val CREATE_TABLE_WEEK_PER_QUERY : String = ("CREATE TABLE ${WEEK_PERFORMANCES.TABLE_NAME} " +
                "(${WEEK_PERFORMANCES.COL_ID} INTEGER PRIMARY KEY," +
                "${WEEK_PERFORMANCES.COL_SUN} FLOAT," +
                "${WEEK_PERFORMANCES.COL_MON} FLOAT," +
                "${WEEK_PERFORMANCES.COL_TUS} FLOAT," +
                "${WEEK_PERFORMANCES.COL_WED} FLOAT," +
                "${WEEK_PERFORMANCES.COL_THU} FLOAT," +
                "${WEEK_PERFORMANCES.COL_FRI} FLOAT," +
                "${WEEK_PERFORMANCES.COL_SAT} FLOAT)"
                )

        p0!!.execSQL(CREATE_TABLE_WORKOUTS_QUERY)
        p0.execSQL(CREATE_TABLE_EXERCISES_QUERY)
        p0.execSQL(CREATE_TABLE_WEEK_PER_QUERY)
    }

    //p0 == db
    //p1 == oldVersion
    //p2 == newVersion
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS ${WORKOUTS.TABLE_NAME}")
        p0.execSQL("DROP TABLE IF EXISTS ${EXERCISES.TABLE_NAME}")
        p0.execSQL("DROP TABLE IF EXISTS ${WEEK_PERFORMANCES.TABLE_NAME}")
        onCreate(p0)
    }

    /**
     * returns a hash set of the workouts exercises.
     * @return null if workout doesn't exists.
     */
    fun getExercisesOfWorkout(workoutName: String): HashSet<Exercise>?{
        var exercises = HashSet<Exercise>()
        var exercisesStr : String? = WORKOUTS.getExercisesOfWorkoutStr(workoutName)
        if (exercisesStr == null)
            return null
        var lstOfIds : List<String> = exercisesStr.split(',')
        for(id : String in lstOfIds){ //for each id, finds the matching exercise and adds it to the hash set
            var exercise : Exercise? = EXERCISES.getExercise(Integer.parseInt(id.trim()))
            if(exercise == null) //todo !!
                throw Exception("workout contains id of exercise that doesn't exists. id:" + id)
            exercises.add(exercise)
        }
        return exercises
    }

    /**
     * deletes the workout and its exercises.
     * @return true if successful, false otherwise.
     */
    fun deleteWorkout(workoutName: String): Boolean{
        var exercisesStr = WORKOUTS.getExercisesOfWorkoutStr(workoutName)
        if (exercisesStr == null)
            return false
        val lstExercises : List<String> = exercisesStr.split(',')

        //deletes the workout from the workouts table
        var isSuccessful = WORKOUTS.deleteWorkout(workoutName)

        //deletes the workouts exercises
        if (isSuccessful) {
            for (id: String in lstExercises) {
                EXERCISES.deleteExercise(Integer.parseInt(id.trim()))
            }
            return true
        }
        else
            return false
    }


}