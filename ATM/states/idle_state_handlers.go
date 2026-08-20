package states

import "ATM/models"

type IdleStateHandler struct{}

func (i *IdleStateHandler) InsertCard(card *models.Card) error
func (i *IdleStateHandler) Authenticate(pin int) error
func (i *IdleStateHandler) WithDraw(amount float64) error
func (i *IdleStateHandler) Deposit(amount float64) error
func (i *IdleStateHandler) BalanceEnquiry() (float64, error)
func (i *IdleStateHandler) EjectCard() error
