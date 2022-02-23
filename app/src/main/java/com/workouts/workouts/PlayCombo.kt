package com.workouts.workouts

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.play_workout.*
import me.zhanghai.android.materialprogressbar.MaterialProgressBar


class PlayCombo : PlayWorkout() {

    lateinit var combo : Combo
    var index : Int = 0 //index of workout in combo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setWorkoutAndExercises(){
        //val listOfCombos : HashSet<Combo> = getListOfCombos()
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("Combo", null)
        if (jsonString!=null){
            combo  = Gson().fromJson(jsonString)
            workout = combo.workouts[0]
            playedWorkoutName.text = workout.name

            val ll_exercisesNext : LinearLayout = findViewById(R.id.ll_exercisesNext)
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            for (workoutt in combo.workouts){
                val nextworkoutName: View =  inflater.inflate(R.layout.next_workout_name, null)
                nextworkoutName.findViewById<TextView>(R.id.nextworkoutName).text = workoutt.name
                ll_exercisesNext.addView(nextworkoutName)
                for (exercise in workoutt.exercisesAndTimes) {
                    val rowView: View = inflater.inflate(R.layout.exercise_details, null)
                    rowView.findViewById<TextView>(R.id.MW_exerciseName).text = exercise.name
                    rowView.findViewById<TextView>(R.id.MW_exerciseTime).text = exercise.time
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
            workout = combo.workouts[index]
            findViewById<MaterialProgressBar>(R.id.progressCountdown).progress = 100
            findViewById<TextView>(R.id.timeCountdown).text = workout.totalTime
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
            workout = combo.workouts[0]
            index = 0
            findViewById<TextView>(R.id.timeCountdown).text = workout.totalTime
            saveTotalTimeOfWorkout()
            releasePlayer()
        }
    }

    override fun initializeColor(){
        var passed = 0
        for (w in combo.workouts){
            for (j in 0 until w.exercisesAndTimes.size ){
                setExerciseColor(j + passed ,"#BCBEBE")
            }
            passed += w.exercisesAndTimes.size + 1

        }
    }

}