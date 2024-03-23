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
import org.json.JSONArray
import org.json.JSONObject
import se.hkr.smarthouse.BuildConfig
import se.hkr.smarthouse.data.Device
import se.hkr.smarthouse.data.Sensor
import java.io.IOException

class WSHelper() {
    companion object {
        private val client = OkHttpClient()
        private var webSocket: WebSocket? = null
        val devices = mutableStateListOf<Device>()
        val sensors = mutableStateListOf<Sensor>()

        fun initConnection(URL: String) {
            val request = Request.Builder().url(URL).build()
            webSocket = client.newWebSocket(request, object : WebSocketListener() {

                override fun onMessage(webSocket: WebSocket, message: String) {
                    Log.d("WSHelper", "WebSocket Message: $message")
                    try {
                        // *** devices ***
                        val jsonDevicesArray = transformJsonObject(message, "devices")
                        var jsonDevices = JSONObject()

                        for (i in 0 until jsonDevicesArray.length()) {
                            val deviceObject = jsonDevicesArray.getJSONObject(i)
                            jsonDevices.put(deviceObject.getString("name"), deviceObject.getBoolean("status"))
                        }

                        // *** sensors ***
                        val jsonSensorsArray = transformJsonObject(message, "sensors")
                        var jsonSensors = JSONObject()

                        for (e in 0 until jsonSensorsArray.length()) {
                            val sensorObject = jsonSensorsArray.getJSONObject(e)
                            jsonSensors.put(sensorObject.getString("name"), sensorObject.getInt("value"))
                        }

                        // ***
                        runBlocking {
                            fetchDeviceStatusFromJSONObject(jsonDevices)
                            fetchSensorStatusFromJSONObject(jsonSensors)
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
                    device.status.value = json.optBoolean(device.name, device.status.value)
                }
            }
        }

        suspend fun fetchSensorStatusFromJSONObject(json: JSONObject) {
            withContext(Dispatchers.Main) {
                sensors.forEach { sensor ->
                    sensor.reading.value = json.optInt(sensor.name, sensor.reading.value)
                }
            }
        }

        fun transformJsonObject(message: String, unit: String): JSONArray {
            // json structure: {devices:[{}], sensors: [{}]}
            return JSONObject(message).getJSONArray(unit)
        }

        fun closeConnection() {
            webSocket?.close(1000, "Closing Connection")
        }

        public fun toggleDevice(currentStatus: MutableState<Boolean>, deviceEndpoint: String) {
            val newStatus = if (currentStatus.value) "0" else "1"
            sendMessage(newStatus, deviceEndpoint, "command")
        }

        public fun sendMessage(currentMessage: String, deviceEndpoint: String, reqField: String) { // reqField: "message" for LCD, "command" for everything else
            val json = "application/json; charset=utf-8".toMediaTypeOrNull()
            val jsonRequestBody = "{\"$reqField\":\"$currentMessage\"}".toRequestBody(json)

            val request = Request.Builder()
                .url("http://${BuildConfig.SERVER_IP}:5000/api/${deviceEndpoint}") // Adjust the URL/port as necessary INSIDE build.gradle
                .post(jsonRequestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("Server connection", "Failed to send \"$reqField\": $currentMessage", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        Log.e("Server connection", "Server error: ${response.code}")
                    } else {
                        Log.d("Server connection", "Successfully sent: $currentMessage")
                    }
                }
            })
        }
    }
}