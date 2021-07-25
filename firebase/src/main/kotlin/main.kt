import api_client.SBankenBankClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Debt

external fun require(module:String) : dynamic
external var exports: dynamic

lateinit var userid : String
lateinit var clientId : String
lateinit var clientSecret : String

var firestore : dynamic = null
var functions : dynamic = null
var firebase : dynamic = null

fun main(args: Array<String>) {
    functions = require("firebase-functions")
    firebase = require("firebase")
    val admin = require("firebase-admin")
    admin.initializeApp()
    firestore = admin.firestore()

    userid = functions.config().user.id as String
    clientId = functions.config().client.id as String
    clientSecret = functions.config().client.secret as String

    val client = SBankenBankClient(userid,clientId,clientSecret)
    val tokenStorage = TokenStorage(client)
    val buffer = BufferAccount(client, tokenStorage, firestore,functions)
    val payments = Payments(client, tokenStorage)

    exports.checkTransactions = functions.pubsub.schedule("every 24 hours").onRun { context ->
        CoroutineScope(Dispatchers.Default).launch {
            payments.checkForNewTransactions()
        }
    }

    exports.payDebtForCurrentMonth = functions.pubsub.schedule("every 24 hours").onRun { context ->
        CoroutineScope(Dispatchers.Default).launch {
            buffer.payDebtForCurrentMonth()
        }
    }

    exports.listDebt = functions.https.onRequest { request, response ->
        CoroutineScope(Dispatchers.Default).launch {
            log("listing debt")
            response.send(Json.encodeToString(buffer.getDebt())) as Unit
        }
        Unit
    }

    exports.addDebt = functions.https.onRequest { request, response ->
        CoroutineScope(Dispatchers.Default).launch {
            buffer.addDebt(
                Debt(
                    request.body.amount.toDouble(),
                    request.body.nrOfPayments,
                    request.body.name
                )
            )
        }
        Unit
    }

    exports.takeUpLoan = functions.https.onRequest { request, response ->
        CoroutineScope(Dispatchers.Default).launch {
            buffer.withdrawWithDownpayment(
                request.body.amount.toDouble(),
                request.body.toAccount,
                Debt(
                    request.body.amount.toDouble(),
                    request.body.nrOfPayments,
                    request.body.name
                )
            )

            discordAlert("Debt", "adding debt at ${request.body.amount} over ${request.body.nrOfPayments} months")
            response.send("")
        }
        Unit
    }

    /*exports.listAccounts = functions.https.onRequest { request , response ->
        CoroutineScope(Dispatchers.Default).launch {
            val token = client.getAccessToken()?.access_token
            val accounts = client.listAccounts(token!!)
            response.send(Json.encodeToString(accounts.items)) as Unit
        }
        Unit
    }*/
}