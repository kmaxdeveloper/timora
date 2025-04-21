package uz.kmax.timora.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import uz.kmax.timora.R

class OnBoardAdapter(val ctx: Context) : RecyclerView.Adapter<OnBoardAdapter.ViewHolder>() {
    private val images = intArrayOf(
        R.raw.lottie_animation_hello_animation,
        R.raw.lottie_animation_planing_works,
        R.raw.lottie_animation_start_and_progress
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.item_on_board, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.animation.setAnimation(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var animation: LottieAnimationView

        init {
            animation = itemView.findViewById(R.id.lottieAnimation)
        }
    }
}