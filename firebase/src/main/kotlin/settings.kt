package main.kotlin

import functions

object settings {
    val nrOfDaysToCheckForTransactions = (functions.config().settings.nr_of_days_to_check_for_transactions?.toString() ?: "5").toInt()
}