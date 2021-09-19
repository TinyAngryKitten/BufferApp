object accounts {
    val buffer = functions.config().accounts.buffer as String
    val creditCardPayments =  functions.config().accounts.credit_card_payments as String
    val paymentsBuffer = functions.config().accounts.buffer_payments as String
    val houseHoldExpenses = functions.config().accounts.household_expenses as String
    val creditCard = functions.config().accounts.creditcard as String
    val generalUse = functions.config().accounts.general_use as String
    val clothes = functions.config().accounts.clothes as String

    //orker ikke gjøre dette finere...
    fun findAccountName(accountNr : String) = when(accountNr) {
        buffer -> "Buffer"
        creditCard -> "Credit card"
        paymentsBuffer -> "Payments buffer"
        houseHoldExpenses -> "Household expsenses"
        creditCardPayments -> "Credit card payments"
        generalUse -> "General use"
        clothes -> "Clothing"
        else -> "Unknown accountnr: $accountNr"
    }
}