package com.example.dataloggerextended.model

import java.util.*

data class DeviceData(
    var id: String? = null,
    var deviceId: String? = null,
    var hum1: String? = null,
    var hum2: String? = null,
    var hum3: String? = null,
    var temp1: String? = null,
    var temp2: String? = null,
    var temp3: String? = null,
    var switch1: String? = null,
    var switch2: String? = null,
    var switch3: String? = null,
    var time: Date? = null
        )
