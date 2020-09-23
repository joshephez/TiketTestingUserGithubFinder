package com.gudangada.tikettesting.response

import com.google.gson.annotations.SerializedName
import com.gudangada.tikettesting.model.User

data class Response(
    @SerializedName("total_count")
    val totalCount: Int?,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean?,
    val items: List<User>
)
