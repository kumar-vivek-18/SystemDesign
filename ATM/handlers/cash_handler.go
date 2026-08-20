package handlers

import "ATM/enums"

type CashHandler interface {
	SetNextHandler(handler CashHandler)
	Dispense(amount int, result map[enums.Denominations]int)
}
