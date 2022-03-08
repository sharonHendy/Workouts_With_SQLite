package com.workouts.workouts

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.gson.Gson
import com.workouts.objects.Workout
import kotlinx.android.synthetic.main.fragment_create.*


class CreateFragment : Fragment() {

    private  var workout : Workout? = null
    private var changed : Boolean = false
    private var favOnlyPressed : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sends bundle from myWorkouts to edit a workout
        val bundle = this.arguments
        if(bundle != null){

            //sets the workout info to edit
            val index = bundle.getInt("index",0)
            favOnlyPressed = bundle.getBoolean("favOnlyPressed", false)
            if(favOnlyPressed){
                workout = getListOfFavoriteWorkouts(requireActivity()).elementAt(index)
            }else{
                workout = getListOfWorkouts(requireActivity()).elementAt(index)
            }

            view.findViewById<EditText>(R.id.et_NewWorkoutName).setText((workout as Workout).name)
            var i : Int = 0
            for(exercise in (workout as Workout).exercisesAndTimes){
                val view : View = onAddField()
                view.findViewById<EditText>(R.id.exerciseName).setText(exercise.name)
                view.findViewById<Button>(R.id.btnTime).text = exercise.time
                i++
            }

            btnDel.isVisible = true
            btnDel.isEnabled = true
            btnIsFavorite.isVisible = true
            btnIsFavorite.isEnabled = true
            if (workout!!.isFavorite){
                btnIsFavorite.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.favorite)
            }

            btnDel.setOnClickListener {
                showDelDialog()
            }

            btnIsFavorite.setOnClickListener {
                workout!!.isFavorite = !workout!!.isFavorite
                if(workout!!.isFavorite){
                    btnIsFavorite.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.favorite)
                }else{
                    btnIsFavorite.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.favorite_border)
                }
            }


        }else{ //if it is not an edit call
            btnDel.isVisible = false
            btnDel.isEnabled = false
            btnIsFavorite.isVisible = false
            btnIsFavorite.isEnabled = false
            onAddField()
            onAddField()
        }

        val btnSave: Button = view.findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            changed = true
            saveData()
        }

        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            onAddField()
        }

        btnback.setOnClickListener{
            cancel()
        }


    }


    /**
     * deletes the workout from memory
     */
    fun deletedWorkout(workout: Workout){

        val listOfWorkouts : HashSet<Workout> = getListOfWorkouts(requireActivity())
        listOfWorkouts.remove(workout)
        val listOfWorkoutsNames = getListOfWorkoutsNames(requireActivity())
        listOfWorkoutsNames.remove(workout.name)

        //saves changes to shared prefs
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val jsonString1 = Gson().toJson(listOfWorkouts)
        val jsonString2 = Gson().toJson(listOfWorkoutsNames)

        editor.putString("ListOfWorkouts", jsonString1)
        editor.putString("ListOfWorkoutsNames",jsonString2)


        if(workout.isFavorite){
            val listOfFavWorkout = getListOfFavoriteWorkouts(requireActivity())
            listOfFavWorkout.remove(workout)
            val jsonString3 = Gson().toJson(listOfFavWorkout)
            editor.putString("ListOfFavoriteWorkouts", jsonString3)
        }

        editor.apply()

        Toast.makeText(requireContext(), "deleted workout", Toast.LENGTH_SHORT).show()

    }

    /**
     * opens delete dialog
     */
    fun showDelDialog(){
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.delete_dialog)
        dialog.findViewById<Button>(R.id.btnYes).setOnClickListener {
            changed = true
            deletedWorkout(workout!!)
            dialog.cancel()
            cancel()
        }
        dialog.findViewById<Button>(R.id.btnNo).setOnClickListener {
            dialog.cancel()
        }
        dialog.show()
    }

    /**
     * goes back to myWorkouts, refreshes myWorkouts only if save button has been pressed
     */
    fun cancel(){
        if (changed){
            val fragment = parentFragmentManager.findFragmentByTag("f0") //default fragment tag
            (fragment as MyWorkoutsFragment).update(favOnlyPressed)
        }
        activity?.onBackPressed()
    }

    /**
     * sets auto complete for exercises name fields.
     * @param index of the exercise view
     */
    fun setAutoComplete(i : Int){
        val ll_fields = requireView().findViewById<LinearLayout>(R.id.ll_fields)
        val exercises : Array<String> = getListOfExercises(requireActivity()).toTypedArray()
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,exercises)
        val autoTextView = ll_fields.getChildAt(i).findViewById<AutoCompleteTextView>(R.id.exerciseName)
        autoTextView.setAdapter(adapter)
        autoTextView.threshold = 1
    }

    /**
     * removes exercise's view.
     * @param index of the exercise view
     */
    fun deleteField(index: Int) {
        val ll_fields = requireView().findViewById<LinearLayout>(R.id.ll_fields)
        ll_fields.removeView(ll_fields.getChildAt(index))
    }

    /**
     * adds field to enter another exercise.
     * @return new exercise view with buttons set with listeners
     */
    fun onAddField() : View {
        val rowView: View = LayoutInflater.from(context).inflate(R.layout.field, null)
        val ll_fields = requireView().findViewById<LinearLayout>(R.id.ll_fields)
        ll_fields!!.addView(rowView, ll_fields!!.childCount)

        openBtnTimeListener(ll_fields.childCount - 1)
        openBtnDeleteListener(ll_fields.childCount - 1)
        setAutoComplete(ll_fields.childCount - 1)
        return rowView
    }

    /**
     * opens listeners for time button.
     * @param index of the field
     */
    fun openBtnTimeListener(i: Int) {
        val ll_fields = requireView().findViewById<LinearLayout>(R.id.ll_fields)
        val ll_singleField = ll_fields.getChildAt(i)
        if (ll_singleField is ConstraintLayout) {
            val btnTime = ll_singleField.findViewById<Button>(R.id.btnTime)
            if (btnTime is Button) {
                btnTime.setOnClickListener {
                    showTimeDialog(ll_fields.indexOfChild(ll_singleField))
                }
            }

        }

    }

    /**
     * opens listeners for delete button.
     * @param index of the field
     */
    fun openBtnDeleteListener(i: Int) {
        val ll_fields = requireView().findViewById<LinearLayout>(R.id.ll_fields)
        val ll_singleField = ll_fields.getChildAt(i)
        if (ll_singleField is ConstraintLayout) {
            val btnDelete = ll_singleField.findViewById<Button>(R.id.btnDelete)
            if (btnDelete is Button) {
                btnDelete.setOnClickListener {
                    deleteField(ll_fields.indexOfChild(ll_singleField))
                }
            }

        }
    }

    /**
     * opens and sets the time for the exercises
     */
    fun showTimeDialog(i: Int) {
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.time_dialog)

        val minute_picker: NumberPicker = dialog.findViewById(R.id.minute_picker)
        minute_picker.maxValue = 59
        minute_picker.minValue = 0

        val second_picker: NumberPicker = dialog.findViewById(R.id.second_picker)
        second_picker.maxValue = 59
        second_picker.minValue = 0

        val btnSetTime: Button = dialog.findViewById(R.id.btnSetTime)
        btnSetTime.setOnClickListener {
            //displays the time chosen on the time button
            val minutes: Int = minute_picker.value
            val seconds: Int = second_picker.value

            val ll_fields = requireView().findViewById<LinearLayout>(R.id.ll_fields)
            val ll_singleField = ll_fields.getChildAt(i)
            if (ll_singleField is ConstraintLayout) {
                val btnTime = ll_singleField.findViewById<Button>(R.id.btnTime)
                if (btnTime is Button) {
                    btnTime.text = padd(minutes) + " : " + padd(seconds)

                }
            }
            dialog.cancel()
        }

        dialog.show()
    }

    private fun padd(time :Int) : String{
        if(time / 10 == 0)
            return "0" + time
        return "" + time
    }

    /**
     * saves the workout detail provided by the user.
     */
    fun saveData(){

        if(checkFields()){
            val sharedPreferences: SharedPreferences =
                requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()

            val listOfWorkouts: HashSet<Workout> = getListOfWorkouts(requireActivity())
            val listOfFavoriteWorkouts = getListOfFavoriteWorkouts(requireActivity())
            val listOfWorkoutsNames : HashSet<String> = getListOfWorkoutsNames(requireActivity())

            val newWorkoutName: String = requireView().findViewById<EditText>(R.id.et_NewWorkoutName).text.toString()
            var newWorkout : Workout = Workout(newWorkoutName)

            if(workout != null){ //(means its an edit call) removes the old workout and its name from shared prefs
                newWorkout.isFavorite = workout!!.isFavorite
                listOfFavoriteWorkouts.remove(workout)
                listOfWorkoutsNames.remove(workout!!.name)
                listOfWorkouts.remove(workout)
            }

            if(newWorkout.isFavorite){ // adds to favorite workouts list
                listOfFavoriteWorkouts.add(newWorkout)
            }

            listOfWorkoutsNames.add(newWorkoutName)

            //adds the exercises to the workout
            val listOfExercises = getListOfExercises(requireActivity())
            val ll_fields: LinearLayout = requireView().findViewById(R.id.ll_fields)
            var j = 0
            for (field in ll_fields.children) {
                if (field is ConstraintLayout) {
                    val name: String = field.findViewById<EditText>(R.id.exerciseName).text.toString()
                    val time: String = field.findViewById<Button>(R.id.btnTime).text.toString()
                    newWorkout.addExercise(name, time)
                    listOfExercises.add(name) //for auto complete
                    j++
                }
            }

            listOfWorkouts.add(newWorkout)


            val jsonString3 = Gson().toJson(listOfWorkoutsNames)
            val jsonString = Gson().toJson(listOfWorkouts)
            val jsonString2 = Gson().toJson(listOfExercises)
            editor.putString("ListOfWorkouts", jsonString)
            editor.putString("ListOfExercises", jsonString2)
            editor.putString("ListOfWorkoutsNames",jsonString3)
            val jsonStr = Gson().toJson(listOfFavoriteWorkouts)
            editor.putString("ListOfFavoriteWorkouts",jsonStr)
            editor.apply()

            Toast.makeText(requireContext(), "saved workout", Toast.LENGTH_SHORT).show()
            cancel()
        }
    }

    /**
     * checks all fields are filled
     */
    fun checkFields() : Boolean{
        var output = true
        if(requireView().findViewById<EditText>(R.id.et_NewWorkoutName).text.toString().equals("")){
            output = false
        }else{
            val ll_fields: LinearLayout = requireView().findViewById(R.id.ll_fields)
            //var j = 0
            if (ll_fields.childCount == 0){
                Toast.makeText(requireContext(), "workout has to have at least one exercise", Toast.LENGTH_SHORT).show()
                return false
            }
            for (field in ll_fields.children) {
                if (field is ConstraintLayout) {
                    val name: String = field.findViewById<EditText>(R.id.exerciseName).text.toString()
                    val time: String = field.findViewById<Button>(R.id.btnTime).text.toString()
                    if (name.equals("")){
                        output = false
                        break
                    }
                    if(time.equals("0 : 0")) {
                        output = false
                        break
                        //j++
                    }
                }
            }
        }
        if (!output){
            Toast.makeText(requireContext(), "please fill in all the fields", Toast.LENGTH_SHORT).show()
        }
        return output
    }

}