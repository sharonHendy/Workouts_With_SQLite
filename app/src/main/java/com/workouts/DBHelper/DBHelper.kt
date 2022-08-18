package com.workouts.DBHelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.workouts.DTOs.Combo
import com.workouts.DTOs.Exercise
import com.workouts.DTOs.WeekPerformance
import com.workouts.DTOs.Workout

class DBHelper(Context : Context) : SQLiteOpenHelper(Context, DATABASE_NAME, null, DATABASE_VER) {
    var WORKOUTS :DAOWorkouts = DAOWorkouts(this)
    var EXERCISES : DAOExercises = DAOExercises(this)
    var WEEK_PERFORMANCES : DAOWeekPerformances = DAOWeekPerformances(this)
    var COMBOS : DAOCombos = DAOCombos(this)

    companion object{
        var DATABASE_NAME = "Workouts.db"
        var DATABASE_VER = 1
//        var WORKOUT_CURR_ID = 0
//        var EXERCISE_CURR_ID = 0
//        var COMBO_CURR_ID = 0
//        var NUM_OF_WP = 0

//        var WORKOUTS :DAOWorkouts = DAOWorkouts()
//        var EXERCISES : DAOExercises = DAOExercises()
//        var WEEK_PERFORMANCES : DAOWeekPerformances = DAOWeekPerformances()
//        var COMBOS : DAOCombos = DAOCombos()
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



//    override fun onOpen(db: SQLiteDatabase?) {
//        super.onOpen(db)
//        db!!.execSQL("DROP TABLE IF EXISTS ${WORKOUTS.TABLE_NAME}")
//        db.execSQL("DROP TABLE IF EXISTS ${EXERCISES.TABLE_NAME}")
//        db.execSQL("DROP TABLE IF EXISTS ${WEEK_PERFORMANCES.TABLE_NAME}")
//        db.execSQL("DROP TABLE IF EXISTS ${COMBOS.TABLE_NAME}")
//        onCreate(db)
//    }
    //p0 == db


    override fun onCreate(p0: SQLiteDatabase?) {

        val CREATE_TABLE_WORKOUTS_QUERY : String = ("CREATE TABLE ${WORKOUTS.TABLE_NAME} " +
                "(${WORKOUTS.COL_ID} INTEGER PRIMARY KEY," +
                "${WORKOUTS.COL_NAME} TEXT," +
                "${WORKOUTS.COL_TOTAL_SEC} INTEGER," +
                "${WORKOUTS.COL_TOTAL_MIN} INTEGER," +
                "${WORKOUTS.COL_TOTAL_HOU} INTEGER," +
                "${WORKOUTS.COL_IS_FAVORITE} BOOLEAN," +
                "${WORKOUTS.COL_TIMES_PLAYED} INTEGER," +
                "${WORKOUTS.COL_NUM_OF_EXERCISES} INTEGER)"
                //"${WORKOUTS.COL_EXERCISES} TEXT)"
                )
        val CREATE_TABLE_EXERCISES_QUERY : String = ("CREATE TABLE ${EXERCISES.TABLE_NAME} " +
                "(${EXERCISES.COL_ID} INTEGER PRIMARY KEY," +
                "${EXERCISES.COL_NAME} TEXT," +
                "${EXERCISES.COL_SEC} INTEGER," +
                "${EXERCISES.COL_MIN} INTEGER," +
                "${EXERCISES.COL_WORKOUT} INTEGER,"+
                "${EXERCISES.COL_INDEX} INTEGER,"+ //todo!!
                "FOREIGN KEY(${EXERCISES.COL_WORKOUT}) REFERENCES ${WORKOUTS.TABLE_NAME}(${WORKOUTS.COL_ID}))"
                )
        val CREATE_TABLE_WEEK_PER_QUERY : String = ("CREATE TABLE ${WEEK_PERFORMANCES.TABLE_NAME} " +
                "(${WEEK_PERFORMANCES.COL_START_OF_WEEK} LONG PRIMARY KEY," +
                "${WEEK_PERFORMANCES.COL_SUN} FLOAT," +
                "${WEEK_PERFORMANCES.COL_MON} FLOAT," +
                "${WEEK_PERFORMANCES.COL_TUS} FLOAT," +
                "${WEEK_PERFORMANCES.COL_WED} FLOAT," +
                "${WEEK_PERFORMANCES.COL_THU} FLOAT," +
                "${WEEK_PERFORMANCES.COL_FRI} FLOAT," +
                "${WEEK_PERFORMANCES.COL_SAT} FLOAT)"
                )
        val CREATE_TABLE_COMBOS_QUERY : String = ("CREATE TABLE ${COMBOS.TABLE_NAME} " +
                "(${COMBOS.COL_ID} INTEGER PRIMARY KEY," +
                "${COMBOS.COL_NAME} TEXT," + //TODO PK?
                "${COMBOS.COL_WORKOUTS} TEXT)"
                )

        p0!!.execSQL(CREATE_TABLE_WORKOUTS_QUERY)
        p0.execSQL(CREATE_TABLE_EXERCISES_QUERY)
        p0.execSQL(CREATE_TABLE_WEEK_PER_QUERY)
        p0.execSQL(CREATE_TABLE_COMBOS_QUERY)
        //todo add to week performance 4 rows
//        for (i in 1..4) {
//            WEEK_PERFORMANCES.addWeekPerformance()
//        }
    }

    //p0 == db
    //p1 == oldVersion
    //p2 == newVersion
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS ${WORKOUTS.TABLE_NAME}")
        p0.execSQL("DROP TABLE IF EXISTS ${EXERCISES.TABLE_NAME}")
        p0.execSQL("DROP TABLE IF EXISTS ${WEEK_PERFORMANCES.TABLE_NAME}")
        p0.execSQL("DROP TABLE IF EXISTS ${COMBOS.TABLE_NAME}")
        onCreate(p0)
    }

