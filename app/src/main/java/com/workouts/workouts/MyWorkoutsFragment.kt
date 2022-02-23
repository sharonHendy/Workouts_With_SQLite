package com.workouts.workouts

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_my_workouts.*


class MyWorkoutsFragment : Fragment() {

    var favClickedOn : Boolean = false
    var comboClickedOn : Boolean = false
    var newCombo : Combo? = null
    var position : Int = 0 //index of workout in new combo
    var workoutWithDetailsOpen : Workout? = null
    var viewWithDetailsOpen : View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sets listener for favorite button, show only favorite com.com.com.workouts.workouts.com.workouts.workouts.com.com.workouts.workouts.com.workouts.workouts
        btnFavorites.setOnClickListener {
            favClickedOn = !favClickedOn
            if(comboClickedOn){ //closes create combo mode if it was on without animation
                comboClickedOn = false
                close_createCombo(false)
                //view.postDelayed({showOnlyFav()},900)
            }
            showOnlyFav()
        }

        //swiches to create combo mode
        btnCombo.setOnClickListener {
            if(comboClickedOn){
                close_createCombo()
            }else{
                createCombo()
            }
            //comboClickedOn = !comboClickedOn
        }

        update()

        //open listener for create button
        btnCreate.setOnClickListener{
            createNewWorkout()
        }


    }

//    fun share(workout: Workout){
//        val sharedWorkout = Gson().toJson(workout)
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.setType("text/plain")
//        intent.putExtra(Intent.EXTRA_TEXT, sharedWorkout)
//        startActivity(Intent.createChooser(intent, "Choose"))
//    }

    //close create combo mode
    fun close_createCombo(withAnim : Boolean = true) {
        comboClickedOn = false
        newCombo = null
        position = 0

        changeBtnCreateAnimation("create", R.drawable.create)
        btnCreate.setOnClickListener{
            createNewWorkout()
        }

        if (withAnim){
            animCloseCreateCombo()
        }
    }

    //switches select buttons to favorite button with animation
    fun animCloseCreateCombo(){
        //val ll_MyWorkouts = requireView().findViewById<LinearLayout>(R.id.ll_MyWorkouts)
        for (item in ll_MyWorkouts){
            val btnAddToCombo = item.findViewById<Button>(R.id.btnAddToCombo)
            val tv_fav = item.findViewById<TextView>(R.id.tv_fav)
            btnAddToCombo.animate().apply {
                alpha(0f)
                duration = 500
                rotationYBy(-360f)
            }.withEndAction {
                btnAddToCombo.visibility = View.GONE
                val indexOfChild = ll_MyWorkouts.indexOfChild(item)
                if(favClickedOn || getListOfWorkouts(requireActivity()).elementAt(indexOfChild).isFavorite){
                    tv_fav.apply{
                        alpha = 0f
                        visibility = View.VISIBLE
                        animate().apply {
                            alpha(1f)
                            duration = 200
                        }
                    }
                }
                btnAddToCombo.text = ""
            }

        }
    }

    //switches create button mode
    private fun changeBtnCreateAnimation(text : String, icon : Int){
        val anim = AlphaAnimation(1.0f, 0.5f)
        anim.duration = 300
        anim.repeatCount = 1
        anim.repeatMode = Animation.REVERSE

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) { }
            override fun onAnimationStart(animation: Animation?) { }
            override fun onAnimationRepeat(animation: Animation?) {
                btnCreate.text = text
                btnCreate.icon = ResourcesCompat.getDrawable(resources, icon, null);
            }
        })
        btnCreate.startAnimation(anim)
    }

    //swiches to create combo mode
    fun createCombo(){

        comboClickedOn = true
        val listOfWorkouts : HashSet<Workout> = getListOfWorkouts(requireActivity())
        //val ll_MyWorkouts = requireView().findViewById<LinearLayout>(R.id.ll_MyWorkouts)
        for (item in ll_MyWorkouts){
            val btnAddToCombo = item.findViewById<Button>(R.id.btnAddToCombo)
           // btnAddToCombo.setBackgroundColor(Color.BLACK)
            val tv_fav = item.findViewById<TextView>(R.id.tv_fav)

            //animate changing the select buttons
            tv_fav.animate().apply {
                alpha(0f)
                duration = 200
            }.withEndAction {
                tv_fav.visibility = View.GONE
                btnAddToCombo.apply{
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate().apply {
                        alpha(1f)
                        duration = 500
                        rotationYBy(360f)
                    }
                }
            }

            newCombo = Combo("name")

            //animate changing the create button
            changeBtnCreateAnimation("create combo", R.drawable.create)

            btnAddToCombo.setOnClickListener { //numbers the buttons and adds to newCombo
                addToCombo(item)
            }

            btnCreate.setOnClickListener{ //opens create combo dialog
                if (newCombo == null || newCombo!!.workouts.size == 0){ //if no com.com.com.workouts.workouts.com.workouts.workouts.com.com.workouts.workouts.com.workouts.workouts selected
                    Toast.makeText(context,"please select at least one workout",Toast.LENGTH_SHORT).show()
                }else{
                    saveCombo()
                }
            }
        }
    }

    //adds selected workout to combo and modifies the position
    fun addToCombo(item : View){
        //closeWorkoutDetails(workoutWithDetailsOpen,viewWithDetailsOpen) //closes details if open

        //loads the list of com.com.com.workouts.workouts.com.workouts.workouts.com.com.workouts.workouts.com.workouts.workouts
        val listOfWorkouts : HashSet<Workout> = if(favClickedOn){
            getListOfFavoriteWorkouts(requireActivity())
        }else{
            getListOfWorkouts(requireActivity())
        }

        //adds the workout to the combo
        //val ll_MyWorkouts = requireView().findViewById<LinearLayout>(R.id.ll_MyWorkouts)
        val btnAddToCombo = item.findViewById<Button>(R.id.btnAddToCombo)
        val index = ll_MyWorkouts.indexOfChild(item)
        val currWorkout = listOfWorkouts.elementAt(index)
        if(btnAddToCombo.text.equals("")){
            newCombo?.addWorkout(currWorkout)
            position++
            btnAddToCombo.text = "$position"
        }else{
            btnAddToCombo.text = ""
            position--
            newCombo!!.workouts.remove(currWorkout)
            for (i in ll_MyWorkouts){ //organizes the indexes
                val btnAddToCombo2 = i.findViewById<Button>(R.id.btnAddToCombo)
                val currWorkout2 = listOfWorkouts.elementAt(ll_MyWorkouts.indexOfChild(i))
                if(!(btnAddToCombo2.text.equals(""))){
                    val index = newCombo!!.workouts.indexOf(currWorkout2)
                    btnAddToCombo2.text = ""+ (index+1)
                }
            }
        }
    }

    //opens start create combo dialog
    fun saveCombo(){
        if(newCombo!=null){
            val dialog = MaterialDialog(requireContext())
                .customView(R.layout.create_combo_dialog)

            val ll_selectedWorkouts = dialog.findViewById<LinearLayout>(R.id.ll_selectedWorkouts)
            //inflates selected com.com.com.workouts.workouts.com.workouts.workouts.com.com.workouts.workouts.com.workouts.workouts views
            for(workout in newCombo!!.workouts){
                val inflater =
                    requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView: View = inflater.inflate(R.layout.workout_selected_for_combo, null)

                rowView.findViewById<TextView>(R.id.S_workoutName).text = workout.name
                rowView.findViewById<TextView>(R.id.S_workoutTime).text = workout.totalTime
                ll_selectedWorkouts.addView(rowView,ll_selectedWorkouts.childCount)
            }
            dialog.findViewById<Button>(R.id.btnPlayCombo).setOnClickListener {

                //saves combo in shared prefs
                val sharedPreferences: SharedPreferences =
                    requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                val jsonString = Gson().toJson(newCombo)
                editor.putString("Combo", jsonString)
                editor.apply()

                //val listOfCombos : HashSet<Combo> = getListOfCombos()
                val intent = Intent(requireContext(), PlayCombo::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                close_createCombo()
                dialog.cancel()
            }
            dialog.show()
        }
    }

    //loads favorites only
    fun showOnlyFav(){
        if(favClickedOn){
            btnFavorites.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.favorite)
            update(true)
        }else{
            btnFavorites.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.favorite_border)
            update(false)
        }

    }

    //loads the com.com.com.workouts.workouts.com.workouts.workouts.com.com.workouts.workouts.com.workouts.workouts from shared prefs and opens listeners
     fun update(favOnly : Boolean = false){

        val listOfWorkouts : HashSet<Workout>
        val ll_MyWorkouts = requireView().findViewById<LinearLayout>(R.id.ll_MyWorkouts)
        ll_MyWorkouts.removeAllViews()

        if (favOnly){
            listOfWorkouts = getListOfFavoriteWorkouts(requireActivity())
        }else{
            listOfWorkouts = getListOfWorkouts(requireActivity())
        }
//
//        val workoutAdapter = WorkoutAdapter(listOfWorkouts,favOnly, requireContext(), requireActivity())
//
//        rv_MyWorkouts.adapter = workoutAdapter
//        rv_MyWorkouts.layoutManager = LinearLayoutManager(requireContext())

        //adds views with saved com.com.com.workouts.workouts.com.workouts.workouts.com.com.workouts.workouts.com.workouts.workouts' details
        for (workout in listOfWorkouts){
            if((favOnly and workout.isFavorite) or !favOnly) {
                val inflater =
                    requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView: View = inflater.inflate(R.layout.new_workout, null)

                rowView.findViewById<Button>(R.id.btnAddToCombo).isVisible = false
                rowView.findViewById<TextView>(R.id.tv_fav).isVisible = workout.isFavorite

                val name: String = workout.name
                val totalTime: String = workout.totalTime
                rowView.findViewById<TextView>(R.id.workoutName).text = name
                rowView.findViewById<TextView>(R.id.totalTime).text = totalTime

                ll_MyWorkouts!!.addView(rowView, ll_MyWorkouts!!.childCount)

                //open listener for the name to open the exercises' details
                if (workoutWithDetailsOpen == workout) {
                    closeWorkoutDetails(workoutWithDetailsOpen, viewWithDetailsOpen)
                }
                rowView.findViewById<TextView>(R.id.workoutName).setOnClickListener {
                    if (workoutWithDetailsOpen == workout){
                        closeWorkoutDetails(workoutWithDetailsOpen,viewWithDetailsOpen)
                    }else if (workoutWithDetailsOpen != null){
                        closeWorkoutDetails(workoutWithDetailsOpen,viewWithDetailsOpen)
                        openWorkoutDetails(workout, rowView)
                    }else{
                        openWorkoutDetails(workout, rowView)
                    }
                }

                //open listener for edit button
                rowView.findViewById<Button>(R.id.btnEdit).setOnClickListener {
                    editWorkout(workout)
                }

                //open listener for play button
                rowView.findViewById<Button>(R.id.btnPlay).setOnClickListener {
                    startWorkout(workout)
                }
            }
        }

        if(ll_MyWorkouts.size == 0 && !favOnly){
            val view = TextView(context)
            view.text = "You have no workouts yet, click on the create button to create a new workout."
            ll_MyWorkouts.addView(view)
            btnCombo.isEnabled = false
        }else{
            btnCombo.isEnabled = true
        }
//        if(listOfWorkouts.size == 0 && !favOnly){
//            val view = TextView(context)
//            view.text = "You have no com.com.com.workouts.workouts.com.workouts.workouts.com.com.workouts.workouts.com.workouts.workouts yet, click on the create button to create a new workout."
//            rv_MyWorkouts.addView(view)
//        }
    }

    //opens create fragment
    fun createNewWorkout(){
        val fragment = CreateFragment()
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }


    fun startWorkout(workout: Workout){
        val listOfWorkouts : HashSet<Workout> = getListOfWorkouts(requireActivity())
        val intent = Intent(requireContext(), PlayWorkout::class.java)
            .putExtra("WorkoutIndex", listOfWorkouts.indexOf(workout))
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    //opens edit fragment and send the index of the workout in the list
    fun editWorkout(workout: Workout){

            val listOfWorkouts : HashSet<Workout> = getListOfWorkouts(requireActivity())
            val fragmentManager = parentFragmentManager

            //send index of the workout to edit
            val bundle : Bundle  = bundleOf()
            bundle.putInt("index", listOfWorkouts.indexOf(workout))
            bundle.putBoolean("favOnlyPressed", favClickedOn)
            val fragment = CreateFragment()
            fragment.arguments = bundle

            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right)

            fragmentTransaction.replace(R.id.frameLayout, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()



    }


    //opens workout details when clicking on the workout name
    fun openWorkoutDetails(workout: Workout, rowView: View){
       // val ll_MyWorkouts = requireView().findViewById<LinearLayout>(R.id.ll_MyWorkouts)

        val exerciseAdapter = ExerciseAdapter(workout.exercisesAndTimes)

        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rvView: View = inflater.inflate(R.layout.workout_details, null)

        //ll_MyWorkouts!!.addView(rvView, ll_MyWorkouts.indexOfChild(rowView) + 1)
        //rv_MyWorkouts.addView(rvView, rv_MyWorkouts.indexOfChild(rowView) + 1)

        rowView.findViewById<FrameLayout>(R.id.rv_container).addView(rvView)

        val rv_workoutExercises : RecyclerView = rvView.findViewById(R.id.rv_workoutExercises)

        rv_workoutExercises.adapter = exerciseAdapter
        rv_workoutExercises.layoutManager = LinearLayoutManager(requireContext())

        workoutWithDetailsOpen = workout
        viewWithDetailsOpen = rowView

    }

    fun closeWorkoutDetails(workout: Workout?, rowView: View?): Boolean{
        if (workout!= null && rowView!= null){
            rowView.findViewById<FrameLayout>(R.id.rv_container).removeAllViews()
//            ll_MyWorkouts.removeViewAt(ll_MyWorkouts.indexOfChild(rowView) + 1)
//            rv_MyWorkouts.removeViewAt(rv_MyWorkouts.indexOfChild(rowView) + 1)
            workoutWithDetailsOpen = null
            viewWithDetailsOpen = null
            return true
        }
        return false
    }

}