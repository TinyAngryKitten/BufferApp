import api_client.BankClient
import kotlinx.datetime.Instant
import model.*

class BankClientStub(
        override val accountsPath: String = "",
        override val baseApiUrl: String = "",
        override val clientId: String = "",
        override val clientSecret: String = "",
        override val identityServerUrl: String = "",
        override val transactionArchivePath: String = "",
        override val transactionPath: String = "",
        override val transferPath: String = "",
        override val userid: String = "",
        var fetchAccountStub : suspend (String, String) -> Account = {_,_ -> TODO() },
        var fetchArchivedTransactionsStub : suspend (String, String, Int?, Instant?, Instant?) -> TransactionResponse = {_,_,_,_,_ -> TODO() },
        var fetchTransactionsStub : suspend (String, String, Int?, Instant?, Instant?) -> TransactionResponse = {_,_,_,_,_ -> TODO() },
        var listAccountsStub : suspend (String) -> AccountResponse = {_ -> TODO() },
        var transferMoneyStub : suspend (Transfer, String) -> Boolean = {_,_ -> TODO() },
        var getAccessTokenStub : suspend () -> AccessToken? = { null }
        ) : BankClient() {
    override suspend fun getAccessToken(): AccessToken? {
        return getAccessTokenStub()
    }

    override suspend fun fetchAccount(accountId: String, token: String): Account {
        return fetchAccountStub(accountId, token)
    }

    override suspend fun fetchArchivedTransactions(token: String, accountId: String, maxCount: Int?, startDate: Instant?, endDate: Instant?): TransactionResponse {
        return fetchArchivedTransactionsStub(token, accountId, maxCount, startDate, endDate)
    }

    override suspend fun fetchTransactions(token: String, accountId: String, maxCount: Int?, startDate: Instant?, endDate: Instant?): TransactionResponse {
        return fetchTransactionsStub(token, accountId, maxCount, startDate, endDate)
    }

    override suspend fun listAccounts(token: String): AccountResponse {
        return listAccountsStub(token)
    }

    override suspend fun transferMoney(transfer: Transfer, token: String): Boolean {
        return transferMoneyStub(transfer, token)
    }
}