package yb.com.imgloader.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import yb.com.imgloader.ImageApplication.Companion.getGlobalContext
import yb.com.imgloader.R
import yb.com.imgloader.base.BaseViewHolder
import yb.com.imgloader.base.BindableAdapter
import yb.com.imgloader.databinding.ItemThumbBinding
import yb.com.imgloader.util.NetworkUtil
import java.util.concurrent.Executors


class ThumbAdapter() : RecyclerView.Adapter<BaseViewHolder<String>>(),
    BindableAdapter<ArrayList<String>> {

    var fixThread = Executors.newFixedThreadPool(4)

    var itemList = MutableLiveData<List<String>>()

    override fun setData(items: ArrayList<String>?) {
        itemList.value = items
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BaseViewHolder<String> {
        return PackageViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_thumb,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int = itemList.value?.size ?: 0

    override fun getItemId(position: Int): Long {
        return if (itemList.value?.isEmpty() == true) {
            0
        } else {
            itemList.value?.get(position).hashCode().toLong()
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<String>, position: Int) =
        holder.bind(itemList.value?.get(position))

    inner class PackageViewHolder(val binding: ItemThumbBinding) :
        BaseViewHolder<String>(binding.root) {
        override fun bind(item: String?) {
            with(itemView) {
                item?.let { url ->
                    binding.ivThumb.tag = url
                    binding.ivThumb.setImageBitmap(null)

                    fixThread.submit {
                        Observable.create(ObservableOnSubscribe<Bitmap> {
                            getGlobalContext().cache.get(item)?.also { bitmap ->
                                it.onNext(bitmap)
                            } ?: run {
                                getBitmapFrom(url, it)
                            }
                        })
                            .subscribe { thumNail ->
                                if (binding.ivThumb.tag == item) {
                                    binding.ivThumb.setImageBitmap(thumNail)
                                }
                            }
                    }
                }
            }
        }
    }

    fun getBitmapFrom(url: String, fileEmitter: ObservableEmitter<Bitmap?>) {
        NetworkUtil.getRetrofit().downloadFile(url)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response == null || !response.isSuccessful || response.body() == null || response.errorBody() != null) {
                        fileEmitter.onComplete()
                        return
                    }
                    val bytes = response.body()!!.bytes()
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 4
                    }

                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options).apply {
                        getGlobalContext().cache.put(url, this)
                        fileEmitter.onNext(this)
                        fileEmitter.onComplete()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, e: Throwable) {
                    fileEmitter.onError(e)
                }

            })
    }
}

