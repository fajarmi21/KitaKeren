package fba.abadi.bahtera.fajar.kotlin.kitakeren.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.tntkhang.fullscreenimageview.library.FullScreenImageViewActivity
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import fba.abadi.bahtera.fajar.kotlin.kitakeren.PostersGridDemoActivity
import fba.abadi.bahtera.fajar.kotlin.kitakeren.QuantityFragment
import fba.abadi.bahtera.fajar.kotlin.kitakeren.R
import fba.abadi.bahtera.fajar.kotlin.kitakeren.databinding.FragmentDetailDeskripsiBinding
import fba.abadi.bahtera.fajar.kotlin.kitakeren.model.Demo
import fba.abadi.bahtera.fajar.kotlin.kitakeren.model.Poster
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
        private var READ = 0
        private var Des = ""
    }
    private lateinit var binding: FragmentDetailDeskripsiBinding
    private val disposables = CompositeDisposable()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail_deskripsi,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                })
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    private fun data() {
        WebServiceClient.client.create(BackEndApi::class.java)
            .Rating1(1, arguments!!.get("DDes").toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.size != 0) {
                    val addTv = LayoutInflater.from(context).inflate(
                        R.layout.list_ulasan, null
                    )
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
                Demo.posters.clear()
                if (arguments!!.get("foto").toString() != null) {
                    Demo.posters.add(Poster("""${WebServiceClient.http}/${arguments!!.get("foto").toString()}"""))
                    Glide.with(context!!)
                        .load(WebServiceClient.http + "/" + arguments!!.get("foto").toString())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.GbDetail)
                } else {
                    Demo.posters.add(Poster("""${WebServiceClient.http}/public/assets/icon/logo.png"""))
                    Glide.with(context!!)
                        .load(WebServiceClient.http + "/" + "public/assets/icon/logo.png")
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.GbDetail)
                }
                binding.wa.text = it[0].nomor_detailDeskripsi
                binding.open.text = it[0].buka_detailDeskripsi
                binding.map.text = it[0].alamat_detailDeskripsi
                binding.tiket.text = it[0].harga_detailDeskripsi
                binding.pemandu.text = it[0].pemandu_detailDeskripsi
                mp = it[0].nama_deskripsi.ind
                binding.deskripsiText.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                if (LANG == 0) {
                    binding.deskripsiText.text = it[0].deskripsi_detailDeskripsi.ind
                    binding.jd.text = it[0].nama_deskripsi.ind
                    Des = it[0].deskripsi_detailDeskripsi.ind
                } else {
                    binding.deskripsiText.text = it[0].deskripsi_detailDeskripsi.en
                    binding.jd.text = it[0].nama_deskripsi.en
                    Des = it[0].deskripsi_detailDeskripsi.en
                }
                if (binding.deskripsiText.text.toString().length >= 50) {
                    binding.deskripsiText.text = binding.deskripsiText.text.replaceRange(
                        50,
                        binding.deskripsiText.text.toString().length,
                        " ...."
                    )
                } else {
                    binding.readmore.visibility = View.GONE
                }

                if(it[0].foto_detailDeskripsi != null) {
                    it[0].foto_detailDeskripsi.forEach {t ->
                        Demo.posters.add(Poster(t))
                    }
                    Log.e("jmlh", it[0].foto_detailDeskripsi.size.toString())
                    if (it[0].foto_detailDeskripsi.size != 5) {
                        for (i in 1 .. 6 - Demo.posters.size) {
                            Demo.posters.add(Poster("""${WebServiceClient.http}/public/assets/icon/logo.png"""))
                        }
                    }
                } else {
                    Demo.posters.add(Poster("""${WebServiceClient.http}/public/assets/icon/logo.png"""))
                    Demo.posters.add(Poster("""${WebServiceClient.http}/public/assets/icon/logo.png"""))
                    Demo.posters.add(Poster("""${WebServiceClient.http}/public/assets/icon/logo.png"""))
                    Demo.posters.add(Poster("""${WebServiceClient.http}/public/assets/icon/logo.png"""))
                    Demo.posters.add(Poster("""${WebServiceClient.http}/public/assets/icon/logo.png"""))
                }
                Demo.posters.forEach {
                    Log.e("gambar", it.url)
                }

                binding.GbLain.setOnClickListener {
                    val intent = Intent(context, PostersGridDemoActivity::class.java)
                    if (arguments!!.get("foto").toString() != null) {
                        intent.putExtra("foto", """${WebServiceClient.http}/${arguments!!.get("foto").toString()}""")
                    } else {
                        intent.putExtra("foto", """${WebServiceClient.http}/public/assets/icon/logo.png""")
                    }
                    intent.putExtra("DDes", arguments!!.get("DDes").toString())
                    startActivity(intent)
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
                binding.readmore.setOnClickListener {
                    if (READ == 0) {
                        binding.deskripsiText.text = binding.deskripsiText.text.replaceRange(
                            50,
                            binding.deskripsiText.text.toString().length,
                            " ...."
                        )
                        binding.readmore.text = "Read More"
                        READ = 1
                    } else {
                        binding.deskripsiText.text = Des
                        binding.readmore.text = "Read Less"
                        READ = 0
                    }
                }
                binding.klikTiket.setOnClickListener {
                    val fragment = QuantityFragment()
//                    val b = Bundle()
//                    b.putString("idDash", arguments!!.get("DDes").toString())
//                    b.putString("foto", arguments!!.get("foto").toString())
//                    fragment.arguments = b
                    activity!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, fragment, fragment.javaClass.simpleName)
                        .addToBackStack("DetailDeskripsiFragment")
                        .commit()
                }
            }, {
                Log.e("gagal 1", it.message!!)
            })

        WebServiceClient.client.create(BackEndApi::class.java)
            .Rating1(2, arguments!!.get("DDes").toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var x = 0.0
                it.forEach {
                    if (x == 0.0) x = it.rating.toDouble()
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
            b.putString("foto", arguments!!.get("foto").toString())
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