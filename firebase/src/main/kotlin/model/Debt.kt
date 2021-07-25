package model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.js.json

@Serializable
data class Debt(
    val amount : Double,
    val nrOfPayments : Int,
    val name : String,
    override val id : String = "",
    override val timestamp : Float = Clock.System.now().toEpochMilliseconds().toFloat()
) : JsJSON {
    override fun addId(id: String): JsJSON = copy(id=id)

    override val jsonObject = json(
        this::amount.name to amount,
        this::nrOfPayments.name to nrOfPayments,
        this::name.name to name,
        this::id.name to id,
        this::timestamp.name to timestamp
    )
}