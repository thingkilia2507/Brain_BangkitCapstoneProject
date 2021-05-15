package com.thing.bangkit.soulmood.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.databinding.ActivityRegisterBinding
import com.thing.bangkit.soulmood.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    //view model
    private val registerViewModel: RegisterViewModel by viewModels()

    private var binding: ActivityRegisterBinding? = null
    private var gender = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //get gender from radio button
        binding?.rgGender?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_male -> {
                    gender = getString(R.string.male)
                }
                R.id.rb_female -> {
                    gender = getString(R.string.female)
                }
            }
        }



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

            btnRegister.setOnClickListener {
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()


                if (name.isEmpty()) etName.error = getString(R.string.enter_your_name)
                if (email.isEmpty()) etEmail.error = getString(R.string.enter_your_email)
                if (password.isEmpty()){
                    tfPassword.endIconMode = TextInputLayout.END_ICON_NONE
                    etPassword.error = getString(R.string.enter_your_pass)
                }
                if (gender.isEmpty()) Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.choose_your_gender),
                    Toast.LENGTH_SHORT
                ).show()

                if (name.isEmpty() && email.isEmpty() && password.isEmpty() && gender.isEmpty()) {
                    etName.error = getString(R.string.enter_your_name)
                    etEmail.error = getString(R.string.enter_your_email)
                    etPassword.error = getString(R.string.enter_your_pass)
                    tfPassword.endIconMode = TextInputLayout.END_ICON_NONE
                    Toast.makeText(
                        this@RegisterActivity,
                        getString(R.string.choose_your_gender),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && gender.isNotEmpty()) {
                    //insert data user
                    registerViewModel.setData(
                        name,
                        email,
                        password,
                        gender,
                        this@RegisterActivity
                    ).observe(this@RegisterActivity, { status ->
                        if (status) {
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    })
                }
            }

            tvLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }


        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}