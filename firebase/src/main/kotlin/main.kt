import api_client.SBankenBankClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

external fun require(module:String) : dynamic
external var exports: dynamic

private lateinit var userid : String
private lateinit var clientId : String
private lateinit var clientSecret : String

fun main(args: Array<String>) {
    val functions = require("firebase-functions")
    userid = functions.config().user.id as String
    clientId = functions.config().client.id as String
    clientSecret = functions.config().client.secret as String

    val client = SBankenBankClient(userid,clientId,clientSecret)
    exports.myTestFun = functions.https.onRequest { request , response ->
        CoroutineScope(Dispatchers.Main).launch {
            val token = client.getAccessToken(clientId, clientSecret)?.access_token
            val accounts = client.listAccounts(token!!)
            response.send(accounts.items.size) as Unit
        }
    }
}