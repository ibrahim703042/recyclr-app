package com.gdsc.recyclr.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.gdsc.recyclr.domain.model.AppLanguage

object AppLocaleManager {
    fun apply(language: AppLanguage) {
        val tags = when (language) {
            AppLanguage.SYSTEM -> ""
            AppLanguage.EN -> "en"
            AppLanguage.FR -> "fr"
        }
        AppCompatDelegate.setApplicationLocales(
            if (tags.isEmpty()) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(tags)
            },
        )
    }
}
