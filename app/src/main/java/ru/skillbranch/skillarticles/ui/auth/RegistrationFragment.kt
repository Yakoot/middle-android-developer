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

    private val nameRegex = Regex("^[\\w\\d-_]{3,}$")
    private val passRegex = Regex("^[\\w\\d]{8,}$")

    override fun setupViews() {

        btn_register.setOnClickListener {

            validateName()
            validateLogin()
            validatePassword()
            if (areThereInputErrors()) return@setOnClickListener

            viewModel.handleRegister(
                et_name.text.trim().toString(),
                et_login.text.trim().toString(),
                et_password.text.trim().toString(),
                if (args.privateDestination == -1) null else args.privateDestination
            )
        }

        et_name.doAfterTextChanged { validateName(false) }
        et_login.doAfterTextChanged { validateLogin(false) }
        et_password.doAfterTextChanged { validatePassword(false) }

    }

    private fun validateName(showError: Boolean = true) {
        if (nameRegex.matches(et_name.text.trim())) {
            wrap_name.isErrorEnabled = false
            wrap_name.error = null
        }
        else if (showError) wrap_name.error =
            "The name must be at least 3 characters long and contain only letters and numbers and can also contain the characters \"-\" and \"_\""
    }

    private fun validateLogin(showError: Boolean = true) {
        if (et_login.text.isNotBlank()) {
            wrap_login.isErrorEnabled = false
            wrap_login.error = null
        }
        else if (showError) wrap_login.error = "Incorrect Email entered"
    }

    private fun validatePassword(showError: Boolean = true) {
        if (passRegex.matches(et_password.text.trim())) {
            wrap_password.isErrorEnabled = false
            wrap_password.error = null
        }
        else if (showError) wrap_password.error =
            "Password must be at least 8 characters long and contain only letters and numbers"
    }

    private fun areThereInputErrors(): Boolean {
        return wrap_name.error != null || wrap_login.error != null || wrap_password.error != null
    }
}