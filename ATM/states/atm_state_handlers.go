package states

import (
	"ATM/core"
	"ATM/models"
)

type ATMStates interface {
	InsertCard(atm *core.ATM, card *models.Card) error
	Authenticate(atm *core.ATM, pin int) error
	WithDraw(atm *core.ATM, amount float64) error
	Deposit(atm *core.ATM, amount float64) error
	BalanceEnquiry(atm *core.ATM) (float64, error)
	EjectCard(atm *core.ATM) error
}
