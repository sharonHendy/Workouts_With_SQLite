package com.workouts.workouts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.workouts.objects.Workout
import kotlinx.android.synthetic.main.new_workout.view.*


class WorkoutAdapter(
    private val workouts: HashSet<Workout>,
    private val favOnly: Boolean,
    private val context : Context,
    private val activity : Activity
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    var workoutWithDetailsOpen : Workout? = null
    var viewWithDetailsOpen : View? = null

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        return WorkoutViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.new_workout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val curWorkout = workouts.elementAt(position)
        holder.itemView.apply {
            findViewById<TextView>(R.id.workoutName).text = curWorkout.name
            findViewById<TextView>(R.id.totalTime).text = curWorkout.totalTime

            findViewById<Button>(R.id.btnAddToCombo).isVisible = false
            findViewById<TextView>(R.id.tv_fav).isVisible = curWorkout.isFavorite

            findViewById<Button>(R.id.btnPlay).setOnClickListener {
                startWorkout(curWorkout)
            }

            findViewById<Button>(R.id.btnEdit).setOnClickListener {
                editWorkout(curWorkout)
            }

            workoutName.setOnClickListener {
                if (workoutWithDetailsOpen == curWorkout){
                        closeWorkoutDetails(curWorkout,holder.itemView)
                }else if (workoutWithDetailsOpen != null){
                        closeWorkoutDetails(workoutWithDetailsOpen,viewWithDetailsOpen)
                        openWorkoutDetails(curWorkout, holder.itemView)
                }else{
                        openWorkoutDetails(curWorkout, holder.itemView)
                }
            }


        }
    }

    //opens workout details when clicking on the workout name
    fun openWorkoutDetails(workout: Workout, view: View){
        // val ll_MyWorkouts = requireView().findViewById<LinearLayout>(R.id.ll_MyWorkouts)

        val exerciseAdapter = ExerciseAdapter(workout.exercisesAndTimes)
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rv_workoutExercises: RecyclerView = inflater.inflate(R.layout.workout_exercises, null) as RecyclerView
        view.findViewById<FrameLayout>(R.id.rv_container).addView(rv_workoutExercises)

        rv_workoutExercises.adapter = exerciseAdapter
        rv_workoutExercises.layoutManager = LinearLayoutManager(context)

        workoutWithDetailsOpen = workout
        viewWithDetailsOpen = view

    }

    fun closeWorkoutDetails(workout: Workout?, view: View?): Boolean{
        if (workout!= null && view!= null){
            val rv_container = view.findViewById<FrameLayout>(R.id.rv_container)
            val rv_workoutExercises: RecyclerView =rv_container.findViewById(R.id.rv_workoutExercises)
            //rv_container.removeView(rv_workoutExercises)
//            val rv_workoutExercises : RecyclerView = view.findViewById(R.id.rv_workoutExercises)
//            for (i in rv_workoutExercises.children){
//                rv_workoutExercises.removeView(i)
//            }
            workoutWithDetailsOpen = null
            viewWithDetailsOpen = null
            return true
        }
        return false
    }

    override fun getItemCount(): Int {
        return workouts.size
    }



    //opens edit fragment and send the index of the workout in the list
    fun editWorkout(workout: Workout){
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager

        //send index of the workout to edit
        val bundle : Bundle = bundleOf()
        bundle.putInt("index", workouts.indexOf(workout))
        bundle.putBoolean("favOnlyPressed", favOnly)
        val fragment = CreateFragment()
        fragment.arguments = bundle

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right)
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    //starts play workout activity
    fun startWorkout(workout: Workout){
        val listOfWorkouts : HashSet<Workout> = getListOfWorkouts(activity)
        val intent = Intent(context, PlayWorkout::class.java)
            .putExtra("WorkoutIndex", listOfWorkouts.indexOf(workout))
        startActivity(context,intent,null)
    }

}