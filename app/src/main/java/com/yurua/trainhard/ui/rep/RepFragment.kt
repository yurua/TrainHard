package com.yurua.trainhard.ui.rep

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yurua.trainhard.R
import com.yurua.trainhard.R.style
import com.yurua.trainhard.databinding.FragmentRepBinding

class RepFragment : DialogFragment(), TextWatcher {

  private val args: RepFragmentArgs by navArgs()

  private var _binding: FragmentRepBinding? = null
  private val binding get() = _binding!!

  private lateinit var dialog: AlertDialog

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _binding = FragmentRepBinding.inflate(LayoutInflater.from(context))

    val builder = MaterialAlertDialogBuilder(requireActivity(), style.DialogTheme)
    builder.setNegativeButton("Отмена", null)
      .setPositiveButton("Добавить") { _, _ ->
        val weight = binding.etWeight.text.toString()
        val reps = binding.etReps.text.toString()
        // Передача вызывающему фрагменту данных
        setFragmentResult(
          "add_routine_request",
          bundleOf(
            "add_routine_weight" to weight,
            "add_routine_reps" to reps,
            "add_routine_block" to args.block
          )
        )
        // Спрятать клавиатуру
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
        // Вернуться назад к списку упражнений
        findNavController().popBackStack()
      }
      .setTitle("Жим штанги лежа")
      .setView(binding.root)

    // Показать клавиатуру при открытии окна
    binding.etWeight.requestFocus()
    val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    binding.etWeight.addTextChangedListener(this)
    binding.etReps.addTextChangedListener(this)

    dialog = builder.create()
    // При открытии окна кнопка Добавить неактивна
    dialog.setOnShowListener {
      (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.disable))
      it.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }

    return dialog
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
  }

  override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    // Если оба поля не пусты, то сделать кнопку Добавить активной
    if (binding.etReps.text.isNotEmpty() && binding.etWeight.text.isNotEmpty()) {
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
      dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
    } else {
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
      dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(requireContext(), R.color.disable))
    }
  }

  override fun afterTextChanged(p0: Editable?) {
  }

}