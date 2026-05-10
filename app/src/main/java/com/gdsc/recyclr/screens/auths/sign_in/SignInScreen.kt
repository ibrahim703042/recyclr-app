@file:Suppress("DEPRECATION")

package com.gdsc.recyclr.screens.auths.sign_in

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.utils.UiUtils.showMessage
import com.gdsc.recyclr.screens.auths.sign_in.components.SignIn
import com.gdsc.recyclr.screens.auths.sign_in.components.SignInContent
import com.gdsc.recyclr.util.AppLogger

@Composable
@ExperimentalComposeUiApi
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navigateToForgotPasswordScreen: () -> Unit,
    navigateToSignUpScreen: () -> Unit,
    continueAsGuest: () -> Unit,
) {
    val vm = viewModel
    val context = LocalContext.current
    val activity = context as ComponentActivity

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        runCatching {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            vm.signInWithGoogleIdToken(account.idToken)
        }.onFailure {
            AppLogger.w("Google Sign-In résultat", it)
            if (it is ApiException && it.statusCode == 12501) return@onFailure
            if (it !is ApiException) {
                showMessage(context, it.message)
            } else {
                showMessage(context, "Google : ${it.statusCode} — ${it.message}")
            }
        }
    }

    SignInContent(
        signIn = { email, password -> vm.signInWithEmailAndPassword(email, password) },
        navigateToForgotPasswordScreen = navigateToForgotPasswordScreen,
        navigateToSignUpScreen = navigateToSignUpScreen,
        continueAsGuest = continueAsGuest,
        phoneHint = vm.phoneHint,
        phoneVerificationId = vm.phoneVerificationId,
        onGoogleClick = {
            runCatching {
                val webId = context.getString(R.string.default_web_client_id)
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webId)
                    .requestEmail()
                    .requestProfile()
                    .build()
                val client = GoogleSignIn.getClient(context, gso)
                googleLauncher.launch(client.signInIntent)
            }.onFailure { e ->
                AppLogger.e("GoogleSignIn non disponible", e)
                showMessage(context, "Connexion Google indisponible sur cet appareil.")
            }
        },
        onSendPhoneCode = { phone -> vm.startPhoneVerification(activity, phone) },
        onVerifyPhoneCode = { code -> vm.verifyPhoneSmsCode(code) }
    )

    SignIn(
        viewModel = vm,
        showErrorMessage = { msg -> showMessage(context, msg) }
    )
}
