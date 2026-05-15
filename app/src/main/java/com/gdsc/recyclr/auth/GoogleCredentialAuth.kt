package com.gdsc.recyclr.auth

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/**
 * Connexion Google via Credential Manager + jeton ID, comme décrit dans la doc Firebase
 * (remplace l’ancien Google Sign-In basé sur une Activity de résultat).
 */
object GoogleCredentialAuth {

    /**
     * Affiche le sélecteur de compte Google et renvoie le jeton ID à passer à Firebase
     * ([com.google.firebase.auth.GoogleAuthProvider.getCredential]).
     *
     * @param filterByAuthorizedAccounts `false` pour proposer tous les comptes (première connexion).
     */
    suspend fun getGoogleIdToken(
        activity: ComponentActivity,
        serverClientId: String,
        filterByAuthorizedAccounts: Boolean = false,
    ): Result<String> = try {
        val credentialManager = CredentialManager.create(activity)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(serverClientId)
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response = credentialManager.getCredential(
            context = activity,
            request = request,
        )
        Result.success(parseGoogleIdToken(response.credential))
    } catch (e: GetCredentialCancellationException) {
        Result.failure(GoogleSignInCancelled())
    } catch (e: GetCredentialException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun parseGoogleIdToken(credential: Credential): String {
        if (credential !is CustomCredential) {
            error("Type d’identifiant inattendu")
        }
        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            error("Type d’identifiant inattendu : ${credential.type}")
        }
        return try {
            val parsed = GoogleIdTokenCredential.createFrom(credential.data)
            parsed.idToken ?: error("Jeton Google manquant")
        } catch (e: GoogleIdTokenParsingException) {
            throw e
        }
    }

    /** À appeler à la déconnexion, en complément de [com.google.firebase.auth.FirebaseAuth.signOut]. */
    suspend fun clearCredentialState(context: Context) {
        runCatching {
            val cm = CredentialManager.create(context.applicationContext)
            cm.clearCredentialState(ClearCredentialStateRequest())
        }
    }
}

/** Annulation utilisateur du flux Credential Manager (équivalent ancien code 12501). */
class GoogleSignInCancelled : Exception()
