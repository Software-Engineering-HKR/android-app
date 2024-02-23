package se.hkr.smarthouse.network

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
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
class WSHelper(ledStatus: MutableState<Boolean>) {
    companion object {
        private val client = OkHttpClient()
        private var webSocket: WebSocket? = null
        val devices = mutableStateListOf<Device>()

        fun initConnection(URL: String) {
            val request = Request.Builder().url(URL).build()
            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("WSHelper", "WebSocket Message: $text")
                    try {
                        val json = JSONObject(text)
                        devices.forEach { device ->
                            when (device.name) {
                                "led" -> device.status.value = json.optBoolean("led", device.status.value)
                                "yellow-led" -> device.status.value = json.optBoolean("yellow-led", device.status.value)
                            }
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

        fun closeConnection() {
            webSocket?.close(1000, "Closing Connection")
        }


        public fun toggleDevice(currentStatus: MutableState<Boolean>, deviceEndpoint: String) {
            val newStatus = if (currentStatus.value) "0" else "1"
            val json = "application/json; charset=utf-8".toMediaTypeOrNull()
            val jsonRequestBody = "{\"command\":\"$newStatus\"}".toRequestBody(json)

            //updateStatus(!(currentStatus.value))

            val request = Request.Builder()
                .url("http://192.168.50.60:5000/api/${deviceEndpoint}") // Adjust the URL/port as necessary
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