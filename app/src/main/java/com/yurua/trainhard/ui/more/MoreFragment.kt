package com.yurua.trainhard.ui.more

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.yurua.trainhard.R
import com.yurua.trainhard.ui.MainActivity
import com.yurua.trainhard.ui.prevDestination
import com.yurua.trainhard.ui.sync.SyncFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")

class MoreFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

  private companion object {
    const val SYNC_UPLOAD = 0
    const val SYNC_DOWNLOAD = 1
    const val DATABASE_NAME = "workout_database"
  }

  lateinit var auth: FirebaseAuth
  lateinit var rootStorage: StorageReference
  lateinit var backupStorage: StorageReference

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)

    auth = FirebaseAuth.getInstance()
    rootStorage = Firebase.storage.reference
    backupStorage = rootStorage.child("backup")

    val screenPref = findPreference<Preference>("keep_screen_on")
    screenPref?.onPreferenceChangeListener = this
    val uploadPref = findPreference<Preference>("reserve_upload")!!
    val downloadPref = findPreference<Preference>("reserve_download")!!

    if (auth.currentUser != null) {
      uploadPref.isEnabled = true
      uploadPref.setIcon(R.drawable.ic_backup)
      downloadPref.isEnabled = true
      downloadPref.setIcon(R.drawable.ic_download)
    } else {
      uploadPref.isEnabled = false
      uploadPref.setIcon(R.drawable.ic_backup2)
      downloadPref.isEnabled = false
      downloadPref.setIcon(R.drawable.ic_download2)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    (activity as MainActivity).supportActionBar?.subtitle = ""
    setDivider(null)
    super.onViewCreated(view, savedInstanceState)
  }

  private fun isNetworkConnected(): Boolean {
    val connectivityManager =
      activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return (networkInfo != null) && networkInfo.isConnected
  }

  @SuppressLint("SdCardPath")
  private fun syncData(type: Int) = CoroutineScope(Dispatchers.IO).launch {

    val syncFragment = SyncFragment()
    syncFragment.show(childFragmentManager, "Sync")

    val currentDBPath = """/data/data/${requireActivity().packageName}/databases/"""
    val dbPath = File(currentDBPath)

    // Создание резервных копий файлов
    if (type == SYNC_UPLOAD) {
      val file = dbPath.listFiles()?.find { it.name.equals(DATABASE_NAME) }
      val uri = Uri.fromFile(file)

      try {
        val fr = file?.let { backupStorage.child(it.name) }
        fr?.putFile(uri)?.addOnFailureListener {
        }?.addOnSuccessListener {
          Toast.makeText(requireContext(), "Резервная копия создана", Toast.LENGTH_SHORT).show()
          syncFragment.dismiss()
        }
      } catch (e: Exception) {
        syncFragment.dismiss()
        Toast.makeText(requireContext(), "Ошибка загрузки резервной копии", Toast.LENGTH_LONG)
          .show()
      }
    }
    // Загрузка файлов с сервера
    else if (type == SYNC_DOWNLOAD) {
      val outFile = File(currentDBPath, DATABASE_NAME)
      try {
        val ref = backupStorage.child(DATABASE_NAME)
        ref.getFile(outFile).addOnSuccessListener {
          Toast.makeText(requireContext(), "Резервная копия восстановлена", Toast.LENGTH_SHORT)
            .show()
          syncFragment.dismiss()
        }
      } catch (e: Exception) {
        syncFragment.dismiss()
        Toast.makeText(requireContext(), "Ошибка восстановления резервной копии", Toast.LENGTH_LONG)
          .show()
      }
    }
  }

  override fun onPreferenceTreeClick(preference: Preference): Boolean {
    val key = preference.key

    val toast = Toast.makeText(activity, "Ошибка подключения к сети", Toast.LENGTH_LONG)

    when (key) {
      "version" -> {
        val action = MoreFragmentDirections.actionMoreFragmentToVersionFragment()
        findNavController().navigate(action)
      }
      "reserve_upload" -> {
        if (!isNetworkConnected()) {
          toast.show()
          return true
        }
        syncData(SYNC_UPLOAD)
      }
      "reserve_download" -> {
        if (!isNetworkConnected()) {
          toast.show()
          return true
        }
        syncData(SYNC_DOWNLOAD)
      }
    }
    return super.onPreferenceTreeClick(preference)
  }

  override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {

    when (preference.key) {
      "keep_screen_on" -> {
        val pref = preference as SwitchPreferenceCompat
        if (!pref.isChecked)
          activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else
          activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      }
    }
    return true
  }
}