package com.likemagic.masters_beside.notification

import android.app.Activity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.likemagic.masters_beside.utils.SERVER_KEY_FCM
import org.json.JSONException
import org.json.JSONObject

class NotificationSender {

    private var requestQueue: RequestQueue? = null
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    private val fcmServerKey = SERVER_KEY_FCM

    fun sendNotifications(userFcmToken: String?,title: String?,body: String?,
                          mActivity: Activity?) {
        requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body", body)
            notiObject.put("icon", "icon")
            mainObj.put("notification", notiObject)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, postUrl, mainObj,
                Response.Listener {},
                Response.ErrorListener {}) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=$fcmServerKey"
                    return header
                }
            }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}