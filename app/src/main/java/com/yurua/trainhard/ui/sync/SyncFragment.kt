package com.yurua.trainhard.ui.sync

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yurua.trainhard.R
import com.yurua.trainhard.R.style
import com.yurua.trainhard.databinding.FragmentSyncBinding

@Suppress("DEPRECATION")
class SyncFragment : AppCompatDialogFragment() {

  private var _binding: FragmentSyncBinding? = null
  private val binding get() = _binding!!

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _binding = FragmentSyncBinding.inflate(LayoutInflater.from(context))

    val builder = MaterialAlertDialogBuilder(requireActivity(), style.DialogTheme)
    builder.setBackground(resources.getDrawable(R.drawable.dialog_rounded2))
      .setView(binding.root)

    return builder.create()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}