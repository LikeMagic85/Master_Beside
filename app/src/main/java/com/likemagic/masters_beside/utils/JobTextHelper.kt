package com.likemagic.masters_beside.utils

class JobTextHelper {

    fun repairText(number: Int):String{
        return if(number < 5){
            "Только что"
        }else if(number ==21|| number ==31 || number ==41 || number ==51){
            "$number минуту назад"
        }else if(number in 22..24 || number in 32..34 || number in 42..44 || number in 52..54){
            "$number минуты назад"
        }else if(number < 60){
            "$number минут назад"
        }else if (number in 60..119){
            "1 час назад"
        }else if (number in 120 ..299){
            "${number/60} часа назад"
        }else if(number in 300 .. 1199){
            "${number/60} часов назад"
        }else if(number in 1200 .. 1259){
            "21 час назад"
        }else if(number in 1260 .. 1439){
            "${number/60} часа назад"
        }else if(number in 1440 .. 2879){
            "Вчера"
        }else if(number in 2880 .. 7199){
            "${number/1440} дня назад"
        }else if(number in 7200 .. 28799){
            "${number/1440} дней назад"
        }else if(number in 28800 .. 30239){
            "21 день назад"
        }else if(number in 30240 .. 35999){
            "${number/1440} дня назад"
        }else if(number in 36000 .. 43199){
            "${number/1440} дней назад"
        }else{
            "Больше месяца назад"
        }
    }
}