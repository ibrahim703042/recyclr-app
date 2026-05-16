package com.gdsc.recyclr.screens.auths.sign_in.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.components.composable.ProgressBar
import com.gdsc.recyclr.domain.model.Response.Failure
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.Response.Success
import com.gdsc.recyclr.screens.auths.sign_in.SignInViewModel
import com.gdsc.recyclr.util.AppLogger
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun SignIn(
    viewModel: SignInViewModel = hiltViewModel(),
    showErrorMessage: (errorMessage: String?) -> Unit
) {
    LaunchedEffect(viewModel) {
        snapshotFlow { viewModel.signInResponse }
            .distinctUntilChanged()
            .collect { resp ->
                when (resp) {
                    is Failure -> {
                        AppLogger.w("Sign-in error", resp.e)
                        showErrorMessage(resp.e.localizedMessage ?: resp.e.message)
                        viewModel.resetSignInResponse()
                    }
                    is Success -> {
                        if (resp.data == true) {
                            viewModel.resetSignInResponse()
                        }
                    }
                    else -> Unit
                }
            }
    }

    if (viewModel.signInResponse is Loading) {
        ProgressBar()
    }
}
