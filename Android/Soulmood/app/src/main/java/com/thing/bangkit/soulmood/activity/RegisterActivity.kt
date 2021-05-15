package com.thing.bangkit.soulmood.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.databinding.ActivityRegisterBinding
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.viewmodel.RegisterViewModel
import es.dmoral.toasty.Toasty

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
            btnRegister.setOnClickListener {
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()


                if (name.isEmpty()) etName.error = getString(R.string.enter_your_name)
                if (email.isEmpty()) etEmail.error = getString(R.string.enter_your_email)
                if (password.isEmpty()) etPassword.error = getString(R.string.enter_your_pass)
                if (gender.isEmpty()) Toasty.error(
                    this@RegisterActivity,
                    getString(R.string.choose_your_gender),
                    Toasty.LENGTH_SHORT
                ).show()

                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && gender.isNotEmpty()) {

                    val alerDialog = MyAsset.sweetAlertDialog(this@RegisterActivity, getString(R.string.loading), false)
                    alerDialog.show()
                    //insert data user
                    registerViewModel.setData(
                        name,
                        email,
                        password,
                        gender,
                        this@RegisterActivity
                    ).observe(this@RegisterActivity, { status ->
                        if (status) {
                            alerDialog.setTitleText(getString(R.string.success)).hideConfirmButton().changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                            Handler(mainLooper).postDelayed({
                                alerDialog.dismiss()
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                finish()
                            }, 2000)
                        }else{
                            alerDialog.dismiss()
                        }
                    })
                }
            }


            tvLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }

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


        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}