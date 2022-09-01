package com.example.dataloggerextended.model

import java.util.*

data class DeviceData(
    var id: String? = null,
    var deviceId: String? = null,
    var hum: String? = null,
    var temp: String? = null,
    var time: Date? = null
        )
