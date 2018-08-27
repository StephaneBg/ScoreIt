package com.sbgapps.scoreit.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.db.chart.model.LineSet
import com.db.chart.renderer.AxisRenderer
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseFragment
import com.sbgapps.scoreit.ui.ext.color
import com.sbgapps.scoreit.ui.ext.observe
import com.sbgapps.scoreit.ui.model.Game
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import kotlinx.android.synthetic.main.fragment_chart.*
import org.jetbrains.anko.dip
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChartFragment : BaseFragment() {

    private val model by sharedViewModel<UniversalViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_chart, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scoreChart.setXLabels(AxisRenderer.LabelPosition.NONE)
            .setYLabels(AxisRenderer.LabelPosition.NONE)
            .setYAxis(false)
            .setAxisColor(requireContext().color(R.color.blue_50))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observe(model.getGame(), ::plotLaps)
    }

    private fun plotLaps(game: Game?) {
        game?.let {
            scoreChart.reset()
            it.achievement.forEach { (player, points) ->
                val line = LineSet()
                points.forEach { point -> line.addPoint("", point.toFloat()) }
                line.color = player.color
                line.thickness = requireContext().dip(2).toFloat()
                line.setDotsRadius(requireContext().dip(4).toFloat())
                line.setDotsColor(requireContext().color(R.color.white))
                line.setDotsStrokeColor(player.color)
                scoreChart.addData(line)
            }
            scoreChart.show()
        }
    }

    companion object {
        fun newInstance() = ChartFragment()
    }
}