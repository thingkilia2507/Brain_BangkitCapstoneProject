package com.thing.bangkit.soulmood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.thing.bangkit.soulmood.databinding.ActivityRegisterBinding
import com.thing.bangkit.soulmood.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    //view model
    private  val registerViewModel:RegisterViewModel by viewModels()

    private var binding : ActivityRegisterBinding? = null
    private var gender =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //get gender from radio button
        binding?.rgGender?.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_male -> {
                    gender = getString(R.string.male)
                }
                R.id.rb_female ->{
                    gender = getString(R.string.female)
                }
            }
        }



        binding?.apply {
            btnRegister.setOnClickListener {
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                when{
                    name.isEmpty() ->  etName.error = getString(R.string.enter_your_name)
                    email.isEmpty() -> etEmail.error = getString(R.string.enter_your_email)
                    password.isEmpty() -> etPassword.error = getString(R.string.enter_your_pass)
                    gender.isEmpty() ->Toast.makeText(this@RegisterActivity, getString(R.string.choose_your_gender), Toast.LENGTH_SHORT).show()
                    name.isEmpty() && email.isEmpty() && password.isEmpty() && gender.isEmpty() ->{
                        etName.error = getString(R.string.enter_your_name)
                        etEmail.error = getString(R.string.enter_your_email)
                        etPassword.error = getString(R.string.enter_your_pass)
                        Toast.makeText(this@RegisterActivity, getString(R.string.choose_your_gender), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        registerViewModel.setData(name,email,password,gender,this@RegisterActivity)
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}