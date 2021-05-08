package com.thing.bangkit.soulmood

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.thing.bangkit.soulmood.databinding.ActivityLoginBinding
import com.thing.bangkit.soulmood.utils.SharedPref
import com.thing.bangkit.soulmood.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    //view model
    private  val loginViewModel:LoginViewModel by viewModels()
    var binding :ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            var visibility = false
            ivEyePass.setOnClickListener {
                if (visibility){
                    visibility = false
                    etPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    etPassword.setSelection(etPassword.text.length)
                    ivEyePass.setImageResource(R.drawable.ic_baseline_visibility_24)

                }else{
                    visibility = true
                    etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    etPassword.setSelection(etPassword.text.length)
                    ivEyePass.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                }
            }

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if(email.isEmpty())etEmail.error = getString(R.string.enter_your_email)
                else if (password.isEmpty())etPassword.error = getString(R.string.enter_your_pass)
                else if (email.isEmpty() && password.isEmpty()){
                    etEmail.error = getString(R.string.enter_your_email)
                    etPassword.error = getString(R.string.enter_your_pass)
                }
                else{
                    loginViewModel.login(email, password, this@LoginActivity).observe(this@LoginActivity,
                        {
                            if (it != null) {
                                //save data to shared preference
                                SharedPref().setPref(
                                    this@LoginActivity,
                                    getString(R.string.name),
                                    it.name
                                )
                                SharedPref().setPref(
                                    this@LoginActivity,
                                    getString(R.string.email),
                                    it.email
                                )
                                SharedPref().setPref(
                                    this@LoginActivity,
                                    getString(R.string.user_id),
                                    it.id
                                )

                                //intent
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        AcitivtyUntukCobaCoba::class.java
                                    )
                                )
                                finish()
                            }
                        })
                }
            }


            tvRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }

            tvForgotPassword.setOnClickListener { startActivity(
                Intent(
                    this@LoginActivity,
                    ForgotPassword::class.java
                )
            ) }

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}