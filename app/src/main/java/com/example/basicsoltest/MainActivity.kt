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
    private lateinit var editText : EditText
    private lateinit var simpleContract: BasicString

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendButton = findViewById(R.id.send)
        dispTextView = findViewById(R.id.dispValue)
        editText = findViewById(R.id.editText)

        val contractAddress = "0x19Ec6403f536CC101c2C7C89d4Aef25147D085f3"
        val url = "https://kovan.infura.io/v3/c419d20f1b064478bcfe5921e4b72142"
        val web3j = Web3jFactory.build(InfuraHttpService(url))
        val gasLimit: BigInteger = BigInteger.valueOf(430) //change this as required
        val gasPrice: BigInteger = BigInteger.valueOf(210) // this value also
        val credentials = Credentials.create("87f153f4484082c9bbdf8014390ccfe34e7e0a0b2f889033e04da46b4574a086")

        simpleContract  = BasicString.load(contractAddress, web3j, credentials, gasLimit, gasPrice)

        sendButton.setOnClickListener {
            val string : String = editText.text.toString()
            if(string != ""){
                GlobalScope.launch(Dispatchers.IO) {
                    sendAndRetrieveData(string)
                }
            }

        }
    }

    private suspend fun sendAndRetrieveData(text : String) {
        val job = GlobalScope.launch(Dispatchers.Default) {
            // write to contract
            val transactionReceipt: Future<TransactionReceipt>? = simpleContract.set(text).sendAsync()
//            val result = "Successful transaction. Gas used: ${transactionReceipt?.get()?.blockNumber}  ${transactionReceipt?.get()?.gasUsed}"
//            Log.i("##SUCCESS_WRITE_DATA##", result)

            // read from contract
            val getValue: Future<String>? = simpleContract.get().sendAsync()
            val convertToString: String? = getValue?.get()
            Log.i("##SUCCESS_READ_DATA##", "contract value returned: $convertToString")

            // returned value displayed to text view in Main Thread
            withContext(Dispatchers.Main){
                dispTextView.text = convertToString.toString()
            }
        }
        job.join()
    }
}
