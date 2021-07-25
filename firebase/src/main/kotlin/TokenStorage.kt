import api_client.BankClient
import model.AccessToken

class TokenStorage(val client: BankClient) {
    var token : AccessToken? = null

    suspend fun getToken() : AccessToken {
        if(token.isValid) {
            return token!!
        } else {
            return client.getAccessToken()!!
        }
    }
}

val AccessToken?.isValid
    get() = this?.isValid ?: false