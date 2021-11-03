object accounts {
    open val buffer by lazy { functions.config().accounts.buffer as String }
    open val creditCardPayments by lazy {  functions.config().accounts.credit_card_payments as String }
    open val paymentsBuffer by lazy { functions.config().accounts.buffer_payments as String }
    open val houseHoldExpenses by lazy { functions.config().accounts.household_expenses as String }
    open val creditCard by lazy { functions.config().accounts.creditcard as String }
    open val generalUse by lazy { functions.config().accounts.general_use as String }
    open val clothes by lazy { functions.config().accounts.clothes as String }
    open val regularPersonalCosts by lazy { functions.config().accounts.regular_personal_costs as String }
    open val bills by lazy { functions.config().accounts.bills as String }

    //orker ikke gjøre dette finere...
    open fun findAccountName(accountNr : String) = when(accountNr) {
        buffer -> "Buffer"
        creditCard -> "Credit card"
        paymentsBuffer -> "Payments buffer"
        houseHoldExpenses -> "Household expsenses"
        creditCardPayments -> "Credit card payments"
        generalUse -> "General use"
        clothes -> "Clothing"
        regularPersonalCosts -> "Regular personal costs"
        bills -> "Bills"
        else -> "Unknown accountnr: $accountNr"
    }
}