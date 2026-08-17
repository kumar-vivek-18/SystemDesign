package enums

type ATMState string

const (
	IDLE          ATMState = "IDLE"
	CARD_INSERTED ATMState = "CARD_INSERTED"
	AUTHENTICATED ATMState = "AUTHENTICATED"
	DISPENSE_CASH ATMState = "DISPENSE_CASH"
)
