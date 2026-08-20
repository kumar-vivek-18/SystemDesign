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

func (c *Card) getCardNumber() string {
	return c.CardNumber
}

func (c *Card) getPin() int {
	return c.Pin
}

func (c *Card) getAccountNumber() string {
	return c.AccountNumber
}
