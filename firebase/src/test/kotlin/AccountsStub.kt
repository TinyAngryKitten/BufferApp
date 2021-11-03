class AccountsStub (
    override val buffer: String = "buffer",
    override val clothes: String = "clothes",
    override val creditCard: String = "creditCard",
    override val creditCardPayments: String = "creditCardPayments",
    override val generalUse: String = "generalUse",
    override val houseHoldExpenses: String = "houseHodlExpenses",
    override val paymentsBuffer: String = "paymentsBuffer",
): Accounts() {
    override fun findAccountName(accountNr : String) = when(accountNr) {
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