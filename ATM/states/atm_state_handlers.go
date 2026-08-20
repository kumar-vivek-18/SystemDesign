package states

import (
	"ATM/models"
)

type ATMStateHandler interface {
	InsertCard(card *models.Card) error
	Authenticate(pin int) error
	WithDraw(amount float64) error
	Deposit(amount float64) error
	BalanceEnquiry() (float64, error)
	EjectCard() error
}
