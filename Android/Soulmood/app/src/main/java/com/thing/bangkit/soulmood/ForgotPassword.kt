package com.thing.bangkit.soulmood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.thing.bangkit.soulmood.databinding.ActivityForgotPasswordBinding
import com.thing.bangkit.soulmood.viewmodel.LoginViewModel

class ForgotPassword : AppCompatActivity() {
    private  val loginViewModel: LoginViewModel by viewModels()
    private var binding : ActivityForgotPasswordBinding ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            btnChangePass.setOnClickListener {
                val email = etEmail.text.toString()
                if(email.isEmpty()) etEmail.error = getString(R.string.enter_your_email)
                else{
                    loginViewModel.forgotPassword(email,this@ForgotPassword).observe(this@ForgotPassword,
                        { status ->
                            if (status) {
                                Toast.makeText(this@ForgotPassword, "Kami telah mengirim link ubah password" +
                                        "pada Email Anda", Toast.LENGTH_SHORT).show()
                                etEmail.text.clear()
                            }
                        })
                }
            }

            ivBack.setOnClickListener { onBackPressed() }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}