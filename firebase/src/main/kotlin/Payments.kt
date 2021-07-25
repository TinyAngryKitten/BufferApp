import api_client.BankClient
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import model.*
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.days

class Payments(
    val bankClient: BankClient,
    val tokenStorage: TokenStorage,
    ) {
    val transactionActionCollection = "transaction-actions"

    @OptIn(ExperimentalTime::class)
    suspend fun checkForNewTransactions(
        since : Instant = Clock.System.now().minus(9.days),
        until : Instant = Clock.System.now().minus(5.days)
    ) {
        bankClient.fetchTransactions(
            tokenStorage.getToken().access_token,
            accounts.creditCard,
            startDate = since,
            endDate = until,
        ).items.forEach {
            handleTransactionIfNew(it, since)
        }
    }

    private suspend fun handleTransactionIfNew(transaction : Transaction, since : Instant) {
        when {
            hasBeenHandled(transaction, since) -> { }
            isNoopTransaction(transaction) -> firestoreHelper.addData(
                TransactionAction(transaction, "",0,"None"),
                transactionActionCollection
            )
            else -> firestoreHelper.addData(
                moveMoneytoCoverTransaction(transaction),
                transactionActionCollection
            )
        }
    }

    private suspend fun moveMoneytoCoverTransaction(transaction: Transaction) : TransactionAction {
        log("move money")
        val paymentAccount = determinePaymentAccount(transaction)
        var transferCompleted = false
        try {
            transferCompleted = bankClient.transferMoney(
                Transfer(
                    accounts.creditCardPayments,
                    paymentAccount,
                    -transaction.amount.roundToInt(),
                    "payment: ${transaction.cardDetails?.merchantName?.take(20)}"
                ),
                tokenStorage.getToken().access_token
            )
        }catch (e : Exception) {
            if(e.message?.contains("insufficient funds", true) ?: false) {
                log("insufficient funds on $paymentAccount, attempting to withdraw from paymentsBuffer")
                discordAlert("Insufficient funds", "Attempted to move ${transaction.amount} from ${accounts.findAccountName(paymentAccount)} because of $transaction")
                transferCompleted = bankClient.transferMoney(
                    Transfer(
                        accounts.creditCardPayments,
                        accounts.paymentsBuffer,
                        -transaction.amount.roundToInt(),
                        "payment: ${transaction.cardDetails?.merchantName?.take(20)}"
                    ),
                    tokenStorage.getToken().access_token
                )
                if(!transferCompleted) discordAlert("Insufficient funds on payments buffer", "Attempted to move ${transaction.amount} because of $transaction")
            } else {
                log("Unknown error occured when handling a transaction $transaction")
            }
        }

        if(transferCompleted) return TransactionAction(
            transaction,
            paymentAccount,
            transaction.amount.toInt(),
            "PayFromAccount"
        )
        else throw Exception("Could not transfer payment to creditcard accound from $paymentAccount")
    }

    private fun determinePaymentAccount(transaction: Transaction) : String  =
        when(parseMCC(transaction.cardDetails?.merchantCategoryCode)) {
            in MccGroup.HouseholdGroup.mccValues -> MccGroup.HouseholdGroup.withdrawalAccount
            else -> MccGroup.Unknown.withdrawalAccount
        }

    private suspend fun hasBeenHandled(transaction : Transaction, since : Instant) : Boolean {
        return firestoreHelper
            .find<TransactionAction>(
                transactionActionCollection,
                where = WhereClause("timestamp", ">", since.toLocalDateTime(norwegianTimeZone).timestamp)
            ).find {
                println("comparing \n$transaction \n${it.transaction}")
                it.transaction == transaction
        } != null
    }

    private fun isNoopTransaction(transaction: Transaction) : Boolean =
        transaction.amount >= 0//only act when money has been withdrawn from the account
}