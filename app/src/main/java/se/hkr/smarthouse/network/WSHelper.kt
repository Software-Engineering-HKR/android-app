package se.hkr.smarthouse.network

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import se.hkr.smarthouse.BuildConfig
import se.hkr.smarthouse.data.Device
import java.io.IOException

/*class WSHelper() {
    private lateinit var listener: WSListener
    private lateinit var ws: WebSocket

    fun InitConnection(URL : String){
        val client: OkHttpClient =  OkHttpClient()

        val request: Request = Request
            .Builder()
            .url(URL)
            .build()
        listener = WSListener()
        ws = client.newWebSocket(request, listener)
    }
}*/

// TODO: refactor to work for all devices, waiting on backend.
class WSHelper() {
    companion object {
        private val client = OkHttpClient()
        private var webSocket: WebSocket? = null
        val devices = mutableStateListOf<Device>()
        val sensors = mutableStateListOf<Sensor>().apply {
            add(Sensor(name = "motion_sensor", endpoint = "motion_sensor_endpoint", displayName = "Motion Sensor", status = mutableStateOf(false)))
           // add(Sensor(name = "moisture_sensor", endpoint = "moisture_sensor_endpoint", displayName = "Moisture Sensor", status = mutableStateOf(false)))
            add(Sensor(name = "photocell_sensor", endpoint = "photocell_sensor_endpoint", displayName = "Photocell Sensor", status = mutableStateOf(false)))
            add(Sensor(name = "gas_sensor", endpoint = "gas_sensor_endpoint", displayName = "Gas Sensor", status = mutableStateOf(false)))
            add(Sensor(name = "steam_sensor", endpoint = "steam_sensor_endpoint", displayName = "Steam Sensor", status = mutableStateOf(false)))
            add(Sensor(name = "soil_humidity_sensor", endpoint = "soil_humidity_sensor_endpoint", displayName = "Soil humidity Sensor", status = mutableStateOf(false)))
        }

        fun initConnection(URL: String) {
            val request = Request.Builder().url(URL).build()
            webSocket = client.newWebSocket(request, object : WebSocketListener() {

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("WSHelper", "WebSocket Message: $text")
                    try {
                        // json structure: {devices:[{}], sensors: [{}]}
                        val json = JSONObject(text)
                        val jsonDevicesArray = json.getJSONArray("devices")
                        var jsonDevices = JSONObject()

                        for (i in 0 until jsonDevicesArray.length()) {
                            val deviceObject = jsonDevicesArray.getJSONObject(i)
                            jsonDevices.put(deviceObject.getString("name"), deviceObject.getBoolean("status"))
                        }

                        runBlocking {
                            fetchDeviceStatusFromJSONObject(jsonDevices)
                        }

                    } catch (e: Exception) {
                        Log.e("WSHelper", "Parsing JSON failed", e)
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("WSHelper", "WebSocket Failure: $t", t)
                }
            })
        }

        suspend fun fetchDeviceStatusFromJSONObject(json: JSONObject) {
            withContext(Dispatchers.Main) {
                devices.forEach { device ->
                    var newStatus = json.optBoolean(device.name, device.status.value)
                    device.status.value = newStatus
                }
            }
        }

        fun closeConnection() {
            webSocket?.close(1000, "Closing Connection")
        }


        public fun toggleDevice(currentStatus: MutableState<Boolean>, deviceEndpoint: String) {
            val newStatus = if (currentStatus.value) "0" else "1"
            val json = "application/json; charset=utf-8".toMediaTypeOrNull()
            val jsonRequestBody = "{\"command\":\"$newStatus\"}".toRequestBody(json)

            //updateStatus(!(currentStatus.value))

            val request = Request.Builder()
                .url("http://${BuildConfig.SERVER_IP}:5000/api/${deviceEndpoint}") // Adjust the URL/port as necessary INSIDE build.gradle
                .post(jsonRequestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("toggleLed", "Failed to send command: $newStatus", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        Log.e("toggleLed", "Server error: ${response.code}")
                    } else {
                        Log.d("toggleLed", "Successfully toggled LED to $newStatus")
                    }
                }
            })
        }
    }

}