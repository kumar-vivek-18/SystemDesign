package core

import (
	"ATM/enums"
	"ATM/exceptions"
	"ATM/models"
	"math/rand/v2"
	"strings"
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

func (b *BankService) createRandomNumber(len int) string {
	var sb strings.Builder
	sb.Grow(len)
	for range len {
		sb.WriteByte(byte('0' + rand.N(10)))
	}
	return sb.String()
}

func (b *BankService) CreateAccount(customerName string, initialBalance float64) {
	b.mu.Lock()
	defer b.mu.Unlock()

	accoutNumber := b.createRandomNumber(10)
	b.Accounts[accoutNumber] = models.NewAccount(accoutNumber, customerName, initialBalance)
}

func (b *BankService) CreateCard(accoutNumber string, pin int) error {
	b.mu.Lock()
	defer b.mu.Unlock()
	if _, ok := b.Accounts[accoutNumber]; !ok {
		return exceptions.ErrAccountNotFound
	}
	cardNumber := b.createRandomNumber(16)

	b.Cards[cardNumber] = models.NewCard(cardNumber, pin, accoutNumber)
	return nil
}

func (b *BankService) Authenticate(cardNumber string, pin int) (*models.Account, error) {
	card, ok := b.Cards[cardNumber]
	if !ok {
		return nil, exceptions.ErrCardNotFound
	}
	if card.Pin != pin {
		return nil, exceptions.ErrInvalidPin
	}
	account, ok := b.Accounts[card.AccountNumber]
	if !ok {
		return nil, exceptions.ErrAccountNotFound
	}
	return account, nil
}

func (b *BankService) GetBalance(accountNumber string) (float64, error) {
	b.mu.RLock()
	defer b.mu.RUnlock()
	account, ok := b.Accounts[accountNumber]
	if !ok {
		return 0, exceptions.ErrAccountNotFound
	}
	return account.GetAccountBalance(), nil
}

func (b *BankService) Debit(accountNumber string, amount float64) error {
	b.mu.RLock()
	defer b.mu.RUnlock()
	account, ok := b.Accounts[accountNumber]
	if !ok {
		return exceptions.ErrAccountNotFound
	}
	return account.Debit(amount)
}

func (b *BankService) Credit(accountNumber string, amount float64) error {
	b.mu.RLock()
	defer b.mu.RUnlock()
	account, ok := b.Accounts[accountNumber]
	if !ok {
		return exceptions.ErrAccountNotFound
	}
	return account.Credit(amount)
}

func (b *BankService) RecordTransaction(txnType enums.TransactionType, accoutNumber string, amount float64) *models.Transaction {
	b.mu.RLock()
	defer b.mu.RUnlock()
	txnID := b.createRandomNumber(8)
	transaction := models.NewTransaction(txnID, accoutNumber, amount, txnType)
	b.TxnCounter++
	b.Transactions[accoutNumber] = append(b.Transactions[accoutNumber], transaction)
	return transaction
}

func (b *BankService) ListTransactions(accountNumber string) []*models.Transaction {
	b.mu.RLock()
	defer b.mu.RUnlock()

	return b.Transactions[accountNumber]
}
