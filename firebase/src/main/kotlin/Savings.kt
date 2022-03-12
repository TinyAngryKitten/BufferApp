@file:OptIn(ExperimentalTime::class)

import api_client.BankClient
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import model.Account
import model.Transfer
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.days

class Savings(
        val bankClient: BankClient,
        val tokenStorage: TokenStorage,
        val payments: Payments,
        val transactionTimeframe: Duration = Duration.days(7),
        val accountUtil: Accounts = Accounts()
) {
    //not tested yet, will it work?
    suspend fun reserveRemainingFundsOfAccounts(accounts: List<String>, savingsAccount: String) =
        accounts.forEach {
            try {
                reserveRemainingFundsOfAccount(it, savingsAccount)
            } catch (e: Exception) {
                discordAlert("Savings", "Could not reserve remaining funds of ${accountUtil.findAccountName(it)} due to error: ${e.message}")
            }
        }

    private suspend fun reserveRemainingFundsOfAccount(account : String, savingsAccount: String) {
        val outstandingDebt = getOutstandingPaymentsForAccount(account)
        val remainingBalanceAfterDebt = bankClient.fetchAccount(account, tokenStorage.getToken().access_token).balance - outstandingDebt
        if(remainingBalanceAfterDebt > 0) {
            discordAlert("Savings", "$remainingBalanceAfterDebt is left on ${accountUtil.findAccountName(account)} at the end of the month, moving it to the savings account.")
            bankClient.transferMoney(
                    Transfer(
                            to = savingsAccount,
                            from = account,
                            message = "Savings",
                            amount = remainingBalanceAfterDebt
                    ),
                    tokenStorage.getToken().access_token
            )
        } else {
            discordAlert("Savings", "${accountUtil.findAccountName(account)} is over budget by ${remainingBalanceAfterDebt.absoluteValue} kr")
        }
    }

    private suspend fun getOutstandingPaymentsForAccount(account: String, sinceDate: Instant = Clock.System.now().minus(transactionTimeframe)) =
        bankClient.fetchTransactions(
                tokenStorage.getToken().access_token,
                account,
                startDate = sinceDate
        ).items.filter {
            payments.hasBeenHandled(it, sinceDate)
        }.fold(0.0) {
            acc, transaction ->
            if(transaction.amount < 0) acc + transaction.amount.absoluteValue
            else acc
        }
}