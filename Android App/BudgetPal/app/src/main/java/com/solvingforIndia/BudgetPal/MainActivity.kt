package com.solvingforIndia.BudgetPal

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup


class MainActivity : AppCompatActivity() {

    var chatview: WebView? = null
    var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        // check if the user is already logged in using shared Prefereneces
        val settings = getSharedPreferences("config", 0)
        val isLoggedIn = settings.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            // if the user is logged in, then go to the home screen
            this.username = settings.getString("username", "")!!
            setContentView(R.layout.activity_main)
            this.chatview = findViewById(R.id.chatview)
            this.setupChatView()
        } else {
            // if the user is not logged in, then launch the login Activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    fun setupChatView() {
        // this function sets up the chat view
        this.chatview?.settings?.javaScriptEnabled = true
        this.chatview?.addJavascriptInterface(WebAppInterface(this, username, this.chatview!!, this), "Android")
        this.chatview?.settings?.setDomStorageEnabled(true);
        this.chatview?.loadUrl("file:///android_asset/index.html")


    }
}

class WebAppInterface(private val mContext: Context, private val username: String, private val webview: WebView, private val activity: MainActivity) {

    var mInput: String = ""

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun getResponse(input: String): String {
        // send the request to the backend and get the response
        mInput = input
        var result = backend()
        return JSONObject(result.body()).getString("reply")
    }

    fun backend(): Connection.Response{x`
        val data = JSONObject(
            mapOf("username" to username, "message" to mInput)).toString()
        return Jsoup.connect("https://asia-south1-solvingforindia.cloudfunctions.net/detectintent").requestBody(data).header( "Content-Type", "application/json").method(
            Connection.Method.POST).execute()
    }

    @JavascriptInterface
    fun showBudgetSet(){
        // this function will show the budget set popup
        activity.runOnUiThread{
        val alertDialog: AlertDialog? = activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Choose a Cateogry you want to set a budget for")
                .setItems(
                    arrayOf("Food", "Transport", "Housing", "Shopping", "Others"),
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which){
                            0 -> {
                                dialog.dismiss()
                                setBudget("Food")

                            }
                            1 -> {
                                dialog.dismiss()
                                setBudget("Transport")
                            }
                            2 -> {
                                dialog.dismiss()
                                setBudget("Housing")
                            }
                            3 -> {
                                dialog.dismiss()
                                setBudget("Shopping")
                            }
                            4 -> {
                                dialog.dismiss()
                                setBudget("Others")
                            }
                        }
                    })
            builder.create()
        }
        alertDialog?.show()
        }
    }

    fun setBudget(type:String,){
        val numberPicker = NumberPicker(activity)
        numberPicker.minValue = 0
        numberPicker.maxValue = 100000

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select the budget for $type")
        builder.setView(numberPicker)

        builder.setPositiveButton("OK") { dialog, which ->
            // User clicked OK button
            val selectedNumber = numberPicker.value
            setBudgetBackend(type, selectedNumber)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            // User cancelled the dialog
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun setBudgetBackend(type:String, budget:Int){
        // this function will set the budget in Backend
        Thread{
            val data = JSONObject(
                mapOf("username" to username, "category_name" to type, "budget_value" to budget)).toString()
            val response = Jsoup.connect("https://asia-south1-solvingforindia.cloudfunctions.net/setbudget ").requestBody(data).header( "Content-Type", "application/json").method(
                Connection.Method.POST).execute()
            activity.runOnUiThread{
                Toast.makeText(mContext, "Budget set to ${budget}", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }
}