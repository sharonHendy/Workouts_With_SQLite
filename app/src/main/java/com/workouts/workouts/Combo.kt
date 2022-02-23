package com.workouts.workouts

data class Combo(var name : String) {

    var workouts : MutableList<Workout> = ArrayList()

    fun addWorkout(workout: Workout){
        workouts.add(workout)
    }

    fun removeWorkout(workout: Workout){
        workouts.remove(workout)
    }
}