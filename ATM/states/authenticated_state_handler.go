package states

import "ATM/models"

type AuthenticatedStateHandler struct{}

func (i *AuthenticatedStateHandler) InsertCard(card *models.Card) error
func (i *AuthenticatedStateHandler) Authenticate(pin int) error
func (i *AuthenticatedStateHandler) WithDraw(amount float64) error
func (i *AuthenticatedStateHandler) Deposit(amount float64) error
func (i *AuthenticatedStateHandler) BalanceEnquiry() (float64, error)
func (i *AuthenticatedStateHandler) EjectCard() error
