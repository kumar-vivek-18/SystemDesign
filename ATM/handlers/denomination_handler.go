package handlers

import (
	"ATM/enums"
)

type DenominationHandler struct {
	denomination enums.Denominations
	count        int
	nextHandler  *CashHandler
}

func NewDenomination(denomination enums.Denominations, count int) *DenominationHandler {
	return &DenominationHandler{
		denomination: denomination,
		count:        count,
	}
}

func (d *DenominationHandler) SetNextHandler(handler *CashHandler) {
	d.nextHandler = handler
}

func (d *DenominationHandler) GetNextHandler() *CashHandler {
	return d.nextHandler
}

func (d *DenominationHandler) AddDenomination(count int) {
	d.count += count
}

func (d *DenominationHandler) ReduceDenomination(count int) {
	d.count -= count
}

func (d *DenominationHandler) Dispense(amount int, result map[enums.Denominations]int) {
	notesCount := amount / int(d.denomination)
	notesCount = min(notesCount, d.count)
	if notesCount > 0 {
		d.count -= notesCount
		result[d.denomination] = notesCount
	}

	amount -= notesCount * int(d.denomination)
	if amount > 0 && d.nextHandler != nil {
		d.GetNextHandler()
	}
}
