package fba.abadi.bahtera.fajar.kotlin.kitakeren

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class QuantityFragment : Fragment() {

    companion object {
        fun newInstance() = QuantityFragment()
    }

    private lateinit var viewModel: QuantityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.quantity_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(QuantityViewModel::class.java)
        // TODO: Use the ViewModel
    }

}