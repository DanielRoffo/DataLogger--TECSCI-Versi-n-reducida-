package com.example.dataloggerextended.ui.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dataloggerextended.adapters.userDevices.UserDevicesAdapter
import com.example.dataloggerextended.databinding.FragmentDevicesBinding
import com.example.dataloggerextended.model.UserDevice
import com.example.dataloggerextended.ui.view.AddDeviceActivity
import com.example.dataloggerextended.ui.viewmodel.MainViewModel
import com.example.dataloggerextended.utils.ScreenState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DevicesFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentDevicesBinding
    private val dbDevices = Firebase.firestore.collection("devices")
    private val dbDevicesUsers = Firebase.firestore.collection("users")
    private val myAdapter by lazy { UserDevicesAdapter(emptyList<UserDevice>())}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDevicesBinding.inflate(layoutInflater)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        viewModel.getUserDevices(firebaseAuth.currentUser!!.email!!)

        initRecyclerView()

        //Observo del MainViewModel si hay cambios en los datos de movieLiveData
        viewModel.device.observe(viewLifecycleOwner,{ state ->
            processDevicesResponse(state)
        })

        addNewDevice()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun processDevicesResponse(state: ScreenState<List<UserDevice?>>) {
        when (state) {
            is ScreenState.Loading -> {

            }
            is ScreenState.Success -> {

                myAdapter.setData(state.data as List<UserDevice>)

            }
            is ScreenState.Error ->{

            }
        }
    }

    //Se inicializa el RecyclerView
    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context, 3)
        val recyclerView = binding.devicesIdRv
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = myAdapter

    }

    private fun addNewDevice(){
        binding.addNewDevice.setOnClickListener {

            val devId = binding.etDeviceId.text.toString()
            val devCode = binding.etDeviceCode.text.toString()

            if (devId.isNotEmpty() && devCode.isNotEmpty()) {

                dbDevicesUsers.document(firebaseAuth.currentUser!!.email!!).collection("linked_devices").document(devId)
                    .get().addOnSuccessListener {
                        if (it.data.isNullOrEmpty()){
                            dbDevices.document(devId).get()
                                .addOnSuccessListener { document ->
                                    if (document.data == null){
                                        Toast.makeText(context, "Couldn't find device = $devId", Toast.LENGTH_SHORT).show()
                                    }else{
                                        if (document.data!!.containsValue(devCode)){
                                            Toast.makeText(context, "Set up your new device!", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(context, AddDeviceActivity::class.java)
                                            val extras = Bundle()
                                            extras.putString("device", devId)
                                            extras.putString("model", document.data!!["model"].toString())
                                            intent.putExtra("extras", extras)
                                            startActivity(intent)
                                        }else if (!document.data!!.containsValue(devCode)){
                                            Toast.makeText(context, "Wrong code. Try Again!", Toast.LENGTH_SHORT).show()
                                        }else{
                                            Toast.makeText(context, "Wrong Id. Try Again!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Ups, something went wrong. Try again!", Toast.LENGTH_SHORT).show()
                                }

                        }else{
                            Toast.makeText(context, "You already have this device!", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { e -> Log.w(TAG, "Error Reading document", e) }
            }else {
                Toast.makeText(context, "Ups, something is missing. Complete all the fields and try again!", Toast.LENGTH_SHORT).show()
            }

        }

    }


}