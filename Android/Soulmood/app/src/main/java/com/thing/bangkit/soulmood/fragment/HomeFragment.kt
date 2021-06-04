package com.thing.bangkit.soulmood.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.activity.*
import com.thing.bangkit.soulmood.adapter.GroupNameViewAdapter
import com.thing.bangkit.soulmood.adapter.SliderCSFAdapter
import com.thing.bangkit.soulmood.databinding.FragmentHomeBinding
import com.thing.bangkit.soulmood.helper.DateHelper
import com.thing.bangkit.soulmood.helper.IProgressResult
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.ChatGroup
import com.thing.bangkit.soulmood.model.ComingSoonFeatureSliderItem
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel
import com.thing.bangkit.soulmood.viewmodel.MoodTrackerViewModel

class HomeFragment : Fragment(), IProgressResult {
    private lateinit var sliderRunnable: Runnable
    private val groupChatViewModel: GroupChatViewModel by viewModels()
    private val moodTrackerViewModel: MoodTrackerViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val sliderHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            setDashboard()
            binding.apply {
                ivChatbot.setOnClickListener {
                    startActivity(Intent(requireActivity(), ChatbotActivity::class.java))
                }
                //chatBotViewModel.reqChatbotMessagesData(requireActivity())
                groupChatViewModel.getQuoteOfTheDay(requireActivity(), this@HomeFragment)
                    .observe(requireActivity(), {
                        it.let {
                            tvMotivationQuotes.text = StringBuilder(it)
                        }
                    })
                val adapter = GroupNameViewAdapter("homeFragment")
                rvGroupName.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL, false
                )
                rvGroupName.adapter = adapter
                adapter.setOnItemClickCallback(object : GroupNameViewAdapter.OnItemClickCallback {
                    override fun onItemClick(data: ChatGroup) {
                        startActivity(Intent(context, ChatGroupActivity::class.java).apply {
                            putExtra(getString(R.string.room_id), data.id)
                            putExtra(getString(R.string.room_name), data.group_name)
                        })
                    }
                })

                floatingAdd.setOnClickListener {
                    val dialog = Dialog(requireContext())
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.add_new_group_dialog)
                    dialog.setCancelable(true)
                    dialog.show()
                    val etGroupName: EditText = dialog.findViewById(R.id.et_group_name)
                    val btnAddNewGroup = dialog.findViewById<Button>(R.id.btn_add_new_group)
                    btnAddNewGroup.setOnClickListener {
                        val groupName = etGroupName.text.toString()
                        if (groupName.isEmpty()) etGroupName.error =
                            getString(R.string.message_input_room_name_empty)
                        else {
                            groupChatViewModel.insertNewGroup(groupName, requireActivity())
                            dialog.dismiss()
                        }
                    }


                }

                floatingSeeAll.setOnClickListener {
                    startActivity(
                        Intent(
                            requireActivity(),
                            GroupNameActivity::class.java
                        )
                    )
                }
                constraintDashboard.setOnClickListener {
                    startActivity(
                        Intent(
                            requireActivity(),
                            MoodTrackerActivity::class.java
                        )
                    )
                }
                groupChatViewModel.setGroupName(MyAsset.HOME_FRAGMENT)
                groupChatViewModel.getGroupName().observe(viewLifecycleOwner, {
                    if (it != null) {
                        adapter.setData(it)
                    }
                })

                val sliderItem = ArrayList<ComingSoonFeatureSliderItem>()
                sliderItem.add(ComingSoonFeatureSliderItem(R.drawable.psikolog_konsultasi_soulmood))
                sliderItem.add(ComingSoonFeatureSliderItem(R.drawable.soulcare))

                val compositePageTransformer = CompositePageTransformer()
                compositePageTransformer.addTransformer(MarginPageTransformer(30))
                compositePageTransformer.addTransformer { page, position ->
                    val r = 1 - kotlin.math.abs(position)
                    page.scaleY = 0.85f + r * 0.25f
                }

                binding.viewPagerComingSoonFeature.apply {
                    this.adapter = SliderCSFAdapter(sliderItem)
                    this.clipChildren = false
                    this.clipToPadding = false
                    this.offscreenPageLimit = 3
                    this.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                    this.setPageTransformer(compositePageTransformer)

                    sliderRunnable = Runnable {
                        if (this.currentItem + 1 == sliderItem.size) {
                            this.currentItem = 0
                        } else {
                            this.currentItem = this.currentItem + 1
                        }
                    }

                    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            sliderHandler.removeCallbacks(sliderRunnable)
                            sliderHandler.postDelayed(sliderRunnable, 3000)
                        }
                    })
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    companion object {
        @Volatile
        private var instance: HomeFragment? = null

        @JvmStatic
        fun newInstance(): HomeFragment =
            instance ?: synchronized(this) {
                instance ?: HomeFragment().apply { instance = this }
            }
    }

    private fun setDashboard() {
        moodTrackerViewModel.getDashboardMood(requireActivity()).observe(requireActivity(), {
            if (activity != null) {
                if (it.mood != "") {
                    binding.apply {

                        when (it.mood_code) {
                            "1" -> ivDashboardMood.setImageDrawable(requireActivity().getDrawable(R.drawable.angry))
                            "2" -> ivDashboardMood.setImageDrawable(requireActivity().getDrawable(R.drawable.fear))
                            "3" -> ivDashboardMood.setImageDrawable(requireActivity().getDrawable(R.drawable.sad))
                            "4" -> ivDashboardMood.setImageDrawable(requireActivity().getDrawable(R.drawable.happy))
                        }

                        tvDashboardMood.text = it.mood
                        tvDashboardName.text =
                            "Hi, ${SharedPref.getPref(requireActivity(), MyAsset.KEY_NAME)}"
                        tvDashboardDate.text =
                            "Moodmu saat ini (" + DateHelper.dateFormat(it.date.take(10)) + ")"
                        tvDashboardDate.visibility = View.VISIBLE
                    }
                } else {
                    binding.tvDashboardName.text =
                        "Hi, ${SharedPref.getPref(requireActivity(), MyAsset.KEY_NAME)}"
                    binding.tvDashboardMood.text = "Semoga Harimu menyenangkan ^-^"
                    binding.tvDashboardDate.visibility = View.GONE
                }
            }
        })
    }

    override fun onProgress() {

    }

    override fun onSuccess(message: String) {
        Handler(requireActivity().mainLooper).postDelayed({
            binding.apply { progressMotivationWord.visibility = View.GONE }
        }, 500)
    }

    override fun onFailure(message: String) {
        binding.apply {
            tvMotivationQuotes.text = message
            progressMotivationWord.visibility = View.GONE
        }


    }

}