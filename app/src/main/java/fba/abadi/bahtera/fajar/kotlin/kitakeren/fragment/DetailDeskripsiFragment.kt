package fba.abadi.bahtera.fajar.kotlin.kitakeren.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import fba.abadi.bahtera.fajar.kotlin.kitakeren.R
import fba.abadi.bahtera.fajar.kotlin.kitakeren.databinding.FragmentDetailDeskripsiBinding
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.BackEndApi
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.WebServiceClient
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.link
import fba.fajarbahtera.kitakeren.fragment.DetailUlasanFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_detail_deskripsi.*
import java.util.concurrent.TimeUnit

class DetailDeskripsiFragment : Fragment() {
    companion object {
        private var LANG = 0
        private var mp = ""
    }
    private lateinit var binding: FragmentDetailDeskripsiBinding
    private val disposables = CompositeDisposable()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_deskripsi, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        button()
//        Log.e("xx", arguments!!.get("DDes").toString())
        data()
        disposables.add(
            Observable.interval(
                1000, 1000,
                TimeUnit.MILLISECONDS
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (LANG != SaveSharedPreference.getLang(context)) {
                        LANG = SaveSharedPreference.getLang(context)
                        data()
                    }
                }, {
                    Log.e("gagal 2", it.message!!)
                }))
    }

    @SuppressLint("CheckResult")
    private fun data() {
        WebServiceClient.client.create(BackEndApi::class.java)
            .Rating1(1, arguments!!.get("DDes").toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.size != 0) {
                    val addTv = LayoutInflater.from(context).inflate(
                            R.layout.list_ulasan, null)
                    addTv.findViewById<TextView>(R.id.txNamaR).text = it[0].username
                    addTv.findViewById<TextView>(R.id.commentU).text = it[0].username
                    addTv.findViewById<SimpleRatingBar>(R.id.RBar).rating = it[0].rating.toFloat()
                    binding.llinclude.addView(addTv)
                }
//                binding.iListU.txNamaR.text = it[0].username
//                binding.iListU.commentU.text = it[0].comment
//                binding.iListU.RBar.rating = it[0].rating.toFloat()
            }, {
                Log.e("gagal 1", it.message!!)
            })

        WebServiceClient.client.create(BackEndApi::class.java)
            .Ddes(arguments!!.get("DDes").toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.wa.text = it[0].nomor_detailDeskripsi
                binding.open.text = it[0].buka_detailDeskripsi
                binding.map.text = it[0].alamat_detailDeskripsi
                binding.tiket.text = it[0].harga_detailDeskripsi
                binding.pemandu.text = it[0].pemandu_detailDeskripsi
                mp = it[0].nama_deskripsi.ind
                if (LANG == 0) {
                    binding.deskripsiText.text = it[0].deskripsi_detailDeskripsi.ind
                    binding.jd.text = it[0].nama_deskripsi.ind
                } else {
                    binding.deskripsiText.text = it[0].deskripsi_detailDeskripsi.en
                    binding.jd.text = it[0].nama_deskripsi.en
                }
                map.setOnClickListener {
                    startActivity(
                        link.newMapsIntent(
                            activity!!.packageManager,
                            mp
                        )
                    )
                }
                wa.setOnClickListener {
                    startActivity(
                        link.newWhatappIntent(
                            activity!!.packageManager,
                            wa.text.toString(),
                            "https://kitakeren.fajarbahteraabadi.com"
                        )
                    )
                }
            }, {
                Log.e("gagal 1", it.message!!)
            })

        WebServiceClient.client.create(BackEndApi::class.java)
            .Rating1(2,arguments!!.get("DDes").toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var x = 0.0
                it.forEach {
                    if ( x == 0.0)  x = it.rating.toDouble()
                    else x = (x + it.rating.toFloat()) / 2
                }
                binding.Drating.text = x.toString()
            }, {
                Log.e("gagal 1", it.message!!)
            })

//        val channel = link.push().subscribe("my-channel")
//        channel.bind("my-crud-rating") {
//            Log.e("Pusher", "Received event with data: ${it.data}")
//            val observable =
//                WebServiceClient.client.create(BackEndApi::class.java)
//                    .Rating1(1)
//            observable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    LANG = SaveSharedPreference.getLang(context)
//                    binding.rvUlasan.apply {
//                        layoutManager = LinearLayoutManager(context)
//                        adapter = Rating(it)
//                    }
//                }, {
//                    Log.e("gagal 1", it.message!!)
//                })
//        }
    }

    private fun button() {
        binding.LSemua.setOnClickListener {
            val fragment = DetailUlasanFragment()
            val b = Bundle()
            b.putString("idDash", arguments!!.get("DDes").toString())
            fragment.arguments = b
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame, fragment, fragment.javaClass.simpleName)
                .addToBackStack("DetailDeskripsiFragment")
                .commit()
        }
        binding.btnbackDDes.setOnClickListener {
            getFragmentManager()!!.popBackStack()
        }
        binding.btnDDDDes.setOnClickListener {
            getFragmentManager()!!.popBackStack()
        }
    }
}