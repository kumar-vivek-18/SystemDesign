package models

import (
	"ATM/exceptions"
	"sync"
)

type Account struct {
	AccoutNumber string
	CustomerName string
	Balance      float64
	mu           sync.Mutex
}

func NewAccount(accountNumber, customerName string, balance float64) *Account {
	return &Account{
		AccoutNumber: accountNumber,
		CustomerName: customerName,
		Balance:      balance,
	}
}

func (a *Account) GetAccountNumber() string {
	return a.AccoutNumber
}

func (a *Account) GetAccountBalance() float64 {
	a.mu.Lock()
	defer a.mu.Unlock()
	return a.Balance
}

func (a *Account) GetCustomerName() string {
	return a.CustomerName
}

func (a *Account) Credit(amount float64) error {
	a.mu.Lock()
	defer a.mu.Unlock()
	a.Balance += amount
	return nil
}

func (a *Account) Debit(amount float64) error {
	a.mu.Lock()
	defer a.mu.Unlock()
	if a.Balance < amount {
		return exceptions.ErrInsufficientBalance
	}
	a.Balance -= amount
	return nil
}
