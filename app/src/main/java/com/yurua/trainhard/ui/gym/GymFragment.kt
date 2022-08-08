package com.yurua.trainhard.ui.gym

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.yurua.trainhard.R
import com.yurua.trainhard.data.Work
import com.yurua.trainhard.databinding.FragmentGymBinding
import com.yurua.trainhard.ui.MainActivity
import com.yurua.trainhard.ui.gym.GymViewModel.GymEvent.NavigateBack
import com.yurua.trainhard.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class GymFragment : Fragment(R.layout.fragment_gym), View.OnClickListener {

    // По умолчанию тренировке присваивается текущая дата
    private var gymDate = Date().str()

    private val viewModel: GymViewModel by viewModels()
    private val args: GymFragmentArgs by navArgs()
    private var work: Work? = null
    private var isGymCompleted = false

    private var _binding: FragmentGymBinding? = null
    private val binding
        get() = _binding!!


    private fun setupGyms(work: Work) {
        val gyms = workToGym(work)

        for ((currBlock, v) in gyms.withIndex()) when (currBlock) {
            0 -> {
                binding.semaphor1.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(v.group)
                    )
                )
                binding.cv1.visibility = View.VISIBLE
                binding.tvHeader1.text = v.name
                binding.c11.text = v.set1
                binding.c12.text = v.set2
                binding.c13.text = v.set3
            }
            1 -> {
                binding.semaphor2.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(v.group)
                    )
                )
                binding.cv2.visibility = View.VISIBLE
                binding.tvHeader2.text = v.name
                binding.c21.text = v.set1
                binding.c22.text = v.set2
                binding.c23.text = v.set3
            }
            2 -> {
                binding.semaphor3.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(v.group)
                    )
                )
                binding.cv3.visibility = View.VISIBLE
                binding.tvHeader3.text = v.name
                binding.c31.text = v.set1
                binding.c32.text = v.set2
                binding.c33.text = v.set3
            }
            3 -> {
                binding.semaphor4.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(v.group)
                    )
                )
                binding.cv4.visibility = View.VISIBLE
                binding.tvHeader4.text = v.name
                binding.c41.text = v.set1
                binding.c42.text = v.set2
                binding.c43.text = v.set3
            }
            4 -> {
                binding.semaphor5.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(v.group)
                    )
                )
                binding.cv5.visibility = View.VISIBLE
                binding.tvHeader5.text = v.name
                binding.c51.text = v.set1
                binding.c52.text = v.set2
                binding.c53.text = v.set3
            }
            5 -> {
                binding.semaphor6.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(v.group)
                    )
                )
                binding.cv6.visibility = View.VISIBLE
                binding.tvHeader6.text = v.name
                binding.c61.text = v.set1
                binding.c62.text = v.set2
                binding.c63.text = v.set3
            }
        }
    }

    private fun showCalendar() {
        val datePicker = MaterialDatePicker.Builder.datePicker().setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
        datePicker.show(requireActivity().supportFragmentManager, "DatePicker")
        datePicker.addOnPositiveButtonClickListener {
            gymDate = Date(it).str()
            setSubtitle(gymDate)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGymBinding.bind(view)

        setSubtitle(gymDate)

        work = args.work

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_gym, menu)

                menu.findItem(R.id.gym_save).isVisible = isGymCompleted
                menu.findItem(R.id.gym_date).isVisible = isGymCompleted
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.gym_save -> {
                        saveWork()
                        true
                    }
                    R.id.gym_date -> {
                        showCalendar()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Режим просмотра тренировки
        work?.let {

            val groups = getGroups(it)

            setupGyms(it)

            with(binding) {
                ivAddRoutine1.isVisible = false
                ivAddRoutine2.isVisible = false
                ivAddRoutine3.isVisible = false
                ivAddRoutine4.isVisible = false
                ivAddRoutine5.isVisible = false
                ivAddRoutine6.isVisible = false
            }

            binding.ivEmpty.visibility = View.INVISIBLE
            binding.tvEmpty.visibility = View.INVISIBLE
            binding.fabGym.visibility = View.INVISIBLE

            binding.tvGroup1.visibility = View.VISIBLE
            binding.tvGroup1.text = groups[0]
            binding.tvGroup1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    getColorByGroup(groups[0])
                )
            )
            binding.tvGroup1.background = ResourcesCompat.getDrawable(
                requireActivity().resources,
                getBackgroundByGroup(groups[0]),
                null
            )

            if (groups.size == 2) {
                binding.tvGroup2.visibility = View.VISIBLE
                binding.tvGroup2.text = groups[1]
                binding.tvGroup2.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(groups[1])
                    )
                )
                binding.tvGroup2.background = ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    getBackgroundByGroup(groups[1]),
                    null
                )
            }

        setSubtitle(it.date)
        }

        binding.ivAddRoutine1.setOnClickListener(this)
        binding.ivAddRoutine2.setOnClickListener(this)
        binding.ivAddRoutine3.setOnClickListener(this)
        binding.ivAddRoutine4.setOnClickListener(this)
        binding.ivAddRoutine5.setOnClickListener(this)
        binding.ivAddRoutine6.setOnClickListener(this)
        // Добавить список текущих упражнений
        binding.fabGym.setOnClickListener {
            var result = ""
            if (binding.cv1.isVisible)
                result += binding.tvHeader1.text
            if (binding.cv2.isVisible)
                result += "/" + binding.tvHeader2.text
            if (binding.cv3.isVisible)
                result += "/" + binding.tvHeader3.text
            if (binding.cv4.isVisible)
                result += "/" + binding.tvHeader4.text
            if (binding.cv5.isVisible)
                result += "/" + binding.tvHeader5.text
            if (binding.cv6.isVisible)
                result += "/" + binding.tvHeader6.text

            val action = GymFragmentDirections.actionGymFragmentToGroupsFragment(result)
            findNavController().navigate(action)
        }

        setFragmentResultListener("add_gym_request") { _, bundle ->
            val group = bundle.getString("add_gym_group")!!
            val routine = bundle.getString("add_gym_routine")!!
            addGym(group, routine)
        }
        setFragmentResultListener("add_routine_request") { _, bundle ->
            val weight = bundle.getString("add_routine_weight")!!
            val reps = bundle.getString("add_routine_reps")!!
            val block = bundle.getInt("add_routine_block")
            addRoutine(weight, reps, block)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.gymEvent.collect { event ->
                when (event) {
                    is NavigateBack -> {
                        setFragmentResult(
                            "gym_request",
                            bundleOf("gym_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun addRoutine(weight: String, reps: String, block: Int) {
        val blockInfo = weight + "x" + reps
        when (block) {
            0 -> {
                if (!binding.c11.isVisible) {
                    binding.c11.text = blockInfo
                    binding.c11.visibility = View.VISIBLE
                } else if (!binding.c12.isVisible) {
                    binding.c12.text = blockInfo
                    binding.c12.visibility = View.VISIBLE
                } else if (!binding.c13.isVisible) {
                    binding.c13.text = blockInfo
                    binding.c13.visibility = View.VISIBLE
                    isGymCompleted = true
                }
            }
            1 -> {
                if (!binding.c21.isVisible) {
                    binding.c21.text = blockInfo
                    binding.c21.visibility = View.VISIBLE
                } else if (!binding.c22.isVisible) {
                    binding.c22.text = blockInfo
                    binding.c22.visibility = View.VISIBLE
                } else if (!binding.c23.isVisible) {
                    binding.c23.text = blockInfo
                    binding.c23.visibility = View.VISIBLE
                    isGymCompleted = true
                }
            }
            2 -> {
                if (!binding.c31.isVisible) {
                    binding.c31.text = blockInfo
                    binding.c31.visibility = View.VISIBLE
                } else if (!binding.c32.isVisible) {
                    binding.c32.text = blockInfo
                    binding.c32.visibility = View.VISIBLE
                } else if (!binding.c33.isVisible) {
                    binding.c33.text = blockInfo
                    binding.c33.visibility = View.VISIBLE
                    isGymCompleted = true
                }
            }
            3 -> {
                if (!binding.c41.isVisible) {
                    binding.c41.text = blockInfo
                    binding.c41.visibility = View.VISIBLE
                } else if (!binding.c42.isVisible) {
                    binding.c42.text = blockInfo
                    binding.c42.visibility = View.VISIBLE
                } else if (!binding.c43.isVisible) {
                    binding.c43.text = blockInfo
                    binding.c43.visibility = View.VISIBLE
                    isGymCompleted = true
                }
            }
            4 -> {
                if (!binding.c51.isVisible) {
                    binding.c51.text = blockInfo
                    binding.c51.visibility = View.VISIBLE
                } else if (!binding.c52.isVisible) {
                    binding.c52.text = blockInfo
                    binding.c52.visibility = View.VISIBLE
                } else if (!binding.c53.isVisible) {
                    binding.c53.text = blockInfo
                    binding.c53.visibility = View.VISIBLE
                    isGymCompleted = true
                }
            }
            5 -> {
                if (!binding.c61.isVisible) {
                    binding.c61.text = blockInfo
                    binding.c61.visibility = View.VISIBLE
                } else if (!binding.c62.isVisible) {
                    binding.c62.text = blockInfo
                    binding.c62.visibility = View.VISIBLE
                } else if (!binding.c63.isVisible) {
                    binding.c63.text = blockInfo
                    binding.c63.visibility = View.VISIBLE
                    isGymCompleted = true
                }
            }
        }
    }

    private fun addGym(group: String, routine: String) {
        when (viewModel.currentBlock) {
            0 -> {
                binding.cv1.visibility = View.VISIBLE
                binding.c11.visibility = View.INVISIBLE
                binding.c12.visibility = View.INVISIBLE
                binding.c13.visibility = View.INVISIBLE
                binding.tvHeader1.text = routine
                binding.semaphor1.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(group)
                    )
                )
            }
            1 -> {
                binding.cv2.visibility = View.VISIBLE
                binding.c21.visibility = View.INVISIBLE
                binding.c22.visibility = View.INVISIBLE
                binding.c23.visibility = View.INVISIBLE
                binding.tvHeader2.text = routine
                binding.semaphor2.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(group)
                    )
                )
            }
            2 -> {
                binding.cv3.visibility = View.VISIBLE
                binding.c31.visibility = View.INVISIBLE
                binding.c32.visibility = View.INVISIBLE
                binding.c33.visibility = View.INVISIBLE
                binding.tvHeader3.text = routine
                binding.semaphor3.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(group)
                    )
                )
            }
            3 -> {
                binding.cv4.visibility = View.VISIBLE
                binding.c41.visibility = View.INVISIBLE
                binding.c42.visibility = View.INVISIBLE
                binding.c43.visibility = View.INVISIBLE
                binding.tvHeader4.text = routine
                binding.semaphor4.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(group)
                    )
                )
            }
            4 -> {
                binding.cv5.visibility = View.VISIBLE
                binding.c51.visibility = View.INVISIBLE
                binding.c52.visibility = View.INVISIBLE
                binding.c53.visibility = View.INVISIBLE
                binding.tvHeader5.text = routine
                binding.semaphor5.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(group)
                    )
                )
            }
            5 -> {
                binding.cv6.visibility = View.VISIBLE
                binding.c61.visibility = View.INVISIBLE
                binding.c62.visibility = View.INVISIBLE
                binding.c63.visibility = View.INVISIBLE
                binding.tvHeader6.text = routine
                binding.semaphor6.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        getColorByGroup(group)
                    )
                )
            }
        }
        if (binding.tvGroup1.visibility == View.INVISIBLE) {
            binding.tvGroup1.visibility = View.VISIBLE
            binding.tvGroup1.text = group
            binding.tvGroup1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    getColorByGroup(group)
                )
            )
            binding.tvGroup1.background = ResourcesCompat.getDrawable(
                requireActivity().resources,
                getBackgroundByGroup(group),
                null
            )
        } else if (binding.tvGroup2.visibility == View.INVISIBLE && binding.tvGroup1.text != group) {
            binding.tvGroup2.visibility = View.VISIBLE
            binding.tvGroup2.text = group
            binding.tvGroup2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    getColorByGroup(group)
                )
            )
            binding.tvGroup2.background = ResourcesCompat.getDrawable(
                requireActivity().resources,
                getBackgroundByGroup(group),
                null
            )

        }
        binding.tvEmpty.visibility = View.INVISIBLE
        binding.ivEmpty.visibility = View.INVISIBLE
        viewModel.currentBlock++
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View?) {
        val view = p0 as ImageView

        val index = when (view.id) {
            R.id.ivAddRoutine1 -> 0
            R.id.ivAddRoutine2 -> 1
            R.id.ivAddRoutine3 -> 2
            R.id.ivAddRoutine4 -> 3
            R.id.ivAddRoutine5 -> 4
            R.id.ivAddRoutine6 -> 5
            else -> -1
        }

        val action = GymFragmentDirections.actionGymFragmentToRepFragment(index)
        findNavController().navigate(action)
    }

    private fun getGroupByGym(gym: String): String {

        val chest = resources.getStringArray(R.array.chest).asList()
        val biceps = resources.getStringArray(R.array.biceps).asList()
        val triceps = resources.getStringArray(R.array.triceps).asList()
        val shoul = resources.getStringArray(R.array.shoulders).asList()
        val legs = resources.getStringArray(R.array.legs).asList()
        val back = resources.getStringArray(R.array.back).asList()

        if (chest.lastIndexOf(gym) != -1)
            return "Грудь"
        if (biceps.lastIndexOf(gym) != -1)
            return "Бицепс"
        if (triceps.lastIndexOf(gym) != -1)
            return "Трицепс"
        if (shoul.lastIndexOf(gym) != -1)
            return "Плечи"
        if (legs.lastIndexOf(gym) != -1)
            return "Ноги"
        if (back.lastIndexOf(gym) != -1)
            return "Спина"

        return ""
    }

    private fun saveWork() {

        var chest = ""
        var biceps = ""
        var triceps = ""
        var back = ""
        var shoul = ""
        var legs = ""

        if (binding.cv1.isVisible) {
            // Название упражнения
            val gymName = binding.tvHeader1.text.toString()
            val gymSet =
                gymName + ":" + binding.c11.text.toString() + "|" + binding.c12.text.toString() + "|" + binding.c13.text.toString()
            when (getGroupByGym(gymName)) {
                "Грудь" -> {
                    chest += if (chest == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Бицепс" -> {
                    biceps += if (biceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Трицепс" -> {
                    triceps += if (triceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Спина" -> {
                    back += if (back == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Ноги" -> {
                    legs += if (legs == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Плечи" -> {
                    shoul += if (shoul == "")
                        gymSet
                    else
                        "/$gymSet"
                }
            }
        }
        if (binding.cv2.isVisible) {
            // Название упражнения
            val gymName = binding.tvHeader2.text.toString()
            val gymSet =
                gymName + ":" + binding.c21.text.toString() + "|" + binding.c22.text.toString() + "|" + binding.c23.text.toString()
            when (getGroupByGym(gymName)) {
                "Грудь" -> {
                    chest += if (chest == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Бицепс" -> {
                    biceps += if (biceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Трицепс" -> {
                    triceps += if (triceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Спина" -> {
                    back += if (back == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Ноги" -> {
                    legs += if (legs == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Плечи" -> {
                    shoul += if (shoul == "")
                        gymSet
                    else
                        "/$gymSet"
                }
            }
        }
        if (binding.cv3.isVisible) {
            // Название упражнения
            val gymName = binding.tvHeader3.text.toString()
            val gymSet =
                gymName + ":" + binding.c31.text.toString() + "|" + binding.c32.text.toString() + "|" + binding.c33.text.toString()
            when (getGroupByGym(gymName)) {
                "Грудь" -> {
                    chest += if (chest == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Бицепс" -> {
                    biceps += if (biceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Трицепс" -> {
                    triceps += if (triceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Спина" -> {
                    back += if (back == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Ноги" -> {
                    legs += if (legs == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Плечи" -> {
                    shoul += if (shoul == "")
                        gymSet
                    else
                        "/$gymSet"
                }
            }
        }
        if (binding.cv4.isVisible) {
            // Название упражнения
            val gymName = binding.tvHeader4.text.toString()
            val gymSet =
                gymName + ":" + binding.c41.text.toString() + "|" + binding.c42.text.toString() + "|" + binding.c43.text.toString()
            when (getGroupByGym(gymName)) {
                "Грудь" -> {
                    chest += if (chest == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Бицепс" -> {
                    biceps += if (biceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Трицепс" -> {
                    triceps += if (triceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Спина" -> {
                    back += if (back == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Ноги" -> {
                    legs += if (legs == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Плечи" -> {
                    shoul += if (shoul == "")
                        gymSet
                    else
                        "/$gymSet"
                }
            }
        }
        if (binding.cv5.isVisible) {
            // Название упражнения
            val gymName = binding.tvHeader5.text.toString()
            val gymSet =
                gymName + ":" + binding.c51.text.toString() + "|" + binding.c52.text.toString() + "|" + binding.c53.text.toString()
            when (getGroupByGym(gymName)) {
                "Грудь" -> {
                    chest += if (chest == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Бицепс" -> {
                    biceps += if (biceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Трицепс" -> {
                    triceps += if (triceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Спина" -> {
                    back += if (back == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Ноги" -> {
                    legs += if (legs == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Плечи" -> {
                    shoul += if (shoul == "")
                        gymSet
                    else
                        "/$gymSet"
                }
            }
        }
        if (binding.cv6.isVisible) {
            // Название упражнения
            val gymName = binding.tvHeader6.text.toString()
            val gymSet =
                gymName + ":" + binding.c61.text.toString() + "|" + binding.c62.text.toString() + "|" + binding.c63.text.toString()
            when (getGroupByGym(gymName)) {
                "Грудь" -> {
                    chest += if (chest == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Бицепс" -> {
                    biceps += if (biceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Трицепс" -> {
                    triceps += if (triceps == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Спина" -> {
                    back += if (back == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Ноги" -> {
                    legs += if (legs == "")
                        gymSet
                    else
                        "/$gymSet"
                }
                "Плечи" -> {
                    shoul += if (shoul == "")
                        gymSet
                    else
                        "/$gymSet"
                }
            }
        }

        var groups = ""

        if (chest.isNotEmpty())
            groups += "Грудь/"
        if (biceps.isNotEmpty())
            groups += "Бицепс/"
        if (triceps.isNotEmpty())
            groups += "Трицепс/"
        if (legs.isNotEmpty())
            groups += "Ноги/"
        if (back.isNotEmpty())
            groups += "Спина/"
        if (shoul.isNotEmpty())
            groups += "Плечи/"

        val groupsClean = groups.substring(0, groups.lastIndexOf('/'))

        val work = Work(gymDate, groupsClean, chest, biceps, triceps, legs, shoul, back)

        viewModel.saveWork(work)

    }
}
