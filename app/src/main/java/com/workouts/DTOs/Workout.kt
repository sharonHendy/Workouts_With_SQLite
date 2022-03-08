package com.workouts.DTOs

class Workout {
    var name: String? = null
    var totalMinutes : Int = 0
    var totalSeconds : Int = 0
    var totalHours : Int = 0
    var isFavorite : Boolean = false
    var timePlayed : Int = 0
    var exercises: String? = ""

    constructor(){}

    constructor( name: String?, totalSec: Int, totalMin: Int, totalHou: Int){
        this.name = name
        this.totalSeconds = totalSec
        this.totalMinutes = totalMin
        this.totalHours = totalHou
    }



}