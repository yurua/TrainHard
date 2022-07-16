package com.yurua.trainhard.ui.groups

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yurua.trainhard.R
import com.yurua.trainhard.databinding.FragmentGroupsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupsFragment : DialogFragment() {

  private val viewModel: GroupsViewModel by viewModels()
  private val args: GroupsFragmentArgs by navArgs()

  private var _binding: FragmentGroupsBinding? = null
  private val binding get() = _binding!!

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

    _binding = FragmentGroupsBinding.inflate(LayoutInflater.from(context))

    val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.DialogTheme)
    builder.setNegativeButton("Отмена", null)
      .setTitle("Список упражнений")
      .setView(binding.root)

    val listDetail = viewModel.getData()
    val listTitle = ArrayList(listDetail.keys)
    val adapter = CustomListAdapter(requireActivity(), listTitle, listDetail)

    binding.groupsListView.setAdapter(adapter)

    binding.groupsListView.setOnChildClickListener { _, _, groupPos, childPos, _ ->
      // Выбранное упражнение из списка
      val routine = listDetail[listTitle[groupPos]]!![childPos]
      // Если это упражнение уже есть в тренировке, то вывести уведомление
      if (args.routines.contains(routine, true)) {
        Toast.makeText(
          requireContext(),
          "Данное упражнение есть в тренировке",
          Toast.LENGTH_LONG
        ).show()
        return@setOnChildClickListener false
      }
      val group = adapter.getGroup(groupPos)
      setFragmentResult("add_gym_request", bundleOf("add_gym_group" to group, "add_gym_routine" to routine))
      findNavController().popBackStack()
      false
    }

    return builder.create()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}