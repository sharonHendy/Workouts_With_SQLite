package com.workouts.DTOs

class Exercise {
    var id : Int = 0
    var name: String
    var seconds : Int = 0
    var minutes : Int = 0
    var workout : Int = 0
    var index : Int = 0

    constructor( name: String, seconds : Int, minutes : Int, workout: Int, index : Int){
        this.name = name
        this.seconds = seconds
        this.minutes = minutes
        this.workout = workout
        this.index = index
    }

    constructor( name: String, time : String, workout: Int, index : Int){
        this.name = name
        this.workout = workout
        this.index = index

        val minutesStr : String = time.substring(0,2)
        var secondsStr : String = ""
        if(minutesStr.substring(minutesStr.length - 1).equals(" ")){
            secondsStr  = time.substring(4)
        }
        else{
            secondsStr = time.substring(5)
        }

        this.seconds = h_extractInt(secondsStr)
        this.minutes = h_extractInt(minutesStr)
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


    fun getTime() :String{
        return padd(minutes)+ " : " + padd(seconds)
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


    fun getTimeInMillis() : Long{
        return ((minutes * 60 + seconds) * 1000).toLong()
    }
}