package com.example.dataloggerextended.ui.view

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dataloggerextended.R
import com.example.dataloggerextended.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cambiar el color del SupportActionBar
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_black)))
        supportActionBar!!.title = ""

        //Agrego un boton de regreso al MainActivity y cambio el titulo del action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.senEmailAppCompatButton.setOnClickListener {
            val email: String = binding.emailEditText.text.toString().trim { it <= ' '}
            if (email.isEmpty()){
                Toast.makeText(this@ForgotPasswordActivity, "Please enter email address", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {task ->
                    if(task.isSuccessful){
                        Toast.makeText(this@ForgotPasswordActivity,
                            "Email sent successfully to reset your password!",
                            Toast.LENGTH_SHORT).show()

                        finish()
                    }else{
                        Toast.makeText(this@ForgotPasswordActivity,
                            task.exception!!.message.toString(),
                            Toast.LENGTH_SHORT).show()
                    }


                }
            }
        }


    }
}