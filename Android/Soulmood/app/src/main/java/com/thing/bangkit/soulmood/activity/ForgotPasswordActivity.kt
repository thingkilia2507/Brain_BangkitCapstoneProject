package com.thing.bangkit.soulmood.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.databinding.ActivityForgotPasswordBinding
import com.thing.bangkit.soulmood.viewmodel.LoginViewModel
import es.dmoral.toasty.Toasty

class ForgotPasswordActivity : AppCompatActivity() {
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
                    loginViewModel.forgotPassword(email,this@ForgotPasswordActivity).observe(this@ForgotPasswordActivity,
                        { status ->
                            if (status) {
                                Toasty.warning(this@ForgotPasswordActivity, getString(R.string.message_forgot_pass_email), Toasty.LENGTH_SHORT).show()
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