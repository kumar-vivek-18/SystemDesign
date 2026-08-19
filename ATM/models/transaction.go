package models

import (
	"ATM/enums"
	"fmt"
	"time"
)

type Transaction struct {
	transactionID   string
	accountNumber   string
	amount          float64
	transactionType enums.TransactionType
	timestamp       time.Time
}

func NewTransaction(txnID, accountNumber string, amount float64, txnType enums.TransactionType) *Transaction {
	return &Transaction{
		transactionID:   txnID,
		accountNumber:   accountNumber,
		amount:          amount,
		transactionType: txnType,
		timestamp:       time.Now().Local(),
	}
}

func (t *Transaction) TransactionID() string {
	return t.transactionID
}

func (t *Transaction) Amount() float64 {
	return t.amount
}

func (t *Transaction) TransactionType() enums.TransactionType {
	return t.transactionType
}

func (t *Transaction) AccountNumber() string {
	return t.accountNumber
}

func (t *Transaction) String() string {
	return fmt.Sprintf("Transaction(id: '%s', type:'%s', amount: Rs'%v', account: '%s', time: '%s')",
		t.transactionID, t.transactionType, t.amount, t.accountNumber, t.timestamp)
}
