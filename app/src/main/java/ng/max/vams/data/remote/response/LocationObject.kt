package ng.max.vams.data.remote.response

import com.google.gson.annotations.SerializedName

data class LocationObject(@SerializedName("objects")
                          val locations: List<Location>)
