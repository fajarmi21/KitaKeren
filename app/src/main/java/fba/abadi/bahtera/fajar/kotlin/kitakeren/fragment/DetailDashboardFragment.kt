package fba.abadi.bahtera.fajar.kotlin.kitakeren.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fba.abadi.bahtera.fajar.kotlin.kitakeren.R
import fba.abadi.bahtera.fajar.kotlin.kitakeren.databinding.FragmentDetailDashboardBinding
import fba.abadi.bahtera.fajar.kotlin.kitakeren.model.DDashboard
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.BackEndApi
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.WebServiceClient
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.link
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class DetailDashboardFragment : Fragment() {
    companion object {
        private var LANG = 0
    }

    private lateinit var binding: FragmentDetailDashboardBinding
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_dashboard, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        data()
        button()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    @SuppressLint("CheckResult")
    private fun data() {
        val observable =
            WebServiceClient.client.create(BackEndApi::class.java).Ddash(arguments!!.get("Dash").toString())
        observable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.rvDetailDashh.apply {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = DetDashAdapter(it)
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
                if (LANG != SaveSharedPreference.getLang(context)) {
                    binding.rvDetailDashh.adapter?.notifyDataSetChanged()
                    LANG = SaveSharedPreference.getLang(context)
                }
            }, {
                Log.e("gagal 2", it.message!!)
            }))

        val channel = link.push().subscribe("my-channel")
        channel.bind("my-crud-detail-dashboard") {
            Log.e("Pusher", "Received event with data: ${it.data}")
            val observable =
                WebServiceClient.client.create(BackEndApi::class.java).Ddash(arguments!!.get("Dash").toString())
            observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.rvDetailDashh.apply {
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = DetDashAdapter(it)
                    }
                }, {
                    Log.e("gagal 1", it.message!!)
                })
        }
    }

    private fun button() {
        binding.btnHomeDD.setOnClickListener {
            val fragment = DashboardFragment()
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame, fragment, fragment.javaClass.simpleName)
                .disallowAddToBackStack()
                .commit()
        }
        binding.btnbackDD.setOnClickListener {
            val fragment = DashboardFragment()
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame, fragment, fragment.javaClass.simpleName)
                .disallowAddToBackStack()
                .commit()
        }
    }

    inner class DetDashAdapter (val dataDetDash:List<DDashboard>):
        RecyclerView.Adapter<DetDashAdapter.HolderDashA>(){
        inner class HolderDashA(iv: View): RecyclerView.ViewHolder(iv){
            val name = iv.findViewById<TextView>(R.id.textView)
            val img = iv.findViewById<ImageView>(R.id.imageView)
            val itemDeskrip = iv.findViewById<CardView>(R.id.ItemDash)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDashA {
            return HolderDashA(LayoutInflater.from(parent.context).inflate(R.layout.list_dashboard, parent, false))
        }

        override fun getItemCount(): Int = dataDetDash.size

        override fun onBindViewHolder(holder: HolderDashA, position: Int) {
            when(LANG) {
                0 -> {
                    holder.name.text = dataDetDash[position].nama_detailDashboard.ind
                }
                1 -> {
                    holder.name.text = dataDetDash[position].nama_detailDashboard.en
                }
            }
            Glide.with(context!!)
                .load(WebServiceClient.http+"/"+dataDetDash[position].icon_detailDashboard)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.img)
            holder.itemDeskrip.setOnClickListener {
                val fragment = DeskripsiFragment()
                val b = Bundle()
                b.putString("Des", dataDetDash[position].id_detailDashboard)
                fragment.arguments = b
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame, fragment, fragment.javaClass.simpleName)
                    .addToBackStack("DetailDashboardFragment")
                    .commit()
            }
        }
    }
}