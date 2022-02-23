package com.workouts.workouts

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

fun getListOfFavoriteWorkouts(activity : Activity): HashSet<Workout> {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val jsonString = sharedPreferences.getString("ListOfFavoriteWorkouts", null)

    return if (jsonString != null)
        Gson().fromJson(jsonString)
    else
        hashSetOf()
}

fun getListOfWorkoutsNames(activity : Activity): HashSet<String> {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val jsonString = sharedPreferences.getString("ListOfWorkoutsNames", null)

    return if (jsonString != null)
        Gson().fromJson(jsonString)
    else
        hashSetOf()
}

fun getListOfWorkouts(activity : Activity): HashSet<Workout> {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val jsonString = sharedPreferences.getString("ListOfWorkouts", null)

    return if (jsonString != null)
        Gson().fromJson(jsonString)
    else
        hashSetOf()
}

//get hash set of exercises from shared prefs
fun getListOfExercises(activity : Activity): HashSet<String> {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val jsonString = sharedPreferences.getString("ListOfExercises", null)

    return if (jsonString != null)
        Gson().fromJson(jsonString)
    else
        hashSetOf()
}

//get hash set of WeekPerformance1 from shared prefs
fun getWeekPerformance1(activity: Activity): ArrayList<Float> {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val jsonString = sharedPreferences.getString("weekPerformance1", null)

    return if(jsonString != null){
        Gson().fromJson(jsonString)
    }else{
        val newWeekPerformance = ArrayList<Float>()
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance
    }
}

//get hash set of WeekPerformance1 from shared prefs
fun getWeekPerformance2(activity: Activity): ArrayList<Float> {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val jsonString = sharedPreferences.getString("weekPerformance2", null)

    return if(jsonString != null){
        Gson().fromJson(jsonString)
    }else{
        val newWeekPerformance = ArrayList<Float>()
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance.add(0f)
        newWeekPerformance
    }
}