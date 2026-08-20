package exceptions

import "errors"

var (
	ErrAccountNotFound       = errors.New("Account not found.")
	ErrCardNotFound          = errors.New("Card not found.")
	ErrInsufficientBalance   = errors.New("Insufficient balance in account.")
	ErrInsufficientCashInATM = errors.New("Insufficient cash in atm.")
	ErrInvalidCard           = errors.New("Invalid Card.")
	ErrInvalidPin            = errors.New("Invalid Pin.")
)
