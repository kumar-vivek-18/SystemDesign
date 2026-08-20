package states

import "ATM/models"

type CardInsertedStateHandler struct{}

func (i *CardInsertedStateHandler) InsertCard(card *models.Card) error
func (i *CardInsertedStateHandler) Authenticate(pin int) error
func (i *CardInsertedStateHandler) WithDraw(amount float64) error
func (i *CardInsertedStateHandler) Deposit(amount float64) error
func (i *CardInsertedStateHandler) BalanceEnquiry() (float64, error)
func (i *CardInsertedStateHandler) EjectCard() error
