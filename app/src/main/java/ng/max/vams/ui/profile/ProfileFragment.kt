package ng.max.vams.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import ng.max.vams.R
import ng.max.vams.data.manager.UserManager
import ng.max.vams.databinding.FragmentProfileBinding
import ng.max.vams.util.showDialog
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var bnd : FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = FragmentProfileBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }
    private fun setupView(){
        UserManager.getUser()?.let { user->
            if (user.photo != null){
                bnd.profileIcon.load(user.photo){
                    placeholder(R.drawable.ic_icon_placeholder)
                            .transformations(CircleCropTransformation())
                }
            }else{
                bnd.profileIcon.load(R.drawable.ic_icon_placeholder){
                    transformations(CircleCropTransformation())
                }
            }
            bnd.usernameTv.text = user.fullName
            bnd.roleTv.text = user.role.capitalize(Locale.getDefault())
        }

        bnd.settingsOption.setOnClickListener {

        }
        bnd.signOutOption.setOnClickListener {
            showDialog("Info", "Sign out from VAMS?", true)
        }
        bnd.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    companion object {
    }
}