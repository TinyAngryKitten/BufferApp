package client

import api_client.SBankenBankClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import sbankenClient
import kotlin.test.Test
import kotlin.time.ExperimentalTime

class BankClientTest {
    @OptIn(ExperimentalTime::class)
    @Test
    fun fetchTransactions() {
        CoroutineScope(Dispatchers.Main).launch {
            val result = sbankenClient.fetchTransactions(sbankenClient.getAccessToken()!!.access_token, "")
            val json = Json {
                prettyPrint = true
            }
            println(json.encodeToString(result))
        }
    }
}