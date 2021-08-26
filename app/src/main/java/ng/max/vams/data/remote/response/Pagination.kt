package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("next_page")
    val nextPage: Int,
    @SerializedName("page_count")
    val pageCount: Int,
    @SerializedName("previous_page")
    val previousPage: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("total")
    val total: Int
)