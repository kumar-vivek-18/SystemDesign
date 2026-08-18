package atm

import "sync"

type Account struct {
	AccoutNumber string
	Balance      int
	mu           sync.Mutex
}
