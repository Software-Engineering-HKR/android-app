package se.hkr.interactivehouse.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket

class WSHelper() {
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
}