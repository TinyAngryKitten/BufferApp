package model

import accounts

fun parseMCC(str : String?) : MCC? =
    try {
        MCC.values().find { it.value == str?.toInt() }
    } catch(e : Exception) {
        null
    }

val MCC?.group : MccGroup
    get() = MccGroup.subclasses.find {
        this in it.mccValues
    } ?: MccGroup.Unknown

sealed class MccGroup {
    abstract val mccValues : List<MCC>
    abstract val withdrawalAccount : String

    companion object {
        val subclasses: List<MccGroup> = listOf(
            HouseholdGroup,
        )
    }

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

    object SmallPersonalCosts : MccGroup() {
        override val withdrawalAccount: String = accounts.generalUse

        override val mccValues: List<MCC> = listOf(
            MCC.CosmeticStores//parfyme / deo
        )
    }
}