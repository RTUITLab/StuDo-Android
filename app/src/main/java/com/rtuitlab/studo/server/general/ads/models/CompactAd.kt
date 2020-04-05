package com.rtuitlab.studo.server.general.ads.models

data class CompactAd(
    val id : String,
    val name : String,
    val shortDescription : String,
    val beginTime : String,
    val endTime : String,
    val userId: String,
    val userName : String,
    val organizationId: String,
    val organizationName : String?
)