package com.workouts.DTOs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class Workout {
    lateinit var name: String
    var totalMinutes : Int = 0
    var totalSeconds : Int = 0
    var totalHours : Int = 0
    var isFavorite : Boolean = false
    var timePlayed : Int = 0
    var exercises: MutableList<String> = mutableListOf()

    constructor(name: String){
        this.name = name
    }

    constructor( name: String, totalSec: Int, totalMin: Int, totalHou: Int){
        this.name = name
        this.totalSeconds = totalSec
        this.totalMinutes = totalMin
        this.totalHours = totalHou
    }

    /**
     * updates the total time, total seconds, total minutes and total hours of the given workout.
     * @param timeToAdd in format MIN:SEC
     * */
    fun addToTotalTimeOfWorkout(timeToAdd : String){
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

        totalSeconds += secondsInt
        totalMinutes += minutesInt
        h_fixTime()
    }

    /**
     * fixes the total time if number of seconds or number of minutes is above 59.
     * @param MutableList of (seconds, minutes, hours)
     */
    private fun h_fixTime(){
        if (totalSeconds > 59){
            totalMinutes += 1
            totalSeconds %= 60
        }
        if(totalMinutes > 59){
            totalHours += 1
            totalMinutes %= 60
        }
    }

    /**
     * parses string of 2 chars to int.
     * @param String of 2 chars representing a number
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

    fun getTotalTime(): String{
        return padd(totalHours)+":" + padd(totalMinutes) + ":" + padd(totalSeconds)
    }

    fun getNumOfExercises():Int{
        return exercises.size
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
     * add the id of the exercise to the exercises list of the workout, and adds its time to the total time.
     */
    fun addExercise(id : String, time: String){
        exercises.add(id)
        addToTotalTimeOfWorkout(time)
    }

}