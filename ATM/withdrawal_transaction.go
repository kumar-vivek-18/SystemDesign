package atm

type WithDrawalTransaction struct {
	BaseTransaction
}

func NewWithDrawalTransaction(transactionID string, account *Account, amount float64) *DepositTransaction {
	return &DepositTransaction{
		BaseTransaction: BaseTransaction{
			TransactionID: transactionID,
			Account:       account,
			Amount:        amount,
		},
	}
}

func (t *WithDrawalTransaction) Execute() error {
	return t.Account.Debit(t.Amount)
}
