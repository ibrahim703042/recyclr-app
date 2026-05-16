package com.gdsc.recyclr.domain.model

/** Mappe toute erreur Firebase / réseau sans ClassCastException. */
fun Throwable.toFailure(): Response.Failure =
    Response.Failure((this as? Exception) ?: Exception(message ?: toString(), this))
