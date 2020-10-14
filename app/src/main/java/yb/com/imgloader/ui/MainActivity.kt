package yb.com.imgloader.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import yb.com.imgloader.ImageConfig
import yb.com.imgloader.R
import yb.com.imgloader.adapter.ThumbAdapter
import yb.com.imgloader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewDataBinding: ActivityMainBinding

    private val thumbAdapter by lazy {
        ThumbAdapter().apply {
            setHasStableIds(true)
        }
    }

    val temp = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        viewDataBinding.rcThumb.apply {
            setHasFixedSize(true)
            adapter = thumbAdapter
        }


        getImageUrl()


    }

    @SuppressLint("CheckResult")
    private fun getImageUrl() {
        Flowable.just(Jsoup.connect(ImageConfig.BASE_URL))
            .subscribeOn(Schedulers.io())
            .map {
                val temp = ArrayList<String>()
                it.get().select(ImageConfig.PARAM_SELECT_QUERY).forEach {
                    temp.add(it.attr(ImageConfig.PARAM_CONTENT))
                }
                temp
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                temp.addAll(it)
                thumbAdapter.setData(temp)
            }
    }
}