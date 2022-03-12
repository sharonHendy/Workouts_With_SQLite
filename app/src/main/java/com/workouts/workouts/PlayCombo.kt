package com.workouts.workouts

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.workouts.DTOs.Combo
import com.workouts.DTOs.Workout
import kotlinx.android.synthetic.main.play_workout.*
import me.zhanghai.android.materialprogressbar.MaterialProgressBar


class PlayCombo : PlayWorkout() {

    lateinit var combo : Combo
    var workouts : MutableList<Workout> = mutableListOf()
    var index : Int = 0 //index of workout in combo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setWorkoutAndExercises(){
        val comboName : String? = intent.getStringExtra("ComboName")
        if (comboName!=null){
            combo  = db.COMBOS.getCombo(comboName)!!
            workouts = db.getWorkoutsOfCombo(comboName)!!
            workout = workouts[0]
            exercises = db.getExercisesOfWorkout(workout.name)!!
            playedWorkoutName.text = workout.name

            val ll_exercisesNext : LinearLayout = findViewById(R.id.ll_exercisesNext)
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            for (workoutt in workouts){
                val nextworkoutName: View =  inflater.inflate(R.layout.next_workout_name, null)
                nextworkoutName.findViewById<TextView>(R.id.nextworkoutName).text = workoutt.name
                ll_exercisesNext.addView(nextworkoutName)
                for (exercise in exercises) {
                    val rowView: View = inflater.inflate(R.layout.exercise_details, null)
                    rowView.findViewById<TextView>(R.id.MW_exerciseName).text = exercise.name
                    rowView.findViewById<TextView>(R.id.MW_exerciseTime).text = exercise.getTime()
                    ll_exercisesNext.addView(rowView)
                }

            }
            ll_exercisesNext.removeViewAt(0)
        }else{
            finish()
        }

    }

    override fun stopTimeCounter(){
        if (index < combo.workouts.size - 1 && timerState != TimerState.Stopped){
            index++
            workout = workouts[index]
            findViewById<MaterialProgressBar>(R.id.progressCountdown).progress = 100
            findViewById<TextView>(R.id.timeCountdown).text = workout.getTotalTime()
            playedWorkoutName.text = workout.name
            if(ll_exercisesNext.childCount > 0){
                ll_exercisesNext.removeViewAt(0) //removes next workout name from view
            }
            startTimeCounter()
        }else{
            timerState = TimerState.Stopped
            btnPause.isEnabled = false
            btnStop.isEnabled = false
            btnStart.isEnabled = true
            ll_exercisesNext.removeAllViews()
            setWorkoutAndExercises()
            initializeColor()
            findViewById<MaterialProgressBar>(R.id.progressCountdown).progress = 100
            workout = workouts[0]
            index = 0
            findViewById<TextView>(R.id.timeCountdown).text = workout.getTotalTime()
            saveTotalTimeOfWorkout()
            releasePlayer()
        }
    }

    override fun initializeColor(){
        var passed = 0
        for (w in workouts){
            for (j in 0 until w.getNumOfExercises() ){
                setExerciseColor(j + passed ,"#BCBEBE")
            }
            passed += w.getNumOfExercises() + 1

        }
    }

}