package com.workouts.DTOs


class Combo {
    var name: String
    var workouts : MutableList<String> //list of the workouts ids

    constructor(name: String, workouts: MutableList<String>) {
        this.name = name
        this.workouts = workouts
    }

    fun addWorkoutId(id : String){
        workouts.add(id)
    }
    fun removeWorkoutId(id : String){
        workouts.remove(id)
    }
}