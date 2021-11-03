package testdata

import AccountsStub
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Account
import model.AccountResponse

private val accStub = AccountsStub()
val accountsWithLotsOfMoney = AccountResponse(0,
        listOf(
                Account(
                        accStub.clothes,
                        "183791723",
                        "Mr person person",
                        "clothes",
                        "Fake account",
                        200000.0,
                        200000.0,
                ),
                Account(
                        accStub.creditCardPayments,
                        "18337917223",
                        "Mr person person",
                        "creditcards",
                        "Fake account",
                        200000.0,
                        200000.0,
                ),
                Account(
                        accStub.generalUse,
                        "18333",
                        "Mr person person",
                        "general use",
                        "Fake account",
                        200000.0,
                        200000.0,
                ),
                Account(
                        accStub.paymentsBuffer,
                        "1832237917223",
                        "Mr person person",
                        "payments buffer",
                        "Fake account",
                        200000.0,
                        200000.0,
                )
        )
)