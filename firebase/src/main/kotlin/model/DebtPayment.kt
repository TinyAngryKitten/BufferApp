package model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.js.json

@Serializable
data class DebtPayment(
    val amount : Double,
    val paymentNr : Int,
    val debt: Debt,
    override val timestamp: Float = Clock.System.now().toEpochMilliseconds().toFloat(),
    override val id : String = "",
) : JsJSON {
    override fun addId(id: String): JsJSON = copy(id=id)

    override val jsonObject = json(
        this::amount.name to amount,
        this::paymentNr.name to paymentNr,
        this::debt.name to debt.jsonObject,
        this::timestamp.name to timestamp,
        this::id.name to id
    )
}