package com.workouts.DTOs

class Exercise {
    var id : Int = 0
    var name: String? = null
    var seconds : Int = 0
    var minutes : Int = 0

    constructor(){}

    constructor(id : Int, name: String?, seconds : Int, minutes : Int){
        this.id = id
        this.name = name
        this.seconds = seconds
        this.minutes = minutes
    }
}