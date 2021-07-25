package model

import firebase
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlin.js.Date
import kotlin.js.Json
import kotlin.js.json

    interface JsJSON {
    val jsonObject: Json
    val id : String
    val timestamp : Float

    fun addId(id : String) : JsJSON
}

val LocalDateTime.timestamp
    get() = toInstant(norwegianTimeZone).toEpochMilliseconds().toFloat()

fun LocalDateTime.Companion.fromTimestamp(timestamp : Float) = Instant
    .fromEpochMilliseconds(timestamp.toLong())
    .toLocalDateTime(norwegianTimeZone)

//add JsJson method to SimpleBankClient models
val Transaction.jsonObject
    get() = json(
        ::accountingDate.name to accountingDate.toString(),
        ::amount.name to amount,
        ::text.name to text,
        ::transactionType.name to transactionType,
        ::transactionTypeText.name to transactionTypeText,
        ::isReservation.name to isReservation,
        ::source.name to source,
        ::otherAccountNumber.name to otherAccountNumber,
        ::cardDetails.name to cardDetails?.jsonObject,
        ::transactionDetail.name to transactionDetail?.jsonObject,
    )

val TransactionDetail.jsonObject
    get() = json(
        ::formattedDAccountNumber.name to formattedDAccountNumber,
        ::transactionId.name to transactionId,
        ::cid.name to cid,
        ::receiverName.name to receiverName,
        ::numericReference.name to numericReference,
        ::payerName.name to payerName,
        ::registrationDate.name to registrationDate.toString()
    )

val CardDetails.jsonObject
    get() = json(
        ::cardNumber.name to cardNumber,
        ::currencyAmount.name to currencyAmount,
        ::currencyRate.name to currencyRate,
        ::merchantCategoryCode.name to merchantCategoryCode,
        ::merchantCategoryDescription.name to merchantCategoryDescription,
        ::merchantCity.name to merchantCity,
        ::merchantName.name to merchantName,
        ::originalCurrencyCode.name to originalCurrencyCode,
        ::purchaseDate.name to purchaseDate.toString(),
        ::transactionId.name to transactionId,
    )