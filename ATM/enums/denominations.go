package enums

type Denominations int

const (
	FiveHundred Denominations = 500
	Hundred     Denominations = 100
	Fifty       Denominations = 50
)

var AllDenominations = []Denominations{FiveHundred, Hundred, Fifty}
