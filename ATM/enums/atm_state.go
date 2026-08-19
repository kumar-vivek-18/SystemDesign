package enums

type ATMState string

const (
	ATMStateIdle          ATMState = "IDLE"
	ATMSStateCardInserted ATMState = "CARD_INSERTED"
	ATMStateAuthenticated ATMState = "AUTHENTICATED"
)
