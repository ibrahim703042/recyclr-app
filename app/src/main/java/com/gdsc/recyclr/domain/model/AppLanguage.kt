package com.gdsc.recyclr.domain.model

enum class AppLanguage {
    SYSTEM,
    EN,
    FR,
    ;

    companion object {
        fun fromStorage(value: String?): AppLanguage =
            entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}
