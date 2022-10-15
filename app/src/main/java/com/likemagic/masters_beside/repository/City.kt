package com.likemagic.masters_beside.repository

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

data class City(var name: String="Минск", var lat: Double = 53.902284, var lon: Double=27.561831) {

    fun getAllCities(context: Context):List<City>{
        val tempArray = ArrayList<City>()

        try {
            val inputStream: InputStream = context.assets.open("by-cities.json")
            val size:Int = inputStream.available()
            val byteArray = ByteArray(size)
            inputStream.read(byteArray)
            val jsonFile = JSONArray(String(byteArray))
            for (i in 0 until jsonFile.length()){
                val jsonObject = JSONObject(jsonFile.getString(i))
                tempArray.add(City(jsonObject.getString("name"),jsonObject.getString("lat").toDouble(),jsonObject.getString("lng").toDouble()))
            }

        }catch (e: IOException){

        }
        return tempArray
    }
}
