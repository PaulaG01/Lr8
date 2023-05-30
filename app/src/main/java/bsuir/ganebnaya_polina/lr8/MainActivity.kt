package bsuir.ganebnaya_polina.lr8

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    private val weatherApiKey: String = "83f9d347f0ddcbdb079512acea344c76"
    private val cityDataUrl: String = "https://api.openweathermap.org/geo/1.0/direct?limit=5&appid=${weatherApiKey}&q="
    private val weatherDataUrl: String = "https://api.openweathermap.org/data/2.5/weather?appid=${weatherApiKey}"
    private val apiRequestHelper: APIRequestHelper = APIRequestHelper()


    private val nbrbDollarUrl = "https://api.nbrb.by/exrates/rates/431"

    private var weatherDataFullUrl: String = ""

    private lateinit var getWeatherBtn: Button
    private lateinit var cityNameTextView: TextView
    private lateinit var lonView: TextView
    private lateinit var latView: TextView
    private lateinit var shadow: TextView
    private lateinit var feelsLike: TextView
    private lateinit var cityView: TextView
    private lateinit var rateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWeatherBtn = findViewById(R.id.button)
        cityNameTextView = findViewById(R.id.editTextText)
        lonView = findViewById(R.id.lonView)
        latView = findViewById(R.id.latView)
        shadow = findViewById(R.id.shadow)
        feelsLike = findViewById(R.id.feelsLike)
        cityView = findViewById(R.id.city)
        rateText = findViewById(R.id.rateText)

        getWeatherBtn.setOnClickListener {
            val weatherCityUrl = cityDataUrl + cityNameTextView.text
            val latLan = getLatLan(weatherCityUrl)
            runBlocking {
                val city: City? = latLan.await()?.first()
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.DOWN
                val lat = df.format(city?.lat)
                val lon = df.format(city?.lon)
                cityView.text = "Погода в ${city?.name}"
                lonView.text = "Lon: ${lon}"
                latView.text = "Lat: ${lat}"

                weatherDataFullUrl = weatherDataUrl + "&lat=${city?.lat}&lon=${city?.lon}"
            }

            val weatherCorut = getWeather(weatherDataFullUrl)
            var iconUrl: String = " https://openweathermap.org/img/wn/"
            runBlocking {
                val weath = weatherCorut.await()
                val main = weath?.get("main")
                val weatherData = (weath?.get("weather") as JsonArray)[0] as JsonObject
                iconUrl += weatherData.get("icon").toString() + "@2x.png"
                val temp = (main as JsonObject).get("temp")
                val tempF = (temp.toString().toFloat() - 273.15F).toInt()
                val feels = (main as JsonObject).get("feels_like")
                val feelsF = (feels.toString().toFloat() - 273.15F).toInt()
                shadow.text = "Градусов в тени: ${tempF} С"
                feelsLike.text = "Ощущается как: ${feelsF} С"
            }

            val dollarCorut = getDoll(nbrbDollarUrl)
            runBlocking {
                val dollar = dollarCorut.await()
                val rate = dollar?.get("Cur_OfficialRate")
                rateText.text = "$rate BYN"
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getDoll(url: String) = GlobalScope.async {
        val url: URL = URL(url)
        apiRequestHelper.getJsonData(url)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getLatLan(url: String) = GlobalScope.async {
        val url: URL = URL(url)
        apiRequestHelper.getCityData(url)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getWeather(url: String) = GlobalScope.async {
        val url: URL = URL(url)
        apiRequestHelper.getJsonData(url)
    }




//    private fun getLatLonByCityName(): Map<String, String> {
//        val latLon = mapOf<String, String>()
//
//    }
}