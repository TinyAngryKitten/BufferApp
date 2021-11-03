package payments

import AccountsStub
import BankClientStub
import FirestoreHelper
import Payments
import TokenStorage
import WhereClause
import functions
import kotlinx.datetime.LocalDateTime
import model.CardDetails
import model.MccGroup
import testdata.accountsWithLotsOfMoney
import kotlin.js.json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull

class IllegalCharacterInMessage {
    val firestoreStub by lazy {
        FirestoreHelper(
                useStubs = true,
                addDataStub = {json,collection -> },
                findStub = {collection : String , where : WhereClause -> ""},
                findAllStub = {collection : String -> "" },
                removeStub = {collection, id -> }
        )
    }

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

    val capriCardDetails = CardDetails(cardNumber="*2322", currencyAmount=294.0, currencyRate=1.0, merchantCategoryCode="5812", merchantCategoryDescription="Restaurant, spisested", merchantCity="Oslo", merchantName="Vipps*Capri Resturant Os", originalCurrencyCode="NOK", purchaseDate= LocalDateTime.parse("2021-10-24T00:00"), transactionId="441292325513712")

    val legalCharacters = """
        \"1234567890aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZæÆøØåÅäÄëËïÏöÖüÜÿâÂêÊîÎôÔûÛãÃñÑõÕàÀèÈìÌòÒùÙáÁéÉíÍóÓýÝ,;.:!-/()? ,
    """.trimIndent()

    @Test
    fun capriTransaction() {
        val bankClient = BankClientStub(listAccountsStub = { accountsWithLotsOfMoney })
        val payments = Payments(bankClient, TokenStorage(bankClient), AccountsStub(), firestoreStub)
        val result = payments.createTransactionMessage(capriCardDetails)

        assertNull(result.find { it !in legalCharacters.toList() })
    }
}