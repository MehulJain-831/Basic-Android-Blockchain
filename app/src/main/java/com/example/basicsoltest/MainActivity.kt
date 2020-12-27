package com.example.basicsoltest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.infura.InfuraHttpService
import java.math.BigInteger
import java.util.concurrent.Future

class MainActivity : AppCompatActivity() {

    private lateinit var sendButton : Button
    private lateinit var dispTextView: TextView
    private lateinit var editTextNumber : EditText
    private lateinit var simpleContract: SimpleContract

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendButton = findViewById(R.id.send)
        dispTextView = findViewById(R.id.dispValue)
        editTextNumber = findViewById(R.id.editTextNumber)

        val contractAddress = "0x90f0F78760464C6731589D044f810376548F7AC7"
        val url = "https://kovan.infura.io/v3/1c7c239803794c23bd60fe236a89995a"
        val web3j = Web3jFactory.build(InfuraHttpService(url))
        val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L) //change this as required
        val gasPrice: BigInteger = BigInteger.valueOf(4300000) // this value also
        val credentials = Credentials.create("87f153f4484082c9bbdf8014390ccfe34e7e0a0b2f889033e04da46b4574a086")

        simpleContract  = SimpleContract.load(contractAddress, web3j, credentials, gasLimit, gasPrice)

        sendButton.setOnClickListener {
            val string : String = editTextNumber.text.toString()
            if(string != ""){
                GlobalScope.launch(Dispatchers.IO) {
                    sendAndRetrieveData(string.toBigInteger())
                }
            }

        }
    }

    private suspend fun sendAndRetrieveData(num : BigInteger) {
        val job = GlobalScope.launch(Dispatchers.Default) {
            // write to contract
            val transactionReceipt: Future<TransactionReceipt>? = simpleContract.set(num).sendAsync()

            // read from contract
            val getValue: Future<BigInteger>? = simpleContract.get().sendAsync()
            val convertToString: BigInteger? = getValue?.get()
            Log.i("##SUCCESS_READ_DATA##", "contract value returned: $convertToString")

            // returned value displayed to text view in Main Thread
            withContext(Dispatchers.Main){
                dispTextView.text = convertToString.toString()
            }
        }
        job.join()
    }
}
