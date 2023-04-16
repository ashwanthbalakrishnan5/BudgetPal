package com.solvingforIndia.BudgetPal.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.*
import android.transition.Fade
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnticipateOvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionInflater
import com.solvingforIndia.BudgetPal.R

class OnBoarding1 : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.left_slide)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // animate the elements of the fragment
        val alpha1 = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        val pos1 = PropertyValuesHolder.ofFloat("translationY", 100f, 0f)
        val startButton = view.findViewById<View>(R.id.startButton)
        val title = view.findViewById<View>(R.id.textView)
        val subtitle = view.findViewById<View>(R.id.textView2)
        ObjectAnimator.ofPropertyValuesHolder(startButton, alpha1, pos1).apply {
            duration = 750
            interpolator = AnticipateOvershootInterpolator()
            start()
        }
        ObjectAnimator.ofPropertyValuesHolder(title, alpha1, pos1).apply {
            duration = 1000
            interpolator = AnticipateOvershootInterpolator()
            start()
        }
        ObjectAnimator.ofPropertyValuesHolder(subtitle, alpha1, pos1).apply {
            duration = 1000
            interpolator = AnticipateOvershootInterpolator()
            start()
        }
        // bind the start button to the next fragment
        startButton!!.setOnClickListener {
            this.vibrate("click")
            val fragment = LoginFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.addSharedElement(startButton, "cta_button")
            transaction.replace(R.id.fragment_container_view, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun vibrate(type: String) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                activity?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") activity?.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (type == "click") {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        30, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else if (type == "error") {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        30, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        } else {
            if (type == "click") {
                vibrator.vibrate(30)
            } else if (type == "error") {
                vibrator.vibrate(50)
            }
        }
    }
}