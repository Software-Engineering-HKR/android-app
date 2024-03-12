package se.hkr.smarthouse.network

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class WSListener: WebSocketListener()  {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send("Hello World!")
        Log.e("WS","error")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("Websocket", "Received : $text")

        try {
            val json = JSONObject(text)
            val sensorName = json.optString("sensor_name")
            val status = json.optBoolean("status")

            WSHelper.updateSensorStateByName(sensorName, status)

        } catch (e: Exception) {
            Log.e("WSListener", "Parsing message failed", e)
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        output("Closing : $code / $reason")
    }


    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message+" failed")
    }

    fun output(text: String?) {
        Log.d("Websocket", text!!)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }

}