package core

import (
	"ATM/models"
	"sync"
)

type BankService struct {
	Accounts     map[string]*models.Account
	Cards        map[string]*models.Card
	Transactions map[string][]*models.Transaction
	TxnCounter   int
	mu           sync.RWMutex
}

func NewBankService() *BankService {
	return &BankService{

		Accounts:     make(map[string]*models.Account),
		Cards:        make(map[string]*models.Card),
		Transactions: make(map[string][]*models.Transaction),
		TxnCounter:   0,
	}
}
