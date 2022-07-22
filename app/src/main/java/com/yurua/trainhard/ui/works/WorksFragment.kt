package com.yurua.trainhard.ui.works

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import com.yurua.trainhard.R
import com.yurua.trainhard.R.drawable
import com.yurua.trainhard.R.layout
import com.yurua.trainhard.data.Work
import com.yurua.trainhard.databinding.FragmentWorksBinding
import com.yurua.trainhard.ui.GYM_RESULT_OK
import com.yurua.trainhard.ui.MainActivity
import com.yurua.trainhard.ui.works.WorksAdapter.OnItemClickListener
import com.yurua.trainhard.ui.works.WorksViewModel.WorksEvent.*
import com.yurua.trainhard.util.exhaustive
import com.yurua.trainhard.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WorksFragment : Fragment(layout.fragment_works), OnItemClickListener {

    private val viewModel: WorksViewModel by viewModels()

    private var _binding: FragmentWorksBinding? = null
    private val binding
        get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_workouts, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.background = ContextCompat.getDrawable(requireContext(), drawable.search_input)
                searchView.apply {
                    queryHint = HtmlCompat.fromHtml(
                        "<font color = #FFACACAC> Поиск по группе </font>",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    imeOptions = EditorInfo.IME_ACTION_DONE
                }

                val iconClose = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
                iconClose.setImageResource(drawable.ic_search_close)

                val autoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
                autoComplete.textSize = 17.0f
                autoComplete.setHintTextColor(Color.parseColor("#c7c7c7"))
                autoComplete.setTextColor(Color.parseColor("#c7c7c7"))
                autoComplete.setBackgroundColor(Color.parseColor("#202020"))
                autoComplete.setDropDownBackgroundResource(drawable.search_input)

                val labels = listOf("Грудь", "Бицепс", "Спина", "Трицепс", "Ноги", "Плечи")
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    labels
                )
                autoComplete.setAdapter(adapter)

                autoComplete.setOnItemClickListener { parent, _, position, _ ->
                    val str = parent.getItemAtPosition(position) as String
                    autoComplete.setText(str)
                    val imm =
                        context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
                }

                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val swipeBackground = ColorDrawable(Color.parseColor("#e32636"))
        val deleteIcon = ContextCompat.getDrawable(requireActivity(), drawable.ic_delete)

        _binding = FragmentWorksBinding.bind(view)

        val workAdapter = WorksAdapter(this)

        binding.apply {
            workoutRecyclerView.apply {
                adapter = workAdapter
                layoutManager = LinearLayoutManager(requireContext(), VERTICAL, true)
                setHasFixedSize(true)
                val itemDecoration =
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                itemDecoration.setDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        drawable.divider,
                        null
                    )!!
                )
                addItemDecoration(itemDecoration)
            }

            setFragmentResultListener("gym_request") { _, bundle ->
                val result = bundle.getInt("gym_result")
                when (result) {
                    GYM_RESULT_OK -> {
                        Toast.makeText(requireContext(), "Тренировка сохранена", Toast.LENGTH_SHORT)
                            .apply {
                                setGravity(Gravity.CENTER, 0, 0)
                            }.show()
                    }
                }
            }

            ItemTouchHelper(object : SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder,
                    target: ViewHolder
                ) = false

                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                    val workout = workAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onWorkoutSwiped(workout)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView

                    val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2

                    if (dX > 0) {
                        swipeBackground.setBounds(
                            itemView.left,
                            itemView.top,
                            dX.toInt(),
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.left + iconMargin,
                            itemView.top + iconMargin,
                            itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMargin
                        )
                    } else {
                        swipeBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                            itemView.top + iconMargin,
                            itemView.right - iconMargin,
                            itemView.bottom - iconMargin
                        )
                    }

                    swipeBackground.draw(c)
                    c.save()

                    if (dX > 0) c.clipRect(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    ) else c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                    deleteIcon.draw(c)
                    c.restore()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }).attachToRecyclerView(workoutRecyclerView)
        }

        binding.fab.setOnClickListener {
            viewModel.addGymHandler()
        }

        viewModel.works.observe(viewLifecycleOwner) {
            workAdapter.submitList(it)
            (activity as MainActivity).supportActionBar?.subtitle = "Тренировок: ${it.size}"
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.worksEvent.collect { event ->
                when (event) {
                    is ShowUndoDeleteWorkoutMessage -> {
                        Snackbar.make(requireView(), "Запись удалена", Snackbar.LENGTH_LONG)
                            .setAction("Отмена") {
                                viewModel.onUndoDeleteClick(event.work)
                            }.show()
                    }
                    is NavigateAddGym -> {
                        val action = WorksFragmentDirections.actionWorkFragmentToGymFragment()
                        findNavController().navigate(action)
                    }
                    is NavigateViewGym -> {
                        val action =
                            WorksFragmentDirections.actionWorkFragmentToGymFragment(event.work)
                        findNavController().navigate(action)
                    }
                }.exhaustive

            }
        }

    }

    override fun onItemClick(work: Work) {
        viewModel.onWorkSelected(work)
    }

}