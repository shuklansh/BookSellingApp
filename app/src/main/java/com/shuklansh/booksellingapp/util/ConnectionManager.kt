package com.shuklansh.booksellingapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {
    //fun funcName(arguement): returnType{..}
    fun checkConnectivity(context: Context): Boolean {
        //gives info about which networks are currently active (mob data or wifi or bt or hotspot) and return if they are present (True or False)
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //activenetworkinfo tells if the hardware is working fine for these network
        val activeNetwork: NetworkInfo? =connectivityManager.activeNetworkInfo

        if(activeNetwork?.isConnected==true){ //isconnected is method of class info. this method tells if network is connected to internet or not
            //values of isConnected : (true => network has internet ; false => network has no internet ; Null => network is broken/inactive)
            return activeNetwork.isConnected
        }
        else{
            return false
        }

    }
}