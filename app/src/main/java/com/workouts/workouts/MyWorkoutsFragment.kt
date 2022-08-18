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
import com.workouts.DBHelper.DBHelper
import com.workouts.DTOs.*
import kotlinx.android.synthetic.main.fragment_my_workouts.*


class MyWorkoutsFragment : Fragment() {

    private lateinit var db : DBHelper
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

        db = DBHelper(requireContext())

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //sets listener for favorite button, show only favorite workouts
        btnFavorites.setOnClickListener {
            favClickedOn = !favClickedOn
            if(comboClickedOn){ //closes create combo mode if it was on without animation
                comboClickedOn = false
                close_createCombo(false)
                //view.postDelayed({showOnlyFav()},900)
            }
            showOnlyFav()
        }

        //switch to create combo mode
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

    /**
     * close create combo mode
     * @param false for closing without animation
     */
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

    /**
     * switches select buttons to favorite button with animation
     */
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
                val nameOfWorkout = item.findViewById<TextView>(R.id.workoutName).text.toString()
                if(favClickedOn || db.WORKOUTS.getWorkoutIsFavorite(nameOfWorkout)){
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

    /**
     * switches create button mode
     */
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

    /**
     * swiches to create combo mode
     */
    fun createCombo(){

        comboClickedOn = true
        //val listOfWorkouts : HashSet<Workout> = getListOfWorkouts(requireActivity())
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

            newCombo = Combo("name", mutableListOf()) //todo add text vie to enter name in the dialog

            //animate changing the create button
            changeBtnCreateAnimation("create combo", R.drawable.create)

            btnAddToCombo.setOnClickListener { //numbers the buttons and adds to newCombo
                addToCombo(item)
            }

            btnCreate.setOnClickListener{ //opens create combo dialog
                if (newCombo == null || newCombo!!.workouts.size == 0){ //if no workouts selected
                    Toast.makeText(context,"please select at least one workout",Toast.LENGTH_SHORT).show()
                }else{
                    saveCombo()
                }
            }
        }
    }

    /**
     * adds selected workout to combo and modifies the position
     * @param the item chosen
     */
    fun addToCombo(item : View){

        //adds the workout to the combo
        val btnAddToCombo = item.findViewById<Button>(R.id.btnAddToCombo)
        val currWorkoutId : Int = db.WORKOUTS.getWorkoutId(item.findViewById<TextView>(R.id.workoutName).text.toString())!!
        if(btnAddToCombo.text.equals("")){ //it hasn't been selected yet
            newCombo?.addWorkoutId(""+currWorkoutId)
            position++
            btnAddToCombo.text = "$position"
        }else{
            btnAddToCombo.text = ""
            position--
            newCombo!!.removeWorkoutId(""+currWorkoutId)
            for (i in ll_MyWorkouts){ //organizes the indexes
                val btnAddToCombo2 = i.findViewById<Button>(R.id.btnAddToCombo)
                if(!(btnAddToCombo2.text.equals(""))){
                    val currWorkoutName = i.findViewById<TextView>(R.id.workoutName).text.toString()
                    val currWorkoutId2 = db.WORKOUTS.getWorkoutId( currWorkoutName)
                    val index = newCombo!!.workouts.indexOf(""+currWorkoutId2)
                    btnAddToCombo2.text = ""+ (index+1)
                }
            }
        }
    }

    /**
     * opens start combo dialog. saves combo in db.
     * @pre newCombo != null && newCombo.workouts.size() > 0
     */
    fun saveCombo(){
        if(newCombo!=null){
            val dialog = MaterialDialog(requireContext())
                .customView(R.layout.create_combo_dialog)

            val ll_selectedWorkouts = dialog.findViewById<LinearLayout>(R.id.ll_selectedWorkouts)
            //inflates selected workouts views
            for(workoutId in newCombo!!.workouts){
                val inflater =
                    requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView: View = inflater.inflate(R.layout.workout_selected_for_combo, null)

                val workoutName : String? = db.WORKOUTS.getWorkoutName( Integer.parseInt(workoutId))
                rowView.findViewById<TextView>(R.id.S_workoutName).text = workoutName
                rowView.findViewById<TextView>(R.id.S_workoutTime).text = db.WORKOUTS.getTotalTimeOfWorkout( workoutName!!)
                ll_selectedWorkouts.addView(rowView,ll_selectedWorkouts.childCount)
            }

            //saves the combo and plays it
            dialog.findViewById<Button>(R.id.btnPlayCombo).setOnClickListener {

                newCombo!!.name = dialog.findViewById<EditText>(R.id.et_NewComboName).text.toString()
                if(newCombo!!.name.equals("")){
                    Toast.makeText(requireContext(), "please enter a name for the combo", Toast.LENGTH_SHORT).show()
                }
                else if(!db.COMBOS.getComboNames().contains(newCombo!!.name)) {
                    //save combo in db
                    db.COMBOS.addCombo(newCombo!!)

                    //val listOfCombos : HashSet<Combo> = getListOfCombos()
                    val intent = Intent(requireContext(), PlayCombo::class.java).putExtra("ComboName", newCombo!!.name)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                    close_createCombo()
                    dialog.cancel()
                }else{
                    Toast.makeText(requireContext(), "this combo name already exists", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }
    }

    /**
     * loads favorites only
     */
    fun showOnlyFav(){
        if(favClickedOn){
            btnFavorites.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.favorite)
            update(true)
        }else{
            btnFavorites.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.favorite_border)
            update(false)
        }

    }

    /**
     * loads the workouts from shared prefs and opens listeners
     */
     fun update(favOnly : Boolean = false){

        var listOfWorkouts : HashSet<Workout>
        val ll_MyWorkouts = requireView().findViewById<LinearLayout>(R.id.ll_MyWorkouts)
        ll_MyWorkouts.removeAllViews()

        if (favOnly){
            //listOfWorkouts = getListOfFavoriteWorkouts(requireActivity())
            listOfWorkouts = db.WORKOUTS.getFavoriteWorkouts()
        }else{
            //listOfWorkouts = getListOfWorkouts(requireActivity())
            listOfWorkouts = db.WORKOUTS.getAllWorkouts()
        }
//
//        val workoutAdapter = WorkoutAdapter(listOfWorkouts,favOnly, requireContext(), requireActivity())
//
//        rv_MyWorkouts.adapter = workoutAdapter
//        rv_MyWorkouts.layoutManager = LinearLayoutManager(requireContext())

        //adds views with saved workouts details
        for (workout in listOfWorkouts){
            if((favOnly and workout.isFavorite) or !favOnly) {
                val inflater =
                    requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView: View = inflater.inflate(R.layout.new_workout, null)

                rowView.findViewById<Button>(R.id.btnAddToCombo).isVisible = false
                rowView.findViewById<TextView>(R.id.tv_fav).isVisible = workout.isFavorite

//                val name: String = workout.name
//                val totalTime: String = workout.totalTime
                rowView.findViewById<TextView>(R.id.workoutName).text = workout.name
                rowView.findViewById<TextView>(R.id.totalTime).text = db.WORKOUTS.getTotalTimeOfWorkout(workout.name)

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

    /**
     * opens create fragment
     */
    fun createNewWorkout(){
        val fragment = CreateFragment()
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    /**
     * opens the play workout activity, sends the workout name.
     */
    fun startWorkout(workout: Workout){
        val intent = Intent(requireContext(), PlayWorkout::class.java)
            .putExtra("WorkoutName", workout.name)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    /**
     * opens edit fragment and sends the workouts name, and favOnlyPressed.
     */
    fun editWorkout(workout: Workout){

            val fragmentManager = parentFragmentManager

            //send name of the workout to edit
            val bundle : Bundle  = bundleOf()
            bundle.putString("workoutName", workout.name)
            bundle.putBoolean("favOnlyPressed", favClickedOn) //todo need this?
            val fragment = CreateFragment()
            fragment.arguments = bundle

            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right)

            fragmentTransaction.replace(R.id.frameLayout, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
    }

    /**
     * opens workout details when clicking on the workout name
     */
    fun openWorkoutDetails(workout: Workout, rowView: View){
       // val ll_MyWorkouts = requireView().findViewById<LinearLayout>(R.id.ll_MyWorkouts)

        var lstOfExercises: MutableList<Exercise>? = db.getExercisesOfWorkout(workout.name)

        val exerciseAdapter = ExerciseAdapter(lstOfExercises!!)

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