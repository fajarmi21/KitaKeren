package fba.fajarbahtera.kitakeren.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import fba.abadi.bahtera.fajar.kotlin.kitakeren.R
import fba.abadi.bahtera.fajar.kotlin.kitakeren.databinding.FragmentDetailUlasanBinding
import fba.abadi.bahtera.fajar.kotlin.kitakeren.model.rating
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.BackEndApi
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.WebServiceClient
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference.getemail
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_detail_ulasan.*
import java.util.concurrent.TimeUnit

class DetailUlasanFragment : Fragment(), RatingDialogListener {
    companion object {
        private var LANG = 0
    }

    private lateinit var binding: FragmentDetailUlasanBinding
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_ulasan, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        disposables.add(
            Observable.interval(
                1000, 1000,
                TimeUnit.MILLISECONDS
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (LANG != SaveSharedPreference.getLang(context)) {
                        binding.rvUlasan.adapter?.notifyDataSetChanged()
                        LANG = SaveSharedPreference.getLang(context)
                        data()
                    }
                }, {
                    Log.e("gagal 2", it.message!!)
                }))
        data()
        btCerita.setOnClickListener {
            AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(listOf("Buruk Sekali", "Buruk", "Biasa", "Bagus", "Bagus Sekali !!!"))
                .setDefaultRating(5)
                .setTitle("Rate this application")
                .setDescription("Please select some stars and give your feedback")
                .setStarColor(R.color.starColor)
                .setNoteDescriptionTextColor(R.color.noteDescriptionTextColor)
                .setTitleTextColor(R.color.black)
                .setDescriptionTextColor(R.color.black)
                .setCommentTextColor(R.color.black)
                .setCommentBackgroundColor(R.color.teal_200)
                .setWindowAnimation(R.style.MyDialogSlideHorizontalAnimation)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.hintTextColor)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(activity!!)
                .setTargetFragment(this, 0)
                .show()
        }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    private fun data() {
        if (arguments!!.get("foto").toString() != null) {
            Glide.with(context!!)
                    .load(WebServiceClient.http + "/" + arguments!!.get("foto").toString())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.PP)
        } else {
            Glide.with(context!!)
                    .load(WebServiceClient.http + "/" + "public/assets/icon/logo.png")
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.PP)
        }
        WebServiceClient.client.create(BackEndApi::class.java)
            .Ddes(arguments!!.get("idDash").toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (LANG == 0) {
                    binding.JdUl.text = it[0].nama_deskripsi.ind
                } else {
                    binding.JdUl.text = it[0].nama_deskripsi.ind
                }
            }, {
                Log.e("gagal 1", it.message!!)
            })

        WebServiceClient.client.create(BackEndApi::class.java)
            .Rating1(2,arguments!!.get("idDash").toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var x = 0.0
                var y = 0
                it.forEach {
                    y++
                    if ( x == 0.0)  x = it.rating.toDouble()
                    else x = (x + it.rating.toFloat()) / 2
                }
                binding.numRU.text = x.toString()
                binding.numCU.text = "($y ulasan)"
            }, {
                Log.e("gagal 1", it.message!!)
            })

        WebServiceClient.client.create(BackEndApi::class.java)
            .Rating1(2,arguments!!.get("idDash").toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.rvUlasan.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = Rating(it)
                }
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

    override fun onNegativeButtonClicked() {
        data()
    }

    override fun onNeutralButtonClicked() {
        data()
    }

    @SuppressLint("CheckResult")
    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        try {
            WebServiceClient.client.create(BackEndApi::class.java)
                .WRating(
                    arguments!!.get("idDash").toString(),
                    getemail(context).toString(),
                    rate.toString(),
                    comment
                )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.status == "0") {
                        data()
                    }
                }, {
                    Log.e("gagal 1", it.message!!)
                })
        } catch (e: Exception){
            Log.e("gagal 1", e.message!!)
        }
    }

    inner class Rating (val rating:List<rating>):
        RecyclerView.Adapter<Rating.HolderRat>(){
        inner class HolderRat(iv: View): RecyclerView.ViewHolder(iv){
            val rat = iv.findViewById<SimpleRatingBar>(R.id.RBar)
            val user = iv.findViewById<TextView>(R.id.txNamaR)
            val tgl = iv.findViewById<TextView>(R.id.tglU)
            val comm = iv.findViewById<TextView>(R.id.commentU)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRat {
            return HolderRat(LayoutInflater.from(parent.context).inflate(R.layout.list_ulasan, parent, false))
        }

        override fun getItemCount(): Int = rating.size

        override fun onBindViewHolder(holder: HolderRat, position: Int) {
            val d = rating[position]
            holder.rat.rating = d.rating.toFloat()
            holder.user.text = d.username
            if (d.updated_rating == "0000-00-00") {
                holder.tgl.text = d.created_rating
            } else {
                holder.tgl.text = d.updated_rating
            }
            holder.comm.text = d.comment
        }
    }
}