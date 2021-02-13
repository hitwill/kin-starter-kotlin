package com.oneminja.kinstaker

import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.oneminja.kinstaker.ui.main.SectionsPagerAdapter
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.KinBinaryMemo
import org.kin.sdk.base.models.KinPayment
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*Initialize Kin*/
        val kin = Kin(
            applicationContext,
            false,
            165,
            "GC6D6TCMFYRTQEECH4FPAS2DFUECCF3KTCXOIYE4NEX2NIDAKQNJ32XS",
            "MyUser",
            "MyPass",
            ::balanceChanged,
            ::paymentHappened
        )

        /*Show the device's address to receive payments*/
        Log.d("Kin", "Device Address:" + kin.address()) //Device's address to receive payments

        /*Send out a payment*/
        var paymentItems = mutableListOf<Pair<String, Double>>()
        paymentItems.add(Pair("One Hamburger", 2.00)) //create a list to hold items being paid for
        paymentItems.add(Pair("Tip the waitress", 0.50))

        Log.d("Kin", "Sending Kin:")

        /*Send a single transaction containing the payment items*/
        kin.sendKin(
            paymentItems,
            "C2Tb36xUjDDiN4H3xE2T7PuBFb1gdCvP7znen1m8FStJ",
            KinBinaryMemo.TransferType.Spend,
            ::sentKin
        )

        /*The balance and payment listeners activate whenever the app initiates a transaction
        * However, you can periodically force them to check for transactions that happened
        * outside of the app, by calling checkTransactions
        **/
        Timer("RefreshListener", false).schedule(1200000) {
            //to test, start the app, then send Kin to the device address
            kin.checkTransactions()
        }

    }

    private fun balanceChanged(balance: KinBalance) {
        //Use this to update your user's current balance
        Log.d("Kin", "Current balance:" + balance.amount.toString()) //current balance
    }

    private fun paymentHappened(payments: List<KinPayment>) {
        //Use this to know when a payment has taken place
        Log.d("Kin", payments.toString())
    }

    private fun sentKin(payment: KinPayment?, error: Throwable?) {
        //Use this to know when the payment is sent
        if (error == null) {
            Log.d("Kin", "Sent Kin:$payment")
        } else {
            Log.e("Kin", "Error sending Kin", error)
        }
    }


}
