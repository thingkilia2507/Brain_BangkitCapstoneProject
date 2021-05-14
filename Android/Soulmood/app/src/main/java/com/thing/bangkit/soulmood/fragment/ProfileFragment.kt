package com.thing.bangkit.soulmood.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.activity.LoginActivity
import com.thing.bangkit.soulmood.databinding.FragmentProfileBinding
import com.thing.bangkit.soulmood.helper.IProgressResult
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.viewmodel.ChangePasswordViewModel
import com.thing.bangkit.soulmood.viewmodel.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(), IProgressResult {

    private lateinit var binding: FragmentProfileBinding
    private val changePassViewModel: ChangePasswordViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var sweetAlertDialog: SweetAlertDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            context?.let {
                tvNameProfile.text = SharedPref.getPref(it, MyAsset.KEY_NAME)
                tvEmailProfile.text = SharedPref.getPref(it, MyAsset.KEY_EMAIL)
            }
            llChangeIdentity.setOnClickListener { _ ->
                context?.let {
                    val dialog = Dialog(it)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.edit_info_profile_dialog)
                    dialog.setCancelable(true)
                    dialog.show()

                    val btnSaveEditProfile: Button = dialog.findViewById(R.id.btn_save_edit_profile)
                    val etNameProfile: TextInputEditText = dialog.findViewById(R.id.et_name_profile)
                    val etEmailProfile: TextInputEditText = dialog.findViewById(R.id.et_email_profile)

                    etNameProfile.text = Editable.Factory.getInstance().newEditable(tvNameProfile.text.toString())
                    etEmailProfile.text = Editable.Factory.getInstance().newEditable(tvEmailProfile.text.toString())

                    btnSaveEditProfile.setOnClickListener { _: View? ->
                        emptyCheckedProfile(etEmailProfile, etNameProfile)

                        if (etNameProfile.text.toString()
                                .isNotEmpty() && etEmailProfile.text.toString().isNotEmpty()
                        ) {
                            onProgress()
                            profileViewModel.onChangeProfileInformation(
                                it,
                                etNameProfile.text.toString(),
                                etEmailProfile.text.toString(),
                                this@ProfileFragment
                            )
                        }

                    }

                }
            }

            llChangePassword.setOnClickListener { _ ->
                context?.let {
                    val dialog = Dialog(it)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.change_password_dialog)
                    dialog.setCancelable(true)
                    dialog.show()

                    val btnChangePassword : Button = dialog.findViewById(R.id.btn_change_password)
                    val etOldPassword : TextInputEditText = dialog.findViewById(R.id.etOld_password)
                    val tfOldPassword : TextInputLayout = dialog.findViewById(R.id.tfOldPassword)
                    val etNewPassword : TextInputEditText = dialog.findViewById(R.id.etNew_password)
                    val tfNewPassword : TextInputLayout = dialog.findViewById(R.id.tfNewPassword)
                    val etConfirmNewPassword : TextInputEditText = dialog.findViewById(R.id.etConfirmNew_password)
                    val tfConfirmNewPassword : TextInputLayout = dialog.findViewById(R.id.tfConfirmNewPassword)

                    initDialogView(etOldPassword,
                        tfOldPassword,
                        etNewPassword,
                        tfNewPassword,
                        etConfirmNewPassword,
                        tfConfirmNewPassword)

                    btnChangePassword.setOnClickListener { _: View? ->
                        emptyCheckedChangePassword(
                            etOldPassword,
                            tfOldPassword,
                            etNewPassword,
                            tfNewPassword,
                            etConfirmNewPassword,
                            tfConfirmNewPassword
                        )

                        if (etOldPassword.text.toString()
                                .isNotEmpty() && etNewPassword.text.toString()
                                .isNotEmpty() && etConfirmNewPassword.text.toString()
                                .isNotEmpty()
                        ) {
                            if (etNewPassword.text.toString() != etConfirmNewPassword.text.toString()) {
                                this@ProfileFragment.onFailure(getString(R.string.sorry_confirm_password_wrong))
                            } else {
                                onProgress()
                                changePassViewModel.onChangePassword(
                                    it,
                                    etOldPassword.text.toString(),
                                    etNewPassword.text.toString(),
                                    this@ProfileFragment
                                )

                            }
                        }
                    }

                }
            }

            llLogOut.setOnClickListener { context?.let { logoutAlert(it) }}

        }


    }

    private fun logoutAlert(mContext : Context) {
        val dialog = MyAsset.makeSweetAlertDialog(
            mContext,
            mContext.getString(R.string.log_out),
            mContext.getString(R.string.logout_message_alert),
            mContext.getString(R.string.yes),
            mContext.getString(R.string.cancel)
        )
        dialog.setCancelClickListener { sweetAlertDialog: SweetAlertDialog -> sweetAlertDialog.dismiss() }
        dialog.setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
            SharedPref.clearPref(mContext)
            sweetAlertDialog.setTitleText(getString(R.string.success)).hideConfirmButton()
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
            val intent = Intent(mContext, LoginActivity::class.java)
            mContext.startActivity(intent)
            sweetAlertDialog.dismiss()
        }
        dialog.show()
    }

    private fun emptyCheckedChangePassword(
        etOldPassword: TextInputEditText,
        tfOldPassword: TextInputLayout,
        etNewPassword: TextInputEditText,
        tfNewPassword: TextInputLayout,
        etConfirmNewPassword: TextInputEditText,
        tfConfirmNewPassword: TextInputLayout
    ) {
        if (etOldPassword.text.toString().isEmpty()) {
            tfOldPassword.endIconMode = TextInputLayout.END_ICON_NONE
            etOldPassword.error = getString(R.string.input_old_password)
        }
        if (etNewPassword.text.toString().isEmpty()) {
            tfNewPassword.endIconMode = TextInputLayout.END_ICON_NONE
            etNewPassword.error = getString(R.string.input_new_password)
        }
        if (etConfirmNewPassword.text.toString().isEmpty()) {
            tfConfirmNewPassword.endIconMode = TextInputLayout.END_ICON_NONE
            etConfirmNewPassword.error =
                getString(R.string.input_confirm_new_password)
        }
    }

    private fun emptyCheckedProfile(
        etEmailProfile: EditText,
        etNameProfile: EditText
    ) {
        if (etEmailProfile.text.toString().isEmpty()) {
            etEmailProfile.error = getString(R.string.input_email)
        }
        if (etNameProfile.text.toString().isEmpty()) {
            etNameProfile.error = getString(R.string.input_name)
        }
    }


    private fun initDialogView(
        etOldPassword: TextInputEditText,
        tfOldPassword: TextInputLayout,
        etNewPassword: TextInputEditText,
        tfNewPassword: TextInputLayout,
        etConfirmNewPassword: TextInputEditText,
        tfConfirmNewPassword: TextInputLayout) {
        etOldPassword.addTextChangedListener(object : TextWatcher {
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
                if (tfOldPassword.endIconMode == TextInputLayout.END_ICON_NONE) tfOldPassword.endIconMode =
                    TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }
        })

        etNewPassword.addTextChangedListener(object : TextWatcher {
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
                if (tfNewPassword.endIconMode == TextInputLayout.END_ICON_NONE) tfNewPassword.endIconMode =
                    TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }
        })

        etConfirmNewPassword.addTextChangedListener(object : TextWatcher {
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
                if (tfConfirmNewPassword.endIconMode == TextInputLayout.END_ICON_NONE) tfConfirmNewPassword.endIconMode =
                    TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }
        })
    }


    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

    override fun onProgress() {
        context?.let {
            sweetAlertDialog = MyAsset.sweetAlertDialog(
                it,
                getString(R.string.LOADING),
                false
            )
            sweetAlertDialog.show()
        }
    }

    override fun onSuccess(message: String) {
        lifecycleScope.launch(Dispatchers.Default) {
            sweetAlertDialog.setTitleText(getString(R.string.success)).setContentText(message)
                .hideConfirmButton()
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
            delay(2000)
            sweetAlertDialog.dismiss()
        }
    }

    override fun onFailure(message: String) {
        lifecycleScope.launch(Dispatchers.Default) {
            sweetAlertDialog.setTitleText(getString(R.string.error))
                .setContentText(message)
                .changeAlertType(SweetAlertDialog.ERROR_TYPE)
        }
    }
}