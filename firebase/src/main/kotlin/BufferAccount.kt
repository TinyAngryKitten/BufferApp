import api_client.BankClient
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.json

class BufferAccount(
    val bankClient: BankClient,
    val tokenStorage : TokenStorage,
    val firestore : dynamic,
    val functions : dynamic,
    ) {
    private val debtCollectionName = "debt"
    private val debtPaymentCollectionName = "debtPayments"

    suspend fun payDebtForCurrentMonth() {
        getDebt().forEach { debt ->
            val payments = getDebtPayments(debt)
            debt
                .takeUnless { isDebtPaidForCurrentMonth(debt, payments) }
                ?.let { payDebt(debt, payments) }
        }
    }

    suspend fun payDebt(debt: Debt, payments: List<DebtPayment>) {
        val paymentAmount = debt.amount / debt.nrOfPayments
        val transfer = Transfer(accounts.buffer, accounts.paymentsBuffer, paymentAmount.toInt(), "debt: ${debt.name}")
        if(bankClient.transferMoney(transfer,tokenStorage.getToken().access_token)) {
            addDebtPayment(
                DebtPayment(
                    debt.amount / debt.nrOfPayments,
                    payments.size,
                    debt,
                    Clock.System.now().toEpochMilliseconds().toFloat()
                )
            )
        } else {
            discordAlert("Couldnt not pay debt","Could not make payment on debt: ${debt.name}")
            println("Could not make payment on debt: ${debt.name}")
        }
    }

    suspend fun isDebtPaidForCurrentMonth(debt: Debt, payments : List<DebtPayment>) : Boolean {
        if(payments.sumOf { it.amount } >= debt.amount) {
            removeDebt(debt)
            return true
        }

        val now = Clock.System.now().toLocalDateTime(norwegianTimeZone)
        return payments.find {
            val timeCreated = LocalDateTime.fromTimestamp(it.timestamp)
            timeCreated.monthNumber == now.monthNumber &&
            timeCreated.year == now.year
        } != null
    }

    suspend fun withdrawWithDownpayment(
        amount: Double,
        accountId : String = accounts.paymentsBuffer,
        debt : Debt
    ) {
        addDebt(debt)
        withdraw(amount,accountId)
    }

    suspend fun getDebtPayments(debt: Debt) : List<DebtPayment> =
            firestoreHelper.find(
                debtPaymentCollectionName,
                WhereClause("debt.id", "==", debt.id)
            )


    suspend fun withdraw(amount : Double, accountId : String = accounts.creditCardPayments) {
        bankClient.transferMoney(Transfer(
            accountId,
            accounts.buffer,
            amount.toInt(),
            "Uttak fra buffer"
        ), tokenStorage.getToken().access_token)
    }

    suspend fun getDebt() = firestoreHelper.findAll<Debt>(debtCollectionName)

    suspend fun addDebt(debt: Debt) {
        firestoreHelper.addData(debt, debtCollectionName)
    }

    suspend fun addDebtPayment(debtPayment: DebtPayment) {
        firestoreHelper.addData(debtPayment, debtPaymentCollectionName)
    }

    fun removeDebt(debt: Debt) {
        firestore.collection(debtCollectionName)
            .doc(debt.id)
            .delete()
    }
}