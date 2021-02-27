package ru.skillbranch.skillarticles.ui.auth

import androidx.annotation.VisibleForTesting
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.savedstate.SavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_registration.*
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.viewmodels.auth.AuthViewModel
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.RootActivity

@AndroidEntryPoint
class RegistrationFragment() : BaseFragment<AuthViewModel>() {
    override val layout: Int = R.layout.fragment_registration

    override val viewModel: AuthViewModel by activityViewModels()

    private val args: RegistrationFragmentArgs by navArgs()

    override fun setupViews() {

        btn_register.setOnClickListener {
            viewModel.handleRegister(
                et_name.text.trim().toString(),
                et_login.text.trim().toString(),
                et_password.text.trim().toString(),
                if (args.privateDestination == -1) null else args.privateDestination
            )
        }
    }
}