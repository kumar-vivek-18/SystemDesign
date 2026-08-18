package atm

type Card struct {
	CardNumber string
	Pin        int
}

func NewCard(cardNumber string, pin int) *Card {
	return &Card{
		CardNumber: cardNumber,
		Pin:        pin,
	}
}
