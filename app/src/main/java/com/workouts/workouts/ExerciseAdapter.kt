package com.workouts.workouts
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ExerciseAdapter(
    private val exercises: MutableList<Exercise>
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return ExerciseViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.exercise_details,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val curExercise = exercises[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.MW_exerciseName).text = curExercise.name
            findViewById<TextView>(R.id.MW_exerciseTime).text = curExercise.time
        }
    }



    override fun getItemCount(): Int {
        return exercises.size
    }


}