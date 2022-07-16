package com.yurua.trainhard.ui.version

import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yurua.trainhard.R.style
import com.yurua.trainhard.databinding.FragmentVersionBinding

class VersionFragment : AppCompatDialogFragment() {

    private var _binding: FragmentVersionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentVersionBinding.inflate(LayoutInflater.from(context))

        binding.tvVersion.movementMethod = ScrollingMovementMethod()
        binding.tvVersion.text = loadFile()

        val builder = MaterialAlertDialogBuilder(requireActivity(), style.DialogTheme)
        builder.setPositiveButton("OK", null)
            .setView(binding.root)
            .setTitle("История версий")

        return builder.create()
    }

    private fun loadFile(): String? {
        return try {
            val istream = activity?.assets?.open("changelog.txt")
            val inputString = istream?.bufferedReader().use { it?.readText() }
            inputString
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}