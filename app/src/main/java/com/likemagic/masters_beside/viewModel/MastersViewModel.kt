package com.likemagic.masters_beside.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.likemagic.masters_beside.database.MastersDB
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.Profession
import com.likemagic.masters_beside.utils.NOT_MASTER

class MastersViewModel:ViewModel() {
    private val liveData: MutableLiveData<AppState> by lazy {MutableLiveData<AppState>()}
    private val dataBase = MastersDB()
    private val accountBase = Firebase.auth

    fun getLiveData(): LiveData<AppState> {
        return liveData
    }

    fun getMyData(){
        dataBase.getMyData(){
            liveData.postValue(AppState.MyData(it))
        }
    }

    fun getAllMasters(){
        liveData.postValue(AppState.Loading)
        dataBase.getAllMasters { list ->
            val tempList = arrayListOf<Master>()
            for(master in list){
                if(master.profession.name != NOT_MASTER)
                    tempList.add(master)
            }
            liveData.postValue(AppState.ListOfMasters(tempList))
        }
    }

    fun getMastersByProfession(profession: Profession){
        liveData.postValue(AppState.Loading)
        dataBase.getMastersByProfession(profession) { list ->
            if(list.isEmpty()){
                liveData.postValue(AppState.EmptyList(true))
            }else{
                liveData.postValue(AppState.ListOfMasters(list))
            }
        }
    }

    fun getMaster(master: Master, editState:Boolean){
        liveData.postValue(AppState.Loading)
        getMasterById(master.uid!!){result ->
            if (result.uid == accountBase.uid){
               if(editState){
                   liveData.postValue(AppState.MasterPage(result,true,true,true,result.isEmailChecked, result.isPhoneChecked))
               }else{
                   if(!result.isEmailChecked){
                       if(accountBase.currentUser?.isEmailVerified!!){
                           result.isEmailChecked = true
                           updateMaster(result, true)
                       }else{
                           liveData.postValue(AppState.MasterPage(result,true,true,false,result.isEmailChecked, result.isPhoneChecked))
                       }
                   }else{
                       liveData.postValue(AppState.MasterPage(result,true,true,false,result.isEmailChecked, result.isPhoneChecked))
                   }
               }
            }else{
                if(accountBase.currentUser?.uid != null){
                    getMasterById(accountBase.currentUser?.uid!!){value ->
                        if (value.isPhoneChecked){
                            liveData.postValue(AppState.MasterPage(result,isMy = false,true,editState = false, result.isEmailChecked, result.isPhoneChecked))
                        }else{
                            liveData.postValue(AppState.MasterPage(result,isMy = false,false,editState = false,result.isEmailChecked, result.isPhoneChecked))
                        }
                    }
                }else{
                    liveData.postValue(AppState.NotRegUser(true))
                }
            }
        }
    }

    fun getMasterById(uid: String, masterCallback: MasterCallback){
        dataBase.getMasterByUID(uid) { list ->
            if(list.isNotEmpty()){
                masterCallback.returnMaster(list[0])
            }else{
                liveData.postValue(AppState.EmptyList(true))
            }
        }
    }

    fun updateMaster(master:Master, isNeed:Boolean){
        dataBase.updateMaster(master) { liveData.postValue(AppState.UpdateMaster(it, isNeed)) }
    }

    fun deleteMaster(master: Master){
        dataBase.deleteMaster(master) {
            liveData.postValue(AppState.DeleteMaster)
        }
    }

    fun confirmPhone(master: Master){
        liveData.postValue(AppState.ConfirmPhone(master))
    }

    fun addToOnline(master: Master){
        dataBase.addToOnline(master)
    }

    fun getFavoriteMasters(master: Master){
        liveData.postValue(AppState.Loading)
        dataBase.getFavoriteMasters(master){list ->
            if(list.isEmpty()){
                liveData.postValue(AppState.EmptyList(true))
            }else{
                liveData.postValue(AppState.ListOfFavoriteMasters(list))
            }
        }
    }

    fun interface MasterCallback{
        fun returnMaster(master:Master)
    }

}