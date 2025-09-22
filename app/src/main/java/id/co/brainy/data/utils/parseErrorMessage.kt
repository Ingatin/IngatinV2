package id.co.brainy.data.utils

import org.json.JSONObject
import retrofit2.HttpException

fun parseErrorMessage(e: Throwable): String {
    return if (e is HttpException) {
        try {
            val errorBody = e.response()?.errorBody()?.string()
            val json = JSONObject(errorBody ?: "")
            json.getString("message") // atau "error", tergantung struktur backend kamu
        } catch (ex: Exception) {
            "Terjadi kesalahan pada server"
        }
    } else {
        e.localizedMessage ?: "Terjadi kesalahan"
    }
}