package bsuir.ganebnaya_polina.lr8

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


class APIRequestHelper {
     suspend fun getCityData(url: URL): Array<City>? {
         val conn = withContext(Dispatchers.IO) {
             url.openConnection()
         } as HttpURLConnection

        conn.requestMethod = "GET"

        var resp: String = ""
        BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
            var line: String?
            while (br.readLine().also { line = it } != null) {
                resp = line.toString()
                println(line)
            }
        }

         val builder = GsonBuilder()
         val gson = builder.create()
         return gson.fromJson(resp, Array<City>::class.java)
    }

    suspend fun getJsonData(url: URL): JsonObject? {
        val conn = withContext(Dispatchers.IO) {
            url.openConnection()
        } as HttpURLConnection

        conn.requestMethod = "GET"

        var resp: String = ""
        BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
            var line: String?
            while (br.readLine().also { line = it } != null) {
                resp = line.toString()
                println(line)
            }
        }

        return JsonParser().parse(resp).getAsJsonObject()
    }

    fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
        when (val value = this[it])
        {
            is JSONArray ->
            {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else            -> value
        }
    }
}