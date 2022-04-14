package com.example.mysocialandroidapp.entity

import com.example.mysocialandroidapp.dto.Coordinates

data class CoordinatesEmbeddable(
    var latitude: Double? = null,
    var longitude: Double? = null,
) {

    fun toDto(): Coordinates = Coordinates(lat = latitude ?: 0.0, long = longitude ?: 0.0)

    companion object {
        fun fromDto(coordinates: Coordinates?): CoordinatesEmbeddable? =
            with(coordinates) {
                CoordinatesEmbeddable(latitude = this?.lat, longitude = this?.long)
            }
    }
}