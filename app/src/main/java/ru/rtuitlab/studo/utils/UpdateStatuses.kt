package ru.rtuitlab.studo.utils

import java.io.Serializable

data class UpdateStatuses(
    var isNeedToUpdateAd: Boolean = false,
    var isNeedToUpdateAdsList: Boolean = false,
    var isNeedToUpdateResume: Boolean = false,
    var isNeedToUpdateResumesList: Boolean = false
) : Serializable