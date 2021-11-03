package testdata

val fjellsport = """
    {
            "accountingDate": "2021-09-14T00:00",
            "amount": -1127,
            "text": "*1823 12.09 NOK 1127.00 Klarna Fjellsport.no Kurs: 1.0000",
            "transactionType": "VISA VARE",
            "transactionTypeText": "VISA VARE",
            "isReservation": false,
            "source": "Archive",
            "cardDetails": {
                "cardNumber": "*3822",
                "currencyAmount": 1127,
                "currencyRate": 1,
                "merchantCategoryCode": "5655",
                "merchantCategoryDescription": "Sportsutstyr",
                "merchantCity": "Sandefjord",
                "merchantName": "Klarna Fjellsport.no",
                "originalCurrencyCode": "NOK",
                "purchaseDate": "2021-09-12T00:00",
                "transactionId": "62125551602530501"
            }
        }
""".trimIndent()