package fba.abadi.bahtera.fajar.kotlin.kitakeren.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fba.abadi.bahtera.fajar.kotlin.kitakeren.R
import fba.abadi.bahtera.fajar.kotlin.kitakeren.databinding.FragmentDeskripsiBinding
import fba.abadi.bahtera.fajar.kotlin.kitakeren.model.Deskripsi
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.BackEndApi
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.WebServiceClient
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.link
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class DeskripsiFragment : Fragment() {
    companion object {
        private var LANG = 0
    }

    private lateinit var binding: FragmentDeskripsiBinding
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_deskripsi, container, false)
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
            WebServiceClient.client.create(BackEndApi::class.java)
                .Des(arguments!!.get("Des").toString())
        observable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LANG = SaveSharedPreference.getLang(context)
                binding.rvDes.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = DesAdapter(it)
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
                    binding.rvDes.adapter?.notifyDataSetChanged()
                    LANG = SaveSharedPreference.getLang(context)
                }
            }, {
                Log.e("gagal 2", it.message!!)
            }))

        val channel = link.push().subscribe("my-channel")
        channel.bind("my-crud-deskripsi") {
            Log.e("Pusher", "Received event with data: ${it.data}")
            val observable =
                WebServiceClient.client.create(BackEndApi::class.java)
                    .Des(arguments!!.get("Des").toString())
            observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LANG = SaveSharedPreference.getLang(context)
                    binding.rvDes.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = DesAdapter(it)
                    }
                }, {
                    Log.e("gagal 1", it.message!!)
                })
        }
    }

    private fun button() {
        binding.btnHomeDes.setOnClickListener {
            val fragment = DashboardFragment()
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame, fragment, fragment.javaClass.simpleName)
                .disallowAddToBackStack()
                .commit()
        }
        binding.btnbackDes.setOnClickListener {
            getFragmentManager()!!.popBackStack()
        }
        binding.btnDDDes.setOnClickListener {
            getFragmentManager()!!.popBackStack()
        }
    }

    inner class DesAdapter(val dataDes: List<Deskripsi>) :
        RecyclerView.Adapter<DesAdapter.HolderDashA>() {
        inner class HolderDashA(iv: View) : RecyclerView.ViewHolder(iv) {
            val name = iv.findViewById<TextView>(R.id.txDeskripsi)
            val img = iv.findViewById<ImageView>(R.id.imvDeskripsi)
            val itemDeskrip = iv.findViewById<CardView>(R.id.ItemFoto)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDashA {
            return HolderDashA(
                LayoutInflater.from(parent.context).inflate(R.layout.list_deskripsi, parent, false)
            )
        }

        override fun getItemCount(): Int = dataDes.size

        override fun onBindViewHolder(holder: HolderDashA, position: Int) {
            when(LANG) {
                0 -> {
                    holder.name.text = dataDes[position].nama_deskripsi.ind
                }
                1 -> {
                    holder.name.text = dataDes[position].nama_deskripsi.en
                }
            }
            Glide.with(context!!)
                .load(WebServiceClient.http + "/" + dataDes[position].icon_deskripsi)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.img)

            holder.itemDeskrip.setOnClickListener {
//                DDF(this@DeskripsiFragment, dataDes[position].language.ind).show()
                val fragment = DetailDeskripsiFragment()
                val b = Bundle()
                b.putString("DDes", dataDes[position].id_deskripsi)
                fragment.arguments = b
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame, fragment, fragment.javaClass.simpleName)
                    .addToBackStack("DeskripsiFragment")
                    .commit()
            }
        }
    }

    class DDF(f: Fragment, judul: String) : Dialog(f.requireContext()), View.OnClickListener {
        val judul = judul
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.fragment_detail_deskripsi)

//            val tx = findViewById<TextView>(R.id.txDetaiJudul)txDetaiJudul
//            tx.text = judul
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }
}