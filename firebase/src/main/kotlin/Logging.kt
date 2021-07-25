import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*

private val webhookUrl : String by lazy {
    functions.config().discord.webhook_url as String
}

val client : HttpClient
    get() = HttpClient(JsClient())

fun log(str : String) {
    functions.logger.log(str);
}

suspend fun discordAlert(title : String, msg : String) : Boolean =
    client.use { client ->
        client.request<HttpResponse>(webhookUrl) {
            method = HttpMethod.Post
            body = """{
                "content" : "$msg",
                "title" : "$title",
                "username" : "Firebase functions"
                }
            """
            header("Content-Type", "application/json")
        }.status == HttpStatusCode.NoContent
    }
