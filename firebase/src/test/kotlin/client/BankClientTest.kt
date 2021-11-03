package client

import Accounts
import api_client.BankClient
import api_client.SBankenBankClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.internal.JSJoda.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.norwegianTimeZone
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.days

class BankClientTest {
    val client = SBankenBankClient(
            userid = "",
            clientId = "",
            clientSecret = ""
    )

    @OptIn(ExperimentalTime::class)
    @Test
    fun fetchTransactions() {
        CoroutineScope(Dispatchers.Main).launch {
            val result = client.fetchTransactions(client.getAccessToken()!!.access_token, "")
            val json = Json {
                prettyPrint = true
            }
            println(json.encodeToString(result))
        }
    }
}