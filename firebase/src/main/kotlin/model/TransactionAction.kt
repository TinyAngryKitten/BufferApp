package model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.Json
import kotlin.js.json

@Serializable
data class TransactionAction(
    val transaction: Transaction,
    val account : String,
    val amount : Int,
    val action : String,
    override val id: String = "",
    override val timestamp: Float = Clock.System.now().toEpochMilliseconds().toFloat()
) : JsJSON {
    override val jsonObject: Json
        get() = json(
            ::action.name to action,
            ::account.name to account,
            ::amount.name to amount,
            ::transaction.name to transaction.jsonObject,
            ::timestamp.name to timestamp
        )

    override fun addId(id: String): JsJSON = copy(id = id)
}