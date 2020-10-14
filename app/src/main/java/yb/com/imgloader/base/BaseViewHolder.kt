package yb.com.imgloader.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView


open class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {


    open fun bind(item: T?) {

    }

}
