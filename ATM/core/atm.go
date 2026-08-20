package core

import (
	"ATM/enums"
	"ATM/models"
	"ATM/states"
	"sync"
)

type ATM struct {
	currentState  enums.ATMState
	stateHandlers map[enums.ATMState]states.ATMStateHandler

	bankService   *BankService
	cashDispenser *CashDispenser

	currentCard    *models.Card
	currentAccount *models.Account

	mu sync.Mutex
}

var (
	atmInstance *ATM
	atmOnce     sync.Once
)

func getATMInstance(bankService *BankService, cashDispenser *CashDispenser) *ATM {
	atmOnce.Do(func() {
		atmInstance = &ATM{
			currentState: enums.ATMStateIdle,
			stateHandlers: map[enums.ATMState]states.ATMStateHandler{
				enums.ATMStateIdle:          &states.IdleStateHandler{},
				enums.ATMSStateCardInserted: &states.CardInsertedStateHandler{},
				enums.ATMStateAuthenticated: &states.AuthenticatedStateHandler{},
			},
		}
	})
	return atmInstance
}
