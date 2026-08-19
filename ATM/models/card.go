package models

type Card struct {
	CardNumber    string
	Pin           int
	AccountNumber string
}

func NewCard(cardNumber string, pin int, accountNumber string) *Card {
	return &Card{
		CardNumber:    cardNumber,
		Pin:           pin,
		AccountNumber: accountNumber,
	}
}
