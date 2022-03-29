package com.example.mysocialandroidapp.entity

import com.example.mysocialandroidapp.dto.Coordinates

data class CoordinatesEmbeddable(
    var lat: Double? = null,
    var long: Double? = null,
) {

    fun toDto(): Coordinates = Coordinates(lat = lat ?: 0.0, long = long ?: 0.0)

    companion object {
        fun fromDto(coordinates: Coordinates?): CoordinatesEmbeddable? =
            with(coordinates) {
                CoordinatesEmbeddable(lat = this?.lat, long = this?.long)
            }
    }
}