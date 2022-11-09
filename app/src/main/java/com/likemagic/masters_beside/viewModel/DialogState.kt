package com.likemagic.masters_beside.viewModel

import com.likemagic.masters_beside.repository.Dialog


sealed class DialogState{
    data class ListOfDialogs(val list:List<Dialog>):DialogState()
    data class DialogPage( val dialog:Dialog):DialogState()
    object Loading:DialogState()
}
