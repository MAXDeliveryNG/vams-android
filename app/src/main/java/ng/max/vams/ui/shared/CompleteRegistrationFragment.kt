package ng.max.vams.ui.shared

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ng.max.vams.R

class CompleteRegistrationFragment : Fragment() {

    //private lateinit var bnd:suspend Com

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_registration, container, false)
    }

    companion object {
    }
}