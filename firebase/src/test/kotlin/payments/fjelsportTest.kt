package payments

import AccountsStub
import BankClientStub
import FirestoreHelper
import Payments
import TokenStorage
import WhereClause
import assert.assert
import functions
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.JsJSON
import model.MccGroup
import model.Transaction
import testdata.accountsWithLotsOfMoney
import testdata.fjellsport
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.json
import kotlin.test.BeforeTest
import kotlin.test.Test


class FjellsportTest {

    val firestoreStub = FirestoreHelper(
            useStubs = true,
            addDataStub = {json,collection ->addDataCallback(json, collection) },
            findStub = {collection : String , where : WhereClause -> findProp },
            findAllStub = {collection : String -> "" },
            removeStub = {collection, id -> }
    )

    var _addDataCallback : (JsJSON, String) -> Unit = {_,_ -> }
    val addDataCallback : (JsJSON, String) -> Unit
        get() = _addDataCallback

    var findAllString = "[]"
    val findAllProp : String
        get() = findAllString


    var findString = "[]"
    val findProp : String
        get() = findString

    @BeforeTest
    fun setup() {
        functions = json(
                "logger" to json("log" to {s : String -> }),
                "config" to {
                    json(
                            "settings" to json("nr_of_days_to_check_for_transactions" to "5"),
                            "discord" to json("webhook_url" to "")
                    )
                }
        )
        MccGroup.accounts = AccountsStub()
    }

    @Test
    fun test() = CoroutineScope(Dispatchers.Main).launch {
        val bankClient = BankClientStub(listAccountsStub = { accountsWithLotsOfMoney })
        val payments = Payments(bankClient, TokenStorage(bankClient), AccountsStub(), firestoreStub)
        val transaction = Json.decodeFromString<Transaction>(fjellsport)
        var res : JsJSON? = null
        val job = GlobalScope.launch {
            res = suspendCoroutine<JsJSON> {
                _addDataCallback = {json,_ -> it.also { println(json.toString()) }.resume(json)}
            }
        }
        delay(100)

        println("gonna handle the thing")
        payments.handleTransactionIfNew(transaction, Clock.System.now())
        println("handled the thing")
        assert(job.isCompleted, "is not completed")
        job.join()
        val v = res
    }
}