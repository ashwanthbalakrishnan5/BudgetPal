package com.solvingforIndia.BudgetPal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.transition.TransitionInflater
import com.google.android.material.textfield.TextInputEditText
import com.solvingforIndia.BudgetPal.LoginActivity
import com.solvingforIndia.BudgetPal.R


class LoginFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //bind the login button to the activity login method
        view.findViewById<AppCompatButton>(R.id.login_button).setOnClickListener {
            val username = view.findViewById<EditText>(R.id.username)
            val password = view.findViewById<EditText>(R.id.password)
            (activity as LoginActivity).login(username.text.toString(),password.text.toString())
        }
        view.findViewById<AppCompatButton>(R.id.sign_up_button).setOnClickListener {
            val username = view.findViewById<EditText>(R.id.username)
            val password = view.findViewById<EditText>(R.id.password)
            (activity as LoginActivity).signup(username.text.toString(),password.text.toString())
        }

    }
}