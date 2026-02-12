package com.zhongpin.mvvm_android.ui.order.detail.timeline

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import com.lxj.xpopup.util.XPopupUtils.setVisible
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.OrderItemProgressTimelineBinding
import com.zhongpin.lib_base.utils.VectorDrawableUtils



class OrderStatusTimelineAdapter(private val timelineModels: List<OrderStatusTimelineModel>) :
    RecyclerView.Adapter<OrderStatusTimelineAdapter.TimelineViewHolder>() {

    private lateinit var layoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {

        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }

        return TimelineViewHolder(
            OrderItemProgressTimelineBinding.inflate(layoutInflater, parent, false),
            viewType
        )
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val timeLineModel = timelineModels[position]
        holder.bind(timeLineModel)
    }

    override fun getItemCount() = timelineModels.size

    inner class TimelineViewHolder(
        private val binding: OrderItemProgressTimelineBinding,
        private val viewType: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.timeline.initLine(viewType)
        }

        fun bind(model: OrderStatusTimelineModel) {
            when (model.status) {
                OrderStatusTimelineModel.UN_DONE -> {
                    setMarker(R.drawable.ic_marker_inactive)
                    binding.timeline.run {
                        setStartLineColor(Color.parseColor("#D8D8D8"), viewType)
                        setEndLineColor(Color.parseColor("#D8D8D8"), viewType)
                        startLineStyle = TimelineView.LineStyle.DASHED
                        endLineStyle = TimelineView.LineStyle.DASHED
                    }
                }
                OrderStatusTimelineModel.DONE -> {
                    setMarker(R.drawable.ic_marker_active)
                    binding.timeline.run {
                        setStartLineColor(Color.parseColor("#57C248"), viewType)
                        setEndLineColor(Color.parseColor("#57C248"), viewType)
                        startLineStyle = TimelineView.LineStyle.DASHED
                        endLineStyle = TimelineView.LineStyle.DASHED
                    }
                }
                OrderStatusTimelineModel.ACTIVE -> {
                    setMarker(R.mipmap.ic_marker_active2)
                    binding.timeline.run {
                        setStartLineColor(Color.parseColor("#57C248"),viewType)
                        setEndLineColor(Color.parseColor("#D8D8D8"),viewType)
                        startLineStyle = TimelineView.LineStyle.DASHED
                        endLineStyle = TimelineView.LineStyle.DASHED
                    }
                }

                else -> {
                    setMarker(R.drawable.ic_marker_inactive)
                    binding.timeline.run {
                        setStartLineColor(Color.parseColor("#D8D8D8"),viewType)
                        setEndLineColor(Color.parseColor("#D8D8D8"), viewType)
                        startLineStyle = TimelineView.LineStyle.DASHED
                        endLineStyle = TimelineView.LineStyle.DASHED
                    }
                }
            }

            if (model.date.isNotEmpty()) {
                binding.timelineDate.run {
                    text = model.date
                }
            }
            binding.timelineTitle.text = model.title
        }

        private fun setMarker(drawableResId: Int, colorFilter: Int) {
            binding.timeline.marker = VectorDrawableUtils.getDrawable(
                binding.timeline.context,
                drawableResId,
                ContextCompat.getColor(binding.timeline.context, colorFilter)
            )
        }

        private fun setMarker(drawableResId: Int) {
            binding.timeline.marker = binding.timeline.context.getDrawable(drawableResId);
        }
    }

}
