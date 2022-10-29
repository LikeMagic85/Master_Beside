package com.likemagic.masters_beside.utils

class OnlineTextHelper {

    fun repairText(number: Int):String{
        return if(number < 5){
            "Онлайн"
        }else if(number ==21|| number ==31 || number ==41 || number ==51){
            "Был $number минуту назад"
        }else if(number in 22..24 || number in 32..34 || number in 42..44 || number in 52..54){
            "Был $number минуты назад"
        }else if(number < 60){
            "Был $number минут назад"
        }else if (number in 60..119){
            "Был 1 час назад"
        }else if (number in 120 ..299){
            "Был ${number/60} часа назад"
        }else if(number in 300 .. 1199){
            "Был ${number/60} часов назад"
        }else if(number in 1200 .. 1259){
            "Был 21 час назад"
        }else if(number in 1260 .. 1439){
            "Был ${number/60} часа назад"
        }else if(number in 1440 .. 2879){
            "Был вчера"
        }else if(number in 2880 .. 7199){
            "Был ${number/1440} дня назад"
        }else if(number in 7200 .. 28799){
            "Был ${number/1440} дней назад"
        }else if(number in 28800 .. 30239){
            "Был 21 день назад"
        }else if(number in 30240 .. 35999){
            "Был ${number/1440} дня назад"
        }else if(number in 36000 .. 43199){
            "Был ${number/1440} дней назад"
        }else{
            "Был давно"
        }
    }
}