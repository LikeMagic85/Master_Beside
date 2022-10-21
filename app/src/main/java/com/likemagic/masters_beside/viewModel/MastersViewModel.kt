package com.likemagic.masters_beside.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.likemagic.masters_beside.database.DBManager
import com.likemagic.masters_beside.repository.City
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.Profession

class MastersViewModel:ViewModel() {
    private val liveData: MutableLiveData<AppState> by lazy {MutableLiveData<AppState>()}
    private val dataBase = DBManager()
    private val accountBase = Firebase.auth

    fun getLiveData(): LiveData<AppState> {
        return liveData
    }

    fun getAllMasters(){
        liveData.postValue(AppState.Loading)
        dataBase.getAllMasters(object : DBManager.ReadDataCallBack{
            override fun readData(list: List<Master>) {
                liveData.postValue(AppState.ListOfMasters(list))
            }

        })
    }

    fun getMastersByProfession(profession: Profession){
        liveData.postValue(AppState.Loading)
        dataBase.getMastersByProfession(profession, object :DBManager.ReadDataCallBack{
            override fun readData(list: List<Master>) {
                liveData.postValue(AppState.ListOfMasters(list))
            }
        })
    }

    fun getMastersByCity(city: City){
        liveData.postValue(AppState.Loading)
        dataBase.getMastersByCity(city, object :DBManager.ReadDataCallBack{
            override fun readData(list: List<Master>) {
                liveData.postValue(AppState.ListOfMasters(list))
            }
        })
    }

    fun getMaster(uid: String, editState:Boolean){
        liveData.postValue(AppState.Loading)
        dataBase.getMasterByUID(uid, object :DBManager.ReadDataCallBack{
            override fun readData(list: List<Master>) {
                if(list.isNotEmpty()){
                    if(uid == accountBase.uid){
                        if (editState){
                            liveData.postValue(AppState.MyProfile(list[0],
                                isMy = true,
                                editState = true
                            ))
                        }else{
                            liveData.postValue(AppState.MyProfile(list[0],true, editState = false))
                        }
                    }else{
                        if (accountBase.currentUser?.isEmailVerified!!){
                            liveData.postValue(AppState.MasterPage(list[0],
                                isMy = false,
                                isEmailVer = true
                            ))
                        }else{
                            liveData.postValue(AppState.MasterPage(list[0],
                                isMy = false,
                                isEmailVer = false
                            ))
                        }
                    }
                }else{
                    liveData.postValue(AppState.EmptyList)
                }
            }
        })
    }

    fun updateMaster(master:Master){
        liveData.postValue(AppState.Loading)
        dataBase.updateMaster(master, object : DBManager.UpdateCallBack{
            override fun updateData(master: Master) {
                liveData.postValue(AppState.UpdateMaster(master))
            }
        })

    }

    fun deleteMaster(master: Master){
        dataBase.deleteMaster(master) {
            liveData.postValue(AppState.DeleteMaster)
        }
    }

}