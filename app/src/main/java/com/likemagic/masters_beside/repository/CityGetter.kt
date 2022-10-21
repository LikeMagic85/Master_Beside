package com.likemagic.masters_beside.repository

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import kotlin.concurrent.thread

class CityGetter {
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

    fun getCityByName(search:String, context: Context, callback: (List<City>) -> Unit){
        thread {
            val tempList = ArrayList<City>()
            for (select: City in getAllCities(context)) {
                var counter = 0
                if(search.length <= select.name.length){
                    for (i in search.indices) {
                        if (search[i] == select.name[i]) {
                            counter++
                        }
                        if (counter == search.length) {
                            tempList.add(select)
                            tempList.sortWith(compareBy { it.name })
                        }else{
                            tempList.remove(select)
                        }
                    }
                }
            }
            callback.invoke(tempList)
        }
    }
}