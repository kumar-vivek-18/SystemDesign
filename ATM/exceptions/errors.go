package exceptions

import "errors"

var (
	ErrInsufficientBalance   = errors.New("Insufficient balance in account.")
	ErrInsufficientCashInATM = errors.New("Insufficient cash in atm.")
	ErrInvalidCard           = errors.New("Invalid Card.")
	ErrInvalidPin            = errors.New("Invalid Pin.")
)
