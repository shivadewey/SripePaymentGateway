package com.shiva.sripepaymentgatewayyoutube

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shiva.sripepaymentgatewayyoutube.API.ApiUtilities
import com.shiva.sripepaymentgatewayyoutube.Utils.PUBLISHABLE_KEY
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var paymentSheet: PaymentSheet

    lateinit var customerId: String
    lateinit var ephemeralKey: String
    lateinit var clientSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PaymentConfiguration.init(this, PUBLISHABLE_KEY)


        getCustomerId()

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {

            paymentFlow()
        }


        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
    }

    private fun paymentFlow() {


        paymentSheet.presentWithPaymentIntent(

            clientSecret,
            PaymentSheet.Configuration(
                "Papaya Coders",

                PaymentSheet.CustomerConfiguration(

                    customerId, ephemeralKey
                )
            )
        )


    }

    private var apiInterface = ApiUtilities.getApiInterface()

    private fun getCustomerId() {

        lifecycleScope.launch(Dispatchers.IO) {

            val res = apiInterface.getCustomer()
            withContext(Dispatchers.Main) {

                if (res.isSuccessful && res.body() != null) {
                    customerId = res.body()!!.id
                    getEphemeralKey(customerId)
                }

            }
        }

    }

    private fun getEphemeralKey(customerId: String) {

        lifecycleScope.launch(Dispatchers.IO) {

            val res = apiInterface.getEphemeralKey(customerId)
            withContext(Dispatchers.Main) {

                if (res.isSuccessful && res.body() != null) {
                    ephemeralKey = res.body()!!.id
                    getPaymentIntent(customerId, ephemeralKey)
                }

            }
        }

    }

    private fun getPaymentIntent(customerId: String, ephemeralKey: String) {


        lifecycleScope.launch(Dispatchers.IO) {

            val res = apiInterface.getPaymentIntent(customerId)
            withContext(Dispatchers.Main) {

                if (res.isSuccessful && res.body() != null) {

                    clientSecret = res.body()!!.client_secret

                    Toast.makeText(this@MainActivity, "Proceed for payment", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
    }


    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        // implemented in the next steps
        
        if (paymentSheetResult is PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment DONE", Toast.LENGTH_SHORT).show()
        }
    }
}