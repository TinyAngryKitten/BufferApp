package model

import accounts

fun parseMCC(str : String?) : MCC? =
    try {
        MCC.values().find { it.value == str?.toInt() }
    } catch(e : Exception) {
        null
    }

sealed class MccGroup {

    abstract val mccValues : List<MCC>
    abstract val withdrawalAccount : String

    object Unknown : MccGroup() {
        override val mccValues: List<MCC> = listOf()
        override val withdrawalAccount: String = accounts.generalUse
    }

    object HouseholdGroup : MccGroup() {
        override val withdrawalAccount: String = accounts.houseHoldExpenses

        override val mccValues: List<MCC> = listOf(
            MCC.MiscellaneousFoodStores,
            MCC.PetShopsPetFoodAndSupplies,
            MCC.GroceryStoresSupermarkets,
        )
    }
}