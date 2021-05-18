package com.thing.bangkit.soulmood.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.alarmreceiver.AlarmReceiver
import com.thing.bangkit.soulmood.databinding.ActivityLoginBinding
import com.thing.bangkit.soulmood.helper.IProgressResult
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel
import com.thing.bangkit.soulmood.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity(), IProgressResult {
    //view model
    private  val loginViewModel:LoginViewModel by viewModels()
    private  val groupChatViewModel:GroupChatViewModel by viewModels()
    private lateinit var alerDialog: SweetAlertDialog
    var binding :ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {

            etPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
                override fun afterTextChanged(s: Editable) {
                    if (tfPassword.endIconMode == TextInputLayout.END_ICON_NONE) tfPassword.endIconMode =
                        TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }
            })

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (email.isEmpty()) etEmail.error = getString(R.string.enter_your_email)
                if (password.isEmpty()) {
                    tfPassword.endIconMode = TextInputLayout.END_ICON_NONE
                    etPassword.error = getString(R.string.input_old_password)
                }

                if(email.isNotEmpty() && password.isNotEmpty()){
                    onProgress()
                    loginViewModel.login(email, password, this@LoginActivity,
                        this@LoginActivity).observe(this@LoginActivity, {
                            if (it != null) {
                                Log.d("TAGDATAKU", "onCreate: notnull")
                                //save data to shared preference
                                SharedPref.setPref(
                                    this@LoginActivity,
                                    MyAsset.KEY_NAME,
                                    it.name
                                )
                                SharedPref.setPref(
                                    this@LoginActivity,
                                    MyAsset.KEY_EMAIL,
                                    it.email
                                )
                              
                                SharedPref.setPref(
                                    this@LoginActivity,
                                    MyAsset.KEY_USER_ID,
                                    it.id
                                )

                                //set dialy motivation word repeat alarm
                                groupChatViewModel.getQuoteOfTheDay().observe(this@LoginActivity,{ it ->
                                    Log.d("TAGDATAKU", "onCreatequotes: "+it)
                                    it?.let{
                                        AlarmReceiver().setRepeatingAlarmMotivationWord(
                                            this@LoginActivity,
                                            it
                                        )
                                    }
                                })

                            }
                        })
                }
            }


            tvRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }

            tvForgotPassword.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        ForgotPasswordActivity::class.java
                    )
                )
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onProgress() {
        alerDialog = MyAsset.sweetAlertDialog(this@LoginActivity, getString(R.string.loading), false)
        alerDialog.show()
    }

    override fun onSuccess(message: String) {
        alerDialog.setTitleText(getString(R.string.success)).hideConfirmButton().changeAlertType(
            SweetAlertDialog.SUCCESS_TYPE)

        Handler(mainLooper).postDelayed({
            alerDialog.dismiss()
            //intent
            startActivity(
                Intent(
                    this@LoginActivity,
                    MainActivity::class.java
                )
            )
            finish()
        }, 2000)
    }

    override fun onFailure(message: String) {
        alerDialog.dismiss()
    }
}