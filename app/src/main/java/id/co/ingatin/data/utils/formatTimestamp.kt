package id.co.ingatin.data.utils

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

fun toTimestamp(date: String, time: String): Timestamp{
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    val localDateTime = LocalDateTime.parse("$date $time", formatter)
    return Timestamp(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()))
}