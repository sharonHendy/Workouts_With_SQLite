package com.workouts.workouts

data class Workout(var name: String){

    var exercisesAndTimes : MutableList<Exercise> = ArrayList() //workout exercises
    var totalTime: String = "00 : 00" //total time of workout
    var totalMinutes : Int = 0
    var totalSeconds : Int = 0
    var totalHours : Int = 0
    var isFavorite : Boolean = false
    var timePlayed : Int = 0 //number of times the workout has been played

    override fun toString() = name

    /**
     * adds to number of times this workout was played.
     */
    fun addToTimePlayed(){
        timePlayed += 1
    }

    /**
    creates an exercise and adds it to the workout.
     */
    fun addExercise(name: String, time: String){
        exercisesAndTimes.add(Exercise(name, time, getTimeInMillis(time)))
        addToTotalTime(time)
    }

    fun deleteExercise(index : Int){
        exercisesAndTimes.removeAt(index)
    }

    /**
     * gets a string representing time ,returns the time in millis.
     */
    fun getTimeInMillis(time : String): Long{
        val minutesStr : String = time.substring(0,2)
        var secondsStr : String = ""
        if (minutesStr.substring(minutesStr.length - 1).equals(" ")){
            secondsStr  = time.substring(4)
        }
        else{
            secondsStr = time.substring(5)
        }
        val minutesInt : Int = h_extractInt(minutesStr)
        val secondsInt : Int = h_extractInt(secondsStr)
        return ((minutesInt * 60 + secondsInt) * 1000).toLong()
    }

    /**
     * adds time given as string to total time.
     */
    fun addToTotalTime(timeToAdd: String){
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

        totalMinutes += minutesInt
        totalSeconds += secondsInt

        totalTime = "" + totalMinutes + " : " + totalSeconds
        h_fixTotalTime()
    }

    /**
     * fixes the total time if number of seconds or number of minutes is above 59.
     */
    private fun h_fixTotalTime(){
        if (totalSeconds > 59){
            totalMinutes += 1
            totalSeconds = totalSeconds % 60
        }
        if(totalMinutes > 59){
            totalHours += 1
            totalMinutes = totalMinutes % 60
        }
        totalTime = ""+totalHours +":"+ totalMinutes +":"+ totalSeconds
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

}