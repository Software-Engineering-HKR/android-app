package se.hkr.interactivehouse.network

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.MutableState
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
    private val client = OkHttpClient()

    init {
        val request = Request.Builder().url("ws://192.168.50.60:8080").build()
        val webSocketListener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                // Parse the JSON data
                val json = JSONObject(text)
                Log.d("Data", text)
                val ledState = json.optString("led", "on")
                Log.d("LedState:", ledState)

                ledStatus.value = ledState.toBoolean()
                Log.d("LedStatus:", ledStatus.toString())
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Connection Failure", t)
            }
        }
        client.newWebSocket(request, webSocketListener)
        // Remember to close the WebSocket connection when it's no longer needed

            /*Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { toggleLed(ledStatus) { newStatus -> ledStatus = newStatus }
                    },
                    enabled = true // Update based on your logic or leave as always enabled
                ) {
                    Text(if (ledStatus) "Turn OFF LED" else "Turn ON LED")
                }
            }*/
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