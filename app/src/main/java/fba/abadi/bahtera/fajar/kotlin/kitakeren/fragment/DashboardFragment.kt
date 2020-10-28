package fba.abadi.bahtera.fajar.kotlin.kitakeren.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fba.abadi.bahtera.fajar.kotlin.kitakeren.R
import fba.abadi.bahtera.fajar.kotlin.kitakeren.databinding.FragmentDashboardBinding
import fba.abadi.bahtera.fajar.kotlin.kitakeren.model.dashboard
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.BackEndApi
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.WebServiceClient
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference.getLang
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.link
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.link.Companion.push
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import technolifestyle.com.imageslider.FlipperLayout
import technolifestyle.com.imageslider.FlipperView
import technolifestyle.com.imageslider.pagetransformers.ZoomOutPageTransformer
import java.util.*
import java.util.concurrent.TimeUnit

class DashboardFragment : Fragment() {
    companion object {
        private var currentPage = 0
        private var NUM_PAGES = 0
        private var LANG = 0
    }
    private lateinit var binding: FragmentDashboardBinding
    private val disposables = CompositeDisposable()
    private lateinit var flipperLayout: FlipperLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        data()
        init()

        binding.link1.setOnClickListener { startActivity(
            link.newInstagramProfileIntent(
                activity!!.packageManager,
                "https://instagram.com/kediritourism?igshid=8sj0q36w5swh"
            )
        ) }
        binding.link2.setOnClickListener { startActivity(
            link.newFacebookIntent(
                activity!!.packageManager,
                "https://www.facebook.com/disbudparporakotakediri.official"
            )
        ) }
        binding.link3.setOnClickListener { startActivity(
            link.newWebsiteIntent(
                "https://www.kediritourism.net/"
            )
        ) }
        binding.link4.setOnClickListener { startActivity(
            link.newWebsiteIntent(
                "https://www.youtube.com/channel/UCk5z7OavRmGb4tddpA9N7xA"
            )
        ) }
        binding.web.setOnClickListener { startActivity(
            link.newWebsiteIntent(
                "https://www.kediritourism.net/"
            )
        ) }
        binding.email.setOnClickListener { startActivity(
            link.newEmailIntent(
                activity!!.packageManager,
                "disbudparporakotakediri@gmail.com"
            )
        ) }
        binding.twitter.setOnClickListener { startActivity(
            link.newWebsiteIntent(
                "https://twitter.com/kediritourism?s=09"
            )
        ) }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    @SuppressLint("CheckResult")
    private fun data() {
        val observable =
            WebServiceClient.client.create(BackEndApi::class.java).dash("1")
        observable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var adapt = DashAdapter(it)
                binding.rvDashh.apply {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = adapt
                }
            }, {
                Log.e("gagal 1", it.message!!)
            })


        disposables.add(Observable.interval(
            1000, 1000,
            TimeUnit.MILLISECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (LANG != getLang(context)) {
                    binding.rvDashh.adapter?.notifyDataSetChanged()
                    LANG = getLang(context)
                }
            }, {
                Log.e("gagal 2", it.message!!)
            }))

        val channel = push().subscribe("my-channel")
        channel.bind("my-crud-dashboard") {
            Log.e("Pusher", "Received event with data: ${it.data}")
            val observable =
                WebServiceClient.client.create(BackEndApi::class.java).dash("1")
            observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
//                        LANG = getLang(context)
                    var adapt = DashAdapter(it)
                    binding.rvDashh.apply {
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = adapt
                    }
                }, {
                    Log.e("gagal 1", it.message!!)
                })
        }
    }

    private fun init() {
        val url = arrayOf(
            R.drawable.gb1,
            R.drawable.gb2,
            R.drawable.gb3,
            R.drawable.gb4,
            R.drawable.gb5,
            R.drawable.gb6
        )

        flipperLayout = view!!.findViewById(R.id.flipper_layout)
        flipperLayout.addPageTransformer(false, ZoomOutPageTransformer())
        val flipperViewList: ArrayList<FlipperView> = ArrayList()
        for (i in url.indices) {
            val view = FlipperView(context!!)
            view.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setImage(url[i]) { flipperImageView, image ->
                    flipperImageView.setImageDrawable(image as Drawable)
//                    Picasso.get().load(image.toString()).into(flipperImageView)
                }
            view.setOnFlipperClickListener(object : FlipperView.OnFlipperClickListener {
                override fun onFlipperClick(flipperView: FlipperView) {
//                    Toast.makeText(this@MainActivity, "Here " + (flipperLayout.currentPagePosition + 1), Toast.LENGTH_SHORT).show()
                }
            })
            flipperViewList.add(view)
        }
        flipperLayout.addFlipperViewList(flipperViewList)
        flipperLayout.scrollTimeInSec = 5
        flipperLayout.removeCircleIndicator()
        flipperLayout.showInnerPagerIndicator()
    }

    inner class DashAdapter(val dataDash: List<dashboard>):
        RecyclerView.Adapter<DashAdapter.HolderDashA>(){
        inner class HolderDashA(iv: View): RecyclerView.ViewHolder(iv){
            val name = iv.findViewById<TextView>(R.id.textView)
            val img = iv.findViewById<ImageView>(R.id.imageView)
            val itemDeskrip = iv.findViewById<CardView>(R.id.ItemDash)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDashA {
            return HolderDashA(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_dashboard,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int = dataDash.size

        override fun onBindViewHolder(holder: HolderDashA, position: Int) {
            when(LANG) {
                0 -> {
                    holder.name.text = dataDash[position].nama_dashboard.ind
                }
                1 -> {
                    holder.name.text = dataDash[position].nama_dashboard.en
                }
            }
            Glide.with(context!!)
                .load(WebServiceClient.http + "/" + dataDash[position].icon_dashboard)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.img)

            holder.itemDeskrip.setOnClickListener {
                val fragment = DetailDashboardFragment()
                val b = Bundle()
                b.putString("Dash", dataDash[position].id_dashboard)
                fragment.arguments = b
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame, fragment, fragment.javaClass.simpleName)
                    .addToBackStack("DashboardFragment")
                    .commit()
            }

        }
    }
}