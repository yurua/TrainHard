package com.yurua.trainhard.ui.stat

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.yurua.trainhard.R
import com.yurua.trainhard.databinding.FragmentStatBinding
import com.yurua.trainhard.ui.stat.StatViewModel.StatEvent.Something
import com.yurua.trainhard.util.TitleListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlin.math.floor


@AndroidEntryPoint
class StatFragment : Fragment(R.layout.fragment_stat) {

    val viewModel: StatViewModel by viewModels()
    lateinit var listener: TitleListener

    private var _binding: FragmentStatBinding? = null
    private val binding
        get() = _binding!!

    // Набор данных для графиков упражнений
    private var dataSet1 = LineDataSet(arrayListOf(), "")
    private var dataSet2 = LineDataSet(arrayListOf(), "")
    private var dataSet3 = LineDataSet(arrayListOf(), "")

    private lateinit var xAxis: XAxis
    private lateinit var yAxis: YAxis

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_stat, menu)
        // По-умолчанию выбранная группа мышц - грудь
        menu.findItem(R.id.stat_shoul).isChecked = true
    }

    // Показ подзаголовка окна
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as TitleListener
        } catch (castException: ClassCastException) {
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        dataSet1.clear()
        dataSet2.clear()
        dataSet3.clear()
        item.isChecked = !item.isChecked
        val title = item.title.toString()
        val gyms = viewModel.getGymsByGroup(title)
        viewModel.queryGraph(title)
        binding.b1.text = "   ${gyms[0]}"
        binding.b2.text = "   ${gyms[1]}"
        binding.b3.text = "   ${gyms[2]}"

        listener.setToolbarTitle(title)
        return super.onOptionsItemSelected(item)
    }

    private fun getMinMax(ds: LineDataSet): Pair<Float, Float> {
        val arr = arrayListOf<Float>()
        for (v in ds.values)
            arr.add(v.y)
        val min = arr.minOrNull() ?: 0f
        val max = arr.maxOrNull() ?: 0f
        return Pair(min, max)
    }

    fun draw(graph1: ArrayList<Entry>, graph2: ArrayList<Entry>, graph3: ArrayList<Entry>) {

        // Первое упражнение выбирается по-умолчанию
        binding.rg.check(R.id.b1)

        // Если имеются данные,то они сохраняются в соответствующих полях
        if (graph1.isNotEmpty())
            dataSet1.values = graph1

        if (graph2.isNotEmpty())
            dataSet2.values = graph2

        if (graph3.isNotEmpty())
            dataSet3.values = graph3

        // Загрузить данные первого упражнения, если они есть
        val data: LineData = if (dataSet1.entryCount > 0)
            LineData(dataSet1)
        else
            LineData()


        if (data.entryCount > 0) {
            xAxis.setLabelCount(data.entryCount, true)
            val rangeY = getMinMax(data.getDataSetByIndex(0) as LineDataSet)
            yAxis.axisMinimum = floor((rangeY.first * 0.8f + 10f / 2f) / 10f) * 10f
            yAxis.axisMaximum = floor((rangeY.second * 1.2f + 10f / 2f) / 10f) * 10f
            // Сохранить количество тренировок с этим упражнением
            binding.chart.visibility = View.VISIBLE
            binding.tChart.isVisible = false
            binding.chart.data = data
            binding.chart.invalidate()
        } else {
            binding.chart.visibility = View.INVISIBLE
            binding.tChart.isVisible = true
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentStatBinding.bind(view)

        setHasOptionsMenu(true)

        // Настройка параметров графиков
        val dataSets = listOf(dataSet1, dataSet2, dataSet3)
        dataSets.forEach {
            with(it) {
                lineWidth = 1.5f
                circleRadius = 4f
                circleHoleRadius = 3f
                circleHoleColor = Color.parseColor("#000000")
                colors = arrayListOf(Color.parseColor("#1af9c2"))
                circleColors = arrayListOf(Color.parseColor("#1af9c2"))
                valueTextSize = 10f
                valueTextColor = Color.parseColor("#dfFFFFFF")
                axisDependency = YAxis.AxisDependency.LEFT
                setDrawValues(true)
                setDrawHighlightIndicators(false)
                highLightColor = Color.parseColor("#61FFFFFF")
            }
        }

        yAxis = binding.chart.axisLeft
        yAxis.gridColor = Color.parseColor("#454545")
        yAxis.textSize = 10f
        yAxis.axisLineWidth = 0.1f
        yAxis.gridLineWidth = 0.6f
        yAxis.setDrawLabels(false)
        yAxis.textColor = Color.parseColor("#61FFFFFF")

        xAxis = binding.chart.xAxis
        xAxis.position = BOTTOM
        xAxis.axisLineWidth = 0.1f
        xAxis.gridColor = Color.parseColor("#454545")
        xAxis.textSize = 10f
        xAxis.gridLineWidth = 0.6f
        xAxis.setDrawLabels(false)
        xAxis.textColor = Color.parseColor("#61FFFFFF")

        binding.chart.legend.isEnabled = false
        binding.chart.axisRight.isEnabled = false
        binding.chart.description.isEnabled = false

        binding.chart.setBackgroundColor(Color.parseColor("#000000"))

        binding.rg.setOnCheckedChangeListener { _, _ ->

            val data = when (binding.rg.checkedRadioButtonId) {
                binding.b1.id -> {
                    if (dataSet1.entryCount > 0)
                        LineData(dataSet1)
                    else
                        LineData()
                }
                binding.b2.id -> {
                    if (dataSet2.entryCount > 0)
                        LineData(dataSet2)
                    else
                        LineData()
                }
                binding.b3.id -> {
                    if (dataSet3.entryCount > 0)
                        LineData(dataSet3)
                    else
                        LineData()
                }
                else -> LineData()
            }
            if (data.entryCount > 0) {
                xAxis.setLabelCount(data.entryCount, true)
                val rangeY = getMinMax(data.getDataSetByIndex(0) as LineDataSet)
                yAxis.axisMinimum = floor((rangeY.first * 0.75f + 10f / 2f) / 10f) * 10f
                yAxis.axisMaximum = floor((rangeY.second * 1.25f + 10f / 2f) / 10f) * 10f
                binding.chart.visibility = View.VISIBLE
                binding.tChart.isVisible = false
                binding.chart.data = data
                binding.chart.invalidate()

            } else {
                binding.chart.visibility = View.INVISIBLE
                binding.tChart.isVisible = true
            }

        }

        val title = "Плечи"
        val gyms = viewModel.getGymsByGroup(title)
        // Отобразить данные первого упражнения на грудь
        binding.b1.text = "  ${gyms[0]}"
        binding.b2.text = "  ${gyms[1]}"
        binding.b3.text = "  ${gyms[2]}"

        viewModel.queryGraph(title)

        listener.setToolbarTitle(title)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.statEvent.collect { event ->
                when (event) {
                    is Something -> {
                        draw(event.graph1, event.graph2, event.graph3)
                    }
                }
            }
        }
    }
}

