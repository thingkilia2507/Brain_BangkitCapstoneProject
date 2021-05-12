package com.thing.bangkit.soulmood

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.thing.bangkit.soulmood.adapter.GroupNameViewAdapter
import com.thing.bangkit.soulmood.databinding.AddNewGroupDialogBinding
import com.thing.bangkit.soulmood.databinding.FragmentHomeBinding
import com.thing.bangkit.soulmood.model.ChatGroup
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel

class HomeFragment : Fragment() {

    private val groupChatViewModel: GroupChatViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private var bindingDialog: AddNewGroupDialogBinding ?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        bindingDialog = AddNewGroupDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val adapter = GroupNameViewAdapter()
            rvGroupName.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL,false)
            rvGroupName.adapter = adapter

            adapter.setOnItemClickCallback(object : GroupNameViewAdapter.OnItemClickCallback{
                override fun onItemClick(data: ChatGroup) {
                    startActivity(Intent(context, ChatGroupActivity::class.java).apply {
                        putExtra(getString(R.string.group_id),data.id)
                        putExtra(getString(R.string.group_name),data.group_name)
                    })
                }
            })

            floatingAdd.setOnClickListener {
                context?.let {
                    val dialog = Dialog(it)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.add_new_group_dialog)
                    dialog.setCancelable(true)
                    dialog.show()

                    dialog.apply {
                        bindingDialog?.apply {
                            btnAddNewGroup.setOnClickListener {
                                val groupName = etGroupName.text.toString()
                                if (groupName.isEmpty()) etGroupName.error = getString(R.string.message_input_groupname_empty)
                                else {
                                    groupChatViewModel.insertNewGroup(groupName, context)
                                    dialog.dismiss()
                                }
                            }
                        }
                    }
                }
            }

            groupChatViewModel.setGroupName()
            groupChatViewModel.getGroupName().observe(viewLifecycleOwner, {
                if (it != null) {
                    adapter.setData(it)
                }
            })

        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

}