package com.likemagic.masters_beside.view.navigation.dialogs

import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import coil.dispose
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentDialogBinding
import com.likemagic.masters_beside.databinding.FragmentListOfDialogsBinding
import com.likemagic.masters_beside.notification.NotificationSender
import com.likemagic.masters_beside.repository.Dialog
import com.likemagic.masters_beside.repository.DialogMessage
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.DIALOG_ID
import com.likemagic.masters_beside.utils.MASTER_ID
import com.likemagic.masters_beside.utils.hideKeyboard
import com.likemagic.masters_beside.utils.setToolbarVisibility
import com.likemagic.masters_beside.view.navigation.ProfileMasterFragment
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.DialogState
import com.likemagic.masters_beside.viewModel.DialogsViewModel
import com.likemagic.masters_beside.viewModel.MastersViewModel


class DialogFragment : Fragment() {

    private var _binding: FragmentDialogBinding? = null
    private val binding: FragmentDialogBinding
        get() = _binding!!
    private val dialogsViewModel: DialogsViewModel by viewModels()
    private val mastersViewModel: MastersViewModel by viewModels()
    private lateinit var sender:Master
    private lateinit var recipient:Master
    private lateinit var messageAdapter: ListOfMessageAdapter


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        val args = arguments
        val dialog = args?.getParcelable<Dialog>(DIALOG_ID)
        mastersViewModel.getMyData()
        mastersViewModel.getLiveData().observe(viewLifecycleOwner){
           setUpMembers(it, dialog!!)
        }
        dialogsViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
    }

    private fun initRecycler(){
        messageAdapter = ListOfMessageAdapter(myData = sender, context = requireContext())
        binding.messageList.adapter = messageAdapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.messageList.setOnScrollChangeListener { _, _, _, _, _ ->
                binding.titleOfListContainer.isSelected = binding.messageList.canScrollVertically(-1)
            }
        }
    }

    private fun setUpMembers(appState: AppState, dialog: Dialog) {
        when(appState){
            is AppState.MyData ->{
                sender = appState.master
                if(sender.uid == dialog.senderUid){
                    mastersViewModel.getMasterById(dialog.recipientUid){result ->
                        recipient = result
                        binding.titleOfList.text = recipient.name
                        dialogsViewModel.findDialog(dialog.members, sender, recipient)
                    }
                    if(dialog.recipientUri.isNotEmpty()){
                        binding.recipientPhoto.load(dialog.recipientUri)
                    }else {
                        binding.recipientPhoto.dispose()
                        binding.recipientPhoto.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_account))
                    }
                }else{
                    mastersViewModel.getMasterById(dialog.senderUid){result ->
                        recipient = result
                        dialogsViewModel.findDialog(dialog.members, sender, recipient)
                        binding.titleOfList.text = recipient.name
                    }
                    if(dialog.senderUri.isNotEmpty()){
                        binding.recipientPhoto.load(dialog.senderUri)
                    }else {
                        binding.recipientPhoto.dispose()
                        binding.recipientPhoto.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_account))
                    }
                }
                initRecycler()
            }
            else -> {}
        }
    }


    private fun renderData(dialogState: DialogState) {
        when(dialogState){
            is DialogState.DialogPage ->{
                sendMessage(dialogState.dialog)
                messageAdapter.setList(dialogState.dialog.listOfMessage)
            }
            else -> {}
        }
    }

    private fun sendMessage(dialog: Dialog){
        binding.sendField.setEndIconOnClickListener {
            val text = binding.sendFieldInput.text.toString()
            val message = DialogMessage(sender.uid!!, text, System.currentTimeMillis())
            if(text.isNotEmpty()){
                dialogsViewModel.addMessageToDialog(message, dialog)
                NotificationSender().sendNotifications(recipient.fcmToken, sender.name, text, requireActivity())
                binding.sendFieldInput.setText("")
                binding.root.hideKeyboard()
                messageAdapter.notifyItemInserted(dialog.listOfMessage.size)
                binding.messageList.smoothScrollToPosition(dialog.listOfMessage.size)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(dialog: Dialog):DialogFragment{
            val args = Bundle()
            args.putParcelable(DIALOG_ID, dialog)
            val fragment = DialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}