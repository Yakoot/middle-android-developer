package ru.skillbranch.skillarticles.ui.auth

import androidx.annotation.VisibleForTesting
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.android.synthetic.main.fragment_registration.*
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.viewmodels.auth.AuthViewModel
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.RootActivity


class RegistrationFragment() : BaseFragment<AuthViewModel>() {
    override val layout: Int = R.layout.fragment_registration

    // for testing
    var _mockFactory: ((SavedStateRegistryOwner) -> ViewModelProvider.Factory)? = null

    override val viewModel: AuthViewModel by viewModels {
        _mockFactory?.invoke(this) ?: defaultViewModelProviderFactory
    }

    // testing constructors
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    constructor(
        mockRoot: RootActivity,
        mockFactory: ((SavedStateRegistryOwner) -> ViewModelProvider.Factory)? = null
    ) : this() {
        _mockRoot = mockRoot
        _mockFactory = mockFactory
    }

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