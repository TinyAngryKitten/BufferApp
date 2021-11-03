import api_client.BankClient
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import main.kotlin.settings
import model.*
import kotlin.time.ExperimentalTime
import kotlin.time.days

class Payments(
    val bankClient: BankClient,
    val tokenStorage: TokenStorage,
    private val accounts: Accounts = Accounts(),
    private val firestoreHelper : FirestoreHelper = FirestoreHelper()
    ) {
    val transactionActionCollection = "transaction-actions"

    @OptIn(ExperimentalTime::class)
    suspend fun checkForNewTransactions(
            since : Instant = Clock.System.now().minus((5 + settings.nrOfDaysToCheckForTransactions).days),
            until : Instant = Clock.System.now().minus(5.days)
    ) {
        bankClient.fetchTransactions(
            tokenStorage.getToken().access_token,
            accounts.creditCard,
            startDate = since,
            endDate = until,
        ).items.forEach {
            try {
                handleTransactionIfNew(it, since)
            } catch (e : Exception) {
                discordAlert("Error handling transaction", "Unknown error occured: ${e.message} while handling a transaction $it. \n${e.stackTraceToString()}")
            }
        }
    }

    suspend fun handleTransactionIfNew(transaction : Transaction, since : Instant) {
        when {
            hasBeenHandled(transaction, since) -> { }
            isNoopTransaction(transaction) -> {
                firestoreHelper.addData(
                    TransactionAction(transaction, "",0,"None"),
                    transactionActionCollection
                )

                log("Registered noop transaction: $transaction")
            }
            else -> {
                firestoreHelper.addData(
                    moveMoneytoCoverTransaction(transaction),
                    transactionActionCollection
                )

                log("Transaction was paid: $transaction")
            }
        }
    }

    suspend fun moveMoneytoCoverTransaction(transaction: Transaction) : TransactionAction {
        val paymentAccount = determinePaymentAccount(transaction)
        var transferCompleted = false
        try {
            transferCompleted = bankClient.transferMoney(
                Transfer(
                    accounts.creditCardPayments,
                    paymentAccount,
                    -transaction.amount,
                    createTransactionMessage(transaction.cardDetails)
                ),
                tokenStorage.getToken().access_token
            )
        }catch (e : Exception) {
            if(e.message?.contains("Insufficient money", true) ?: false) {
                log("insufficient funds on $paymentAccount, attempting to withdraw from paymentsBuffer")
                discordAlert("Insufficient funds", "Attempted to move ${transaction.amount} from ${accounts.findAccountName(paymentAccount)} because of the transaction: $transaction")
                transferCompleted = bankClient.transferMoney(
                    Transfer(
                        accounts.creditCardPayments,
                        accounts.paymentsBuffer,
                        -transaction.amount,
                        createTransactionMessage(transaction.cardDetails)
                    ),
                    tokenStorage.getToken().access_token
                )
                if(!transferCompleted) discordAlert("Insufficient funds on payments buffer", "Attempted to move ${transaction.amount} kr because of $transaction")
            } else {
                discordAlert("Error","Unknown error occured: ${e.message} when handling a transaction $transaction")
                log("Unknown error occured: ${e.message} when handling a transaction $transaction")
            }
        }

        if(transferCompleted) return TransactionAction(
            transaction,
            paymentAccount,
            transaction.amount.toInt(),
            "PayFromAccount",
            accounts.findAccountName(paymentAccount)
        )
        else throw Exception("Could not transfer payment to creditcard accound from $paymentAccount")
    }

    fun createTransactionMessage(cardDetails: CardDetails?) =
            "payment: ${cardDetails
                    ?.merchantName
                    ?.filter(::isValidBankStatementCharacter)
                    ?.take(20)}"

    private fun isValidBankStatementCharacter(c : Char) =
            c.toString()
            .matches("[a-zA-Z0-9-. ]")

    fun determinePaymentAccount(transaction: Transaction) : String  =
        parseMCC(transaction.cardDetails?.merchantCategoryCode)
            .group
            .withdrawalAccount

    suspend fun hasBeenHandled(transaction : Transaction, since : Instant) : Boolean {
        return firestoreHelper
            .find<TransactionAction>(
                transactionActionCollection,
                where = WhereClause("timestamp", ">", since.toLocalDateTime(norwegianTimeZone).timestamp)
            ).find {
                it.transaction == transaction
        } != null
    }

    private fun isNoopTransaction(transaction: Transaction) : Boolean =
        transaction.amount >= 0//only act when money has been withdrawn from the account
}