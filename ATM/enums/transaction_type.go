package enums

type TransactionType string

const (
	TransactionTypeCredit         TransactionType = "CREDIT"
	TransactionTypeDebit          TransactionType = "DEBIT"
	TransactionTypeBalanceEnquiry TransactionType = "BALANCE_ENQUIRY"
)