    /**
     * returns a Mutable List of the workouts exercises.
     * @return null if workout doesn't exists.
     */
    fun getExercisesOfWorkout(workoutName: String): MutableList<Exercise>?{
        var exercises = mutableListOf<Exercise>()

        var workoutId : Int = WORKOUTS.getWorkoutId(workoutName)
        if(workoutId == -1)
            return null
        return EXERCISES.getExercisesOfWorkout(workoutId)

//        var exercisesStr : String? = WORKOUTS.getExercisesOfWorkoutStr(workoutName)
//        if (exercisesStr == null)
//            return null
//        var lstOfIds : List<String> = exercisesStr.split(',')
//        for(id : String in lstOfIds){ //for each id, finds the matching exercise and adds it to the List
//            var exercise : Exercise? = EXERCISES.getExercise(Integer.parseInt(id.trim()))
//            if(exercise == null) //todo !!
//                throw Exception("workout contains id of exercise that doesn't exists. id:" + id)
//            exercises.add(exercise)
//        }
//        return exercises
    }

    /**
     * deletes the workout and its exercises.
     * @return true if successful, false otherwise.
     */
    fun deleteWorkout(workoutName: String): Boolean{
        var workoutId : Int = WORKOUTS.getWorkoutId(workoutName)
        if(workoutId == -1)
            return false
        return EXERCISES.deleteExercisesOfWorkout(workoutId) && WORKOUTS.deleteWorkout(workoutName)

//        var exercisesStr = WORKOUTS.getExercisesOfWorkoutStr(workoutName)
//        if (exercisesStr == null)
//            return false
//        val lstExercises : List<String> = exercisesStr.split(',')
//
//        //deletes the workout from the workouts table
//        var isSuccessful = WORKOUTS.deleteWorkout(workoutName)
//
//        //deletes the workouts exercises
//        if (isSuccessful) {
//            for (id: String in lstExercises) {
//                EXERCISES.deleteExercise( Integer.parseInt(id.trim()))
//            }
//            return true
//        }
//        else
//            return false
    }

    /**
     * returns a mutable list of the combos workouts, or null if the combo doesn't exists.
     */
    fun getWorkoutsOfCombo(comboName : String) : MutableList<Workout>?{
        var workouts : MutableList<Workout> = mutableListOf()
        val workoutsStr = COMBOS.getWorkoutsStrOfCombo( comboName) ?: return null //combo doesn't exists
        val workoutsIds = workoutsStr.split(',')
        for(id : String in workoutsIds){
            val workout = WORKOUTS.getWorkout(Integer.parseInt(id.trim()))
            workouts.add(workout!!)
        }
        return workouts
    }

    /**
     * computes the time of the workout and adds the data to the db.
     */
    fun computeTotalTimeOfWorkout(workoutName: String) : Boolean {
        var exercises = getExercisesOfWorkout(workoutName)
        var seconds = 0
        var minutes = 0
        var hours = 0
        if(exercises != null) {
            for (exer in exercises) {
                seconds += exer.seconds
                minutes += exer.minutes
            }

            //fix the time
            if (seconds > 59){
                minutes += seconds / 60
                seconds = seconds % 60
            }
            if(minutes > 59){
                hours += minutes / 60
                minutes = minutes % 60
            }

            WORKOUTS.setSecMinHou(workoutName, seconds, minutes, hours)
        }else{
            return false
        }
        return true

    }

//    /**
//     * adds a new workout.
//     * @return true if successful.
//     */
//    fun addWorkout(workout: Workout) : Boolean{
//        if(WORKOUTS.addWorkout(this, workout, WORKOUT_CURR_ID)) {
//            WORKOUT_CURR_ID += 1
//            return true
//        }
//        return false
//    }

//    /**
//     * adds a new exercise.
//     * @return the id if successful. -1 otherwise.
//     */
//    fun addExercise(exercise: Exercise) : Int{
//        if(EXERCISES.addExercise(this, exercise, EXERCISE_CURR_ID) != -1){
//            EXERCISE_CURR_ID += 1
//            return EXERCISE_CURR_ID -1
//        }
//        return -1
//    }
//
//    /**
//     * adds a new combo.
//     * @return true if successful.
//     */
//    fun addCombo(combo: Combo) : Boolean{
//        if(COMBOS.addCombo(this, combo, EXERCISE_CURR_ID)){
//            COMBO_CURR_ID += 1
//            return true
//        }
//        return false
//    }

//    fun addWeekPerformance(): Boolean{
//        if(WEEK_PERFORMANCES.addWeekPerformance(this, NUM_OF_WP)){
//            if(NUM_OF_WP < 4)
//                NUM_OF_WP += 1
//            return true
//        }
//        return false
//    }

//    fun addTimeToWeekPerformance( day : Int, time : Float){
//        if(NUM_OF_WP == 0)
//            addWeekPerformance()
//        WEEK_PERFORMANCES.addTimeToWeekPerformance(this, day, time)
//    }

//    fun getAllWorkouts(): HashSet<Workout>{
//        return WORKOUTS.getAllWorkouts(this)
//    }


}