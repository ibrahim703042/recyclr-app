package com.gdsc.recyclr.screens.auths.sign_up.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.components.composable.ProgressBar
import com.gdsc.recyclr.domain.model.Response.Failure
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.Response.Success
import com.gdsc.recyclr.screens.auths.sign_up.SignUpViewModel
import com.gdsc.recyclr.util.AppLogger
import kotlinx.coroutines.flow.distinctUntilChanged

@ExperimentalMaterialApi
@Composable
fun SignUp(
    viewModel: SignUpViewModel = hiltViewModel(),
    showErrorMessage: (String?) -> Unit,
) {
    LaunchedEffect(viewModel) {
        snapshotFlow { viewModel.signUpResponse }
            .distinctUntilChanged()
            .collect { resp ->
                when (resp) {
                    is Failure -> {
                        AppLogger.w("Sign-up error", resp.e)
                        showErrorMessage(resp.e.localizedMessage ?: resp.e.message)
                        viewModel.resetSignUpResponse()
                    }
                    is Success -> {
                        if (resp.data == true) viewModel.resetSignUpResponse()
                    }
                    else -> Unit
                }
            }
    }

    LaunchedEffect(viewModel) {
        snapshotFlow { viewModel.phoneSignInResponse }
            .distinctUntilChanged()
            .collect { resp ->
                when (resp) {
                    is Failure -> {
                        AppLogger.w("Phone sign-up error", resp.e)
                        showErrorMessage(resp.e.localizedMessage ?: resp.e.message)
                        viewModel.resetPhoneSignInResponse()
                    }
                    is Success -> {
                        if (resp.data == true) viewModel.resetPhoneSignInResponse()
                    }
                    else -> Unit
                }
            }
    }

    if (viewModel.signUpResponse is Loading || viewModel.phoneSignInResponse is Loading) {
        ProgressBar()
    }
}
