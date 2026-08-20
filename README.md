# Go (Golang) — Practical Reference

A working reference for day-to-day Go development: core language, concurrency, stdlib patterns, and idioms.

## Table of Contents
1. [Variables, Types & Constants](#1-variables-types--constants)
2. [Control Flow](#2-control-flow)
3. [Functions](#3-functions)
4. [Structs & Methods](#4-structs--methods)
5. [Interfaces](#5-interfaces)
6. [Pointers](#6-pointers)
7. [Arrays, Slices & Maps](#7-arrays-slices--maps)
8. [Error Handling](#8-error-handling)
9. [Goroutines](#9-goroutines)
10. [Channels](#10-channels)
11. [select](#11-select)
12. [sync Package](#12-sync-package)
13. [Context](#13-context)
14. [Generics](#14-generics)
15. [Packages & Modules](#15-packages--modules)
16. [JSON & Encoding](#16-json--encoding)
17. [Testing](#17-testing)
18. [HTTP (net/http)](#18-http-nethttp)
19. [Database (database/sql)](#19-database-databasesql)
20. [Common Concurrency Patterns](#20-common-concurrency-patterns)
21. [Profiling & Performance](#21-profiling--performance)
22. [Idioms & Gotchas](#22-idioms--gotchas)

---

## 1. Variables, Types & Constants

```go
// Declaration styles
var x int = 10
var y = 10          // type inferred
z := 10              // short declaration (function scope only)

var (
    a int
    b string
    c bool
)

// Zero values: 0, "", false, nil (for pointers/slices/maps/chans/funcs/interfaces)

// Constants + iota
type Weekday int

const (
    Sunday Weekday = iota // 0
    Monday                 // 1
    Tuesday                // 2
)

const (
    _  = iota // skip 0
    KB = 1 << (10 * iota)
    MB
    GB
)

// Type conversion (explicit only, no implicit coercion)
var i int = 42
var f float64 = float64(i)
var u uint = uint(f)

// Basic types
// bool, string
// int, int8, int16, int32, int64, uint, uint8(byte), uint16, uint32, uint64, uintptr
// float32, float64
// complex64, complex128
// rune (alias for int32, represents a Unicode code point)
```

---

## 2. Control Flow

### if / else
```go
if v := compute(); v > 0 {
    fmt.Println(v)
} else if v == 0 {
    fmt.Println("zero")
} else {
    fmt.Println("negative")
}
```

### for — Go's only loop keyword
```go
// classic
for i := 0; i < 10; i++ { }

// while-style
n := 0
for n < 10 {
    n++
}

// infinite
for {
    if done() {
        break
    }
}

// range over slice
for i, v := range []string{"a", "b"} {
    fmt.Println(i, v)
}

// range over map (order is randomized)
for k, v := range map[string]int{"a": 1} { fmt.Println(k, v) }

// range over string (iterates runes, not bytes)
for i, r := range "héllo" {
    fmt.Println(i, r)
}

// range over channel (until closed)
for v := range ch { fmt.Println(v) }

// range over int (Go 1.22+)
for i := range 5 { fmt.Println(i) } // 0..4

// range over function iterator (Go 1.23+, range-over-func)
func Seq(yield func(int) bool) {
    for i := 0; i < 3; i++ {
        if !yield(i) { return }
    }
}
for v := range Seq { fmt.Println(v) }

// labeled break/continue
outer:
for i := 0; i < 3; i++ {
    for j := 0; j < 3; j++ {
        if j == 1 { continue outer }
        if i == 2 { break outer }
    }
}
```

### switch
```go
switch day {
case "Mon", "Tue", "Wed", "Thu", "Fri":
    fmt.Println("weekday")
case "Sat", "Sun":
    fmt.Println("weekend")
default:
    fmt.Println("unknown")
}

// no expression = clean if-else chain
switch {
case score >= 90:
    grade = "A"
case score >= 80:
    grade = "B"
default:
    grade = "F"
}

// type switch
func describe(i interface{}) {
    switch v := i.(type) {
    case int:
        fmt.Println("int:", v)
    case string:
        fmt.Println("string:", v)
    case nil:
        fmt.Println("nil")
    default:
        fmt.Printf("unknown type %T\n", v)
    }
}

// fallthrough (explicit, unlike C's default fall-through)
switch 1 {
case 1:
    fmt.Println("one")
    fallthrough
case 2:
    fmt.Println("two") // also prints
}
```

### defer, panic, recover
```go
func process() (err error) {
    defer func() {
        if r := recover(); r != nil {
            err = fmt.Errorf("recovered: %v", r)
        }
    }()
    // defers run LIFO, args evaluated at defer time (not at execution)
    defer fmt.Println("1")
    defer fmt.Println("2") // prints before "1"

    panic("something broke")
}

// defer commonly used for cleanup
f, err := os.Open("file.txt")
if err != nil { return err }
defer f.Close()
```

---

## 3. Functions

```go
// multiple return values
func divide(a, b int) (int, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}

// named returns
func split(sum int) (x, y int) {
    x = sum * 4 / 9
    y = sum - x
    return // naked return
}

// variadic
func sum(nums ...int) int {
    total := 0
    for _, n := range nums { total += n }
    return total
}
sum(1, 2, 3)
sum([]int{1, 2, 3}...) // spread a slice

// functions as values / closures
func counter() func() int {
    count := 0
    return func() int {
        count++
        return count
    }
}
next := counter()
next() // 1
next() // 2

// function types as parameters (higher-order functions)
func apply(nums []int, f func(int) int) []int {
    out := make([]int, len(nums))
    for i, n := range nums { out[i] = f(n) }
    return out
}

// method values / expressions
type T struct{ x int }
func (t T) Add(n int) int { return t.x + n }
t := T{x: 1}
f := t.Add       // method value, bound to t
g := T.Add       // method expression, needs receiver as first arg
g(t, 5)
```

---

## 4. Structs & Methods

```go
type User struct {
    ID    int
    Name  string
    Email string
}

// struct literals
u1 := User{ID: 1, Name: "Vivek", Email: "v@example.com"}
u2 := User{1, "Vivek", "v@example.com"} // positional, avoid in production code

// embedding (composition, not inheritance)
type Admin struct {
    User        // embedded field, promotes User's fields/methods
    Level int
}
a := Admin{User: User{Name: "Root"}, Level: 9}
fmt.Println(a.Name) // promoted field access

// methods: value receiver vs pointer receiver
func (u User) String() string {           // value receiver: gets a copy
    return u.Name
}
func (u *User) SetEmail(e string) {       // pointer receiver: mutates original
    u.Email = e
}

// Rule of thumb: if any method needs a pointer receiver, use pointer
// receivers for ALL methods on that type for consistency.

// struct tags (used by encoding/json, validators, ORMs, etc.)
type Product struct {
    ID    int     `json:"id"`
    Name  string  `json:"name"`
    Price float64 `json:"price,omitempty"`
    internal string `json:"-"` // excluded from marshaling
}

// comparing structs (works if all fields are comparable)
u1 == u2 // valid only if User has no slice/map/func fields

// anonymous structs (quick, throwaway grouping)
point := struct{ X, Y int }{X: 1, Y: 2}
```

---

## 5. Interfaces

```go
type Reader interface {
    Read(p []byte) (n int, err error)
}

// Go interfaces are satisfied implicitly — no "implements" keyword
type FileReader struct{}
func (f FileReader) Read(p []byte) (int, error) { return 0, nil } // satisfies Reader

// small, composable interfaces are idiomatic
type Writer interface {
    Write(p []byte) (n int, err error)
}
type ReadWriter interface {
    Reader
    Writer
}

// empty interface: any type
var anything interface{}
var also any // "any" is an alias for interface{} since Go 1.18

// type assertion
var r Reader = FileReader{}
fr, ok := r.(FileReader)
if !ok {
    // handle mismatch — always use the comma-ok form in production code
}

// common stdlib interfaces to know
// io.Reader, io.Writer, io.Closer, io.ReadWriter
// fmt.Stringer  { String() string }
// error         { Error() string }
// sort.Interface{ Len(), Less(i,j), Swap(i,j) }
// context.Context

// nil interface gotcha: an interface holding a typed nil pointer is NOT == nil
type MyErr struct{}
func (e *MyErr) Error() string { return "err" }
func doWork() error {
    var e *MyErr = nil
    return e // returns non-nil error interface wrapping a nil *MyErr!
}
```

---

## 6. Pointers

```go
x := 10
p := &x       // p is *int, points to x
*p = 20       // dereference and assign
fmt.Println(x) // 20

// no pointer arithmetic (unlike C)
// new() allocates zeroed memory, returns pointer
n := new(int) // *int, *n == 0

// passing by pointer avoids copies for large structs, and allows mutation
func scale(u *User, factor int) {
    // mutate via u.Field, Go auto-dereferences for field access
}

// slices, maps, channels, functions are reference-like (share underlying data)
// even when passed by value — the header is copied, backing data is not
```

---

## 7. Arrays, Slices & Maps

### Arrays (fixed size, value type)
```go
var arr [5]int
arr2 := [3]int{1, 2, 3}
arr3 := [...]int{1, 2, 3} // size inferred
```

### Slices (dynamic, reference type — the everyday choice)
```go
s := []int{1, 2, 3}
s = append(s, 4, 5)
s = append(s, otherSlice...)

// make with length and capacity
s2 := make([]int, 5)       // len=5, cap=5, zeroed
s3 := make([]int, 0, 10)   // len=0, cap=10 — pre-allocate to avoid reallocs

// slicing
s[1:3]   // elements 1,2
s[:2]    // first 2
s[2:]    // from index 2 to end
s[:]     // full slice

// len vs cap
len(s)   // number of elements
cap(s)   // capacity of underlying array from the slice's start

// copy
dst := make([]int, len(src))
n := copy(dst, src)

// slice of slices sharing backing array — mutation gotcha
a := []int{1, 2, 3, 4, 5}
b := a[1:3]
b[0] = 99 // also mutates a[1]!

// full slice expression to limit shared capacity (prevents append side effects)
c := a[1:3:3] // len=2, cap=2 — append(c, x) allocates new array, doesn't touch a

// 2D slices
grid := make([][]int, rows)
for i := range grid {
    grid[i] = make([]int, cols)
}

// removing an element (order-preserving)
s = append(s[:i], s[i+1:]...)

// removing an element (fast, order not preserved)
s[i] = s[len(s)-1]
s = s[:len(s)-1]
```

### Maps
```go
m := make(map[string]int)
m["a"] = 1
m2 := map[string]int{"a": 1, "b": 2}

v, ok := m["key"]  // comma-ok idiom to check existence
if !ok { /* key absent */ }

delete(m, "a")

for k, v := range m { } // iteration order is randomized by design

// maps are reference types; zero value is nil
var nilMap map[string]int
nilMap["x"] = 1 // panics: assignment to entry in nil map
_ = nilMap["x"] // reading a nil map is fine, returns zero value

// map with struct values can't be mutated in place
type P struct{ X int }
pm := map[string]P{"a": {X: 1}}
// pm["a"].X = 2 // compile error
p := pm["a"]
p.X = 2
pm["a"] = p // must reassign whole struct, or use map[string]*P
```

---

## 8. Error Handling

```go
// basic error
err := errors.New("something failed")
err2 := fmt.Errorf("context: %w", err) // %w wraps, preserves chain

// checking errors
if err != nil {
    return err
}

// sentinel errors
var ErrNotFound = errors.New("not found")
if errors.Is(err, ErrNotFound) { }

// custom error types
type ValidationError struct {
    Field string
    Msg   string
}
func (e *ValidationError) Error() string {
    return fmt.Sprintf("%s: %s", e.Field, e.Msg)
}

var ve *ValidationError
if errors.As(err, &ve) {
    fmt.Println(ve.Field)
}

// wrapping multiple errors (Go 1.20+)
err3 := errors.Join(err, err2)

// panic/recover — reserved for truly unrecoverable situations,
// NOT a substitute for normal error returns
func safeDivide(a, b int) (result int, err error) {
    defer func() {
        if r := recover(); r != nil {
            err = fmt.Errorf("recovered from panic: %v", r)
        }
    }()
    result = a / b // panics on b == 0
    return
}

// idiomatic error message style: lowercase, no trailing punctuation
// "failed to open file: %w", not "Failed to open file."
```

---

## 9. Goroutines

```go
// starting a goroutine — just add "go"
go doWork()

go func() {
    fmt.Println("running concurrently")
}()

// goroutines are cheap (~2KB stack, grows as needed) — fine to spawn thousands

// COMMON BUG: loop variable capture (fixed by default in Go 1.22+,
// but be careful on older Go or when capturing other mutable state)
for _, v := range items {
    go func() {
        fmt.Println(v) // Go < 1.22: all goroutines may see the same/final v
    }()
}
// fix for Go < 1.22, or general good practice: pass explicitly
for _, v := range items {
    go func(v string) {
        fmt.Println(v)
    }(v)
}

// waiting for goroutines — WaitGroup
var wg sync.WaitGroup
for _, item := range items {
    wg.Add(1)
    go func(it string) {
        defer wg.Done()
        process(it)
    }(item)
}
wg.Wait()

// goroutine leaks: always ensure a goroutine has a way to exit
// (closed channel, cancelled context, etc.) — otherwise it leaks forever
func leaky() {
    ch := make(chan int)
    go func() {
        val := <-ch // blocks forever if nothing ever sends — LEAK
        fmt.Println(val)
    }()
}

// recovering panics in goroutines — panics do NOT propagate across
// goroutine boundaries; an unrecovered panic in a goroutine crashes the
// whole program
go func() {
    defer func() {
        if r := recover(); r != nil {
            log.Println("recovered:", r)
        }
    }()
    riskyOperation()
}()
```

---

## 10. Channels

```go
// unbuffered channel — send blocks until a receiver is ready (synchronization point)
ch := make(chan int)

// buffered channel — send blocks only when buffer is full
ch2 := make(chan int, 10)

// send / receive
ch <- 5
v := <-ch

// close a channel — signals "no more values"; only the sender should close
close(ch)

// receiving from a closed channel returns zero value immediately
v, ok := <-ch // ok == false once closed and drained

// ranging over a channel exits automatically when closed
for v := range ch {
    fmt.Println(v)
}

// directional channels (compile-time safety in function signatures)
func send(ch chan<- int) { ch <- 1 }      // send-only
func recv(ch <-chan int) int { return <-ch } // receive-only

// nil channel: send/receive block forever (useful in select to "disable" a case)
var nilCh chan int
// <-nilCh // blocks forever

// common panics:
// - send on closed channel -> panic
// - close on already-closed channel -> panic
// - close on nil channel -> panic

// producer/consumer
func producer(ch chan<- int, n int) {
    defer close(ch)
    for i := 0; i < n; i++ {
        ch <- i
    }
}
func consumer(ch <-chan int) {
    for v := range ch {
        fmt.Println(v)
    }
}
```

---

## 11. select

```go
// select waits on multiple channel operations, picks whichever is ready
// (random choice if multiple are ready simultaneously)
select {
case v := <-ch1:
    fmt.Println("from ch1:", v)
case v := <-ch2:
    fmt.Println("from ch2:", v)
case ch3 <- 5:
    fmt.Println("sent to ch3")
default:
    fmt.Println("no channel ready, don't block")
}

// timeout pattern
select {
case res := <-resultCh:
    fmt.Println(res)
case <-time.After(2 * time.Second):
    fmt.Println("timed out")
}

// cancellation pattern with context
select {
case res := <-resultCh:
    return res, nil
case <-ctx.Done():
    return nil, ctx.Err()
}

// empty select{} blocks forever (rare, e.g. in a main() that only spawns
// background goroutines and never exits)
```

---

## 12. sync Package

```go
// Mutex — protects shared state
type Counter struct {
    mu    sync.Mutex
    count int
}
func (c *Counter) Inc() {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.count++
}

// RWMutex — many readers OR one writer
type Cache struct {
    mu   sync.RWMutex
    data map[string]string
}
func (c *Cache) Get(k string) string {
    c.mu.RLock()
    defer c.mu.RUnlock()
    return c.data[k]
}
func (c *Cache) Set(k, v string) {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.data[k] = v
}

// WaitGroup — wait for a group of goroutines
var wg sync.WaitGroup
wg.Add(3)
for i := 0; i < 3; i++ {
    go func() { defer wg.Done(); doWork() }()
}
wg.Wait()

// Once — run something exactly once (e.g. lazy singleton init)
var once sync.Once
var instance *Service
func GetService() *Service {
    once.Do(func() {
        instance = &Service{}
    })
    return instance
}

// sync.Map — concurrent map for specific high-contention read-heavy cases
// (prefer a regular map + Mutex unless profiling shows sync.Map helps)
var sm sync.Map
sm.Store("key", "value")
val, ok := sm.Load("key")
sm.Delete("key")
sm.Range(func(k, v any) bool {
    fmt.Println(k, v)
    return true // continue iterating
})

// atomic package — lock-free primitives for simple counters/flags
var counter atomic.Int64
counter.Add(1)
counter.Load()

var flag atomic.Bool
flag.Store(true)

// generic atomic.Value / atomic.Pointer[T] for swapping config/state atomically
var cfg atomic.Pointer[Config]
cfg.Store(&Config{})
current := cfg.Load()
```

---

## 13. Context

```go
// context carries deadlines, cancellation signals, and request-scoped
// values across API boundaries and goroutines

// root contexts
ctx := context.Background() // top-level, e.g. main(), server startup
ctx2 := context.TODO()      // placeholder when unsure / migrating code

// WithCancel — manual cancellation
ctx, cancel := context.WithCancel(context.Background())
defer cancel() // ALWAYS call cancel to release resources, even if ctx finishes naturally

go func() {
    select {
    case <-ctx.Done():
        fmt.Println("cancelled:", ctx.Err()) // context.Canceled
    }
}()
cancel() // triggers Done()

// WithTimeout / WithDeadline
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()

deadline := time.Now().Add(1 * time.Minute)
ctx2, cancel2 := context.WithDeadline(context.Background(), deadline)
defer cancel2()

// on expiry, ctx.Err() returns context.DeadlineExceeded

// WithValue — request-scoped data ONLY (never for optional params or
// passing core business logic dependencies)
type ctxKey string
const userIDKey ctxKey = "userID" // use unexported custom type, never string/int, to avoid collisions

ctx3 := context.WithValue(context.Background(), userIDKey, 42)
if uid, ok := ctx3.Value(userIDKey).(int); ok {
    fmt.Println(uid)
}

// propagating context — always the first parameter, named ctx
func fetchUser(ctx context.Context, id int) (*User, error) {
    req, err := http.NewRequestWithContext(ctx, "GET", url, nil)
    if err != nil {
        return nil, err
    }
    resp, err := http.DefaultClient.Do(req)
    // ...
}

// respecting cancellation inside long-running work
func doWork(ctx context.Context) error {
    for {
        select {
        case <-ctx.Done():
            return ctx.Err()
        default:
            // do a chunk of work
        }
    }
}

// WithTimeoutCause / WithDeadlineCause (Go 1.21+) — attach a reason
ctx4, cancel4 := context.WithTimeoutCause(context.Background(), 3*time.Second, errors.New("upstream slow"))
defer cancel4()
// context.Cause(ctx4) after expiry returns the custom cause

// rules of thumb:
// - never store context in a struct; pass it explicitly
// - never pass nil context; use context.TODO() if truly unsure
// - context.Context should be the first function parameter
// - only put request-scoped values in context, not app config/dependencies
```

---

## 14. Generics

```go
// type parameters (Go 1.18+)
func Max[T cmp.Ordered](a, b T) T {
    if a > b { return a }
    return b
}
Max(3, 5)
Max(3.1, 2.9)
Max("a", "b")

// generic struct
type Stack[T any] struct {
    items []T
}
func (s *Stack[T]) Push(v T) { s.items = append(s.items, v) }
func (s *Stack[T]) Pop() (T, bool) {
    var zero T
    if len(s.items) == 0 { return zero, false }
    v := s.items[len(s.items)-1]
    s.items = s.items[:len(s.items)-1]
    return v, true
}

// constraints
type Number interface {
    ~int | ~int64 | ~float64 // ~ allows named types with this underlying type
}
func Sum[T Number](nums []T) T {
    var total T
    for _, n := range nums { total += n }
    return total
}

// standard constraints
// - any                 (alias for interface{})
// - comparable           (types supporting == and !=, e.g. for map keys)
// - cmp.Ordered          (from "cmp" package: supports <, >, <=, >=)

// generic functions over slices (see also stdlib "slices" and "maps" packages)
func Map[T, U any](s []T, f func(T) U) []U {
    out := make([]U, len(s))
    for i, v := range s { out[i] = f(v) }
    return out
}
func Filter[T any](s []T, pred func(T) bool) []T {
    var out []T
    for _, v := range s {
        if pred(v) { out = append(out, v) }
    }
    return out
}

// stdlib slices/maps packages (Go 1.21+) already provide a lot of this:
slices.Sort(s)
slices.Contains(s, x)
slices.Index(s, x)
maps.Keys(m)   // returns an iterator (Go 1.23+)
```

---

## 15. Packages & Modules

```bash
go mod init github.com/user/project   # start a new module
go get github.com/pkg/errors@v0.9.1   # add/upgrade a dependency
go mod tidy                            # add missing, remove unused deps
go mod vendor                          # vendor dependencies locally
go build ./...
go run main.go
go install ./cmd/mytool
```

```go
// package naming: short, lowercase, no underscores (e.g. "httputil" not "http_util")
// exported (public) identifiers start with a capital letter: func Foo()
// unexported (private) identifiers start lowercase: func foo()

// typical project layout
// /cmd/appname/main.go     — entrypoints
// /internal/...            — private packages, not importable by other modules
// /pkg/...                 — public/shared packages (if intended for external use)
// go.mod, go.sum

// init() — runs automatically before main(), per package, useful for
// registration patterns (e.g. database/sql drivers); avoid overusing it
func init() {
    // setup, e.g. registering a driver
}
```

---

## 16. JSON & Encoding

```go
type User struct {
    ID    int    `json:"id"`
    Name  string `json:"name"`
    Email string `json:"email,omitempty"`
    Age   int    `json:"-"` // never serialized
}

// marshal (struct -> JSON)
data, err := json.Marshal(u)
data2, err := json.MarshalIndent(u, "", "  ") // pretty-printed

// unmarshal (JSON -> struct)
var u2 User
err := json.Unmarshal(data, &u2)

// streaming (large payloads, HTTP bodies)
dec := json.NewDecoder(resp.Body)
err := dec.Decode(&u2)

enc := json.NewEncoder(w)
err := enc.Encode(u)

// unknown/dynamic JSON
var raw map[string]interface{}
json.Unmarshal(data, &raw)

// custom marshal/unmarshal
type Status int
func (s Status) MarshalJSON() ([]byte, error) {
    return json.Marshal(s.String())
}

// other common encodings in stdlib:
// encoding/xml, encoding/csv, encoding/base64, encoding/gob (Go-to-Go binary)
```

---

## 17. Testing

```go
// file: math_test.go (must end in _test.go)
package math

import "testing"

func TestAdd(t *testing.T) {
    got := Add(2, 3)
    want := 5
    if got != want {
        t.Errorf("Add(2,3) = %d, want %d", got, want)
    }
}

// table-driven tests — the standard Go idiom
func TestAdd_Table(t *testing.T) {
    cases := []struct {
        name     string
        a, b     int
        expected int
    }{
        {"positive", 2, 3, 5},
        {"negative", -1, -1, -2},
        {"zero", 0, 0, 0},
    }
    for _, tc := range cases {
        t.Run(tc.name, func(t *testing.T) {
            got := Add(tc.a, tc.b)
            if got != tc.expected {
                t.Errorf("got %d, want %d", got, tc.expected)
            }
        })
    }
}

// setup/teardown
func TestMain(m *testing.M) {
    setup()
    code := m.Run()
    teardown()
    os.Exit(code)
}

// subtests share t, run in parallel with t.Parallel()
func TestParallel(t *testing.T) {
    t.Parallel()
    // ...
}

// benchmarks
func BenchmarkAdd(b *testing.B) {
    for i := 0; i < b.N; i++ {
        Add(2, 3)
    }
}
// go test -bench=. -benchmem

// test helpers
func TestSomething(t *testing.T) {
    t.Helper() // marks this func as a helper, improves failure line reporting
}

// mocking via interfaces (Go's preferred approach — no framework required)
type Store interface {
    Get(id int) (string, error)
}
type mockStore struct{ data map[int]string }
func (m *mockStore) Get(id int) (string, error) {
    v, ok := m.data[id]
    if !ok { return "", errors.New("not found") }
    return v, nil
}

// common commands
// go test ./...             — run all tests
// go test -run TestAdd      — run matching tests
// go test -v                — verbose
// go test -cover            — coverage
// go test -race             — race detector (use routinely for concurrent code)
```

---

## 18. HTTP (net/http)

```go
// simple server
func main() {
    mux := http.NewServeMux()
    mux.HandleFunc("GET /users/{id}", getUser)   // Go 1.22+ method + path params
    mux.HandleFunc("POST /users", createUser)

    srv := &http.Server{
        Addr:         ":8080",
        Handler:      mux,
        ReadTimeout:  5 * time.Second,
        WriteTimeout: 10 * time.Second,
        IdleTimeout:  120 * time.Second,
    }
    log.Fatal(srv.ListenAndServe())
}

func getUser(w http.ResponseWriter, r *http.Request) {
    id := r.PathValue("id")
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(map[string]string{"id": id})
}

// middleware pattern (wrap handlers)
func loggingMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        next.ServeHTTP(w, r)
        log.Printf("%s %s %v", r.Method, r.URL.Path, time.Since(start))
    })
}
handler := loggingMiddleware(mux)

// graceful shutdown
func gracefulShutdown(srv *http.Server) {
    quit := make(chan os.Signal, 1)
    signal.Notify(quit, os.Interrupt, syscall.SIGTERM)
    <-quit

    ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
    defer cancel()
    if err := srv.Shutdown(ctx); err != nil {
        log.Fatal("forced shutdown:", err)
    }
}

// HTTP client — always set timeouts, reuse the client (it pools connections)
var client = &http.Client{
    Timeout: 10 * time.Second,
}

func fetch(ctx context.Context, url string) ([]byte, error) {
    req, err := http.NewRequestWithContext(ctx, "GET", url, nil)
    if err != nil {
        return nil, err
    }
    resp, err := client.Do(req)
    if err != nil {
        return nil, err
    }
    defer resp.Body.Close()

    if resp.StatusCode != http.StatusOK {
        return nil, fmt.Errorf("unexpected status: %d", resp.StatusCode)
    }
    return io.ReadAll(resp.Body)
}
```

---

## 19. Database (database/sql)

```go
import (
    "database/sql"
    _ "github.com/lib/pq" // driver registers itself via init()
)

db, err := sql.Open("postgres", dsn)
if err != nil { log.Fatal(err) }
defer db.Close()

// connection pool tuning — important in production services
db.SetMaxOpenConns(25)
db.SetMaxIdleConns(25)
db.SetConnMaxLifetime(5 * time.Minute)

// query single row
var name string
err = db.QueryRowContext(ctx, "SELECT name FROM users WHERE id=$1", id).Scan(&name)
if errors.Is(err, sql.ErrNoRows) {
    // not found
}

// query multiple rows
rows, err := db.QueryContext(ctx, "SELECT id, name FROM users")
if err != nil { return err }
defer rows.Close()

for rows.Next() {
    var id int
    var name string
    if err := rows.Scan(&id, &name); err != nil {
        return err
    }
}
if err := rows.Err(); err != nil { // always check after the loop
    return err
}

// exec (insert/update/delete)
res, err := db.ExecContext(ctx, "UPDATE users SET name=$1 WHERE id=$2", newName, id)
affected, _ := res.RowsAffected()

// transactions
tx, err := db.BeginTx(ctx, nil)
if err != nil { return err }
defer tx.Rollback() // no-op if committed

if _, err := tx.ExecContext(ctx, "..."); err != nil {
    return err
}
return tx.Commit()

// always use parameterized queries ($1, ?) — never string-concatenate
// user input into SQL (SQL injection risk)
```

---

## 20. Common Concurrency Patterns

### Worker Pool
```go
func workerPool(jobs <-chan int, results chan<- int, numWorkers int) {
    var wg sync.WaitGroup
    for w := 0; w < numWorkers; w++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for job := range jobs {
                results <- process(job)
            }
        }()
    }
    wg.Wait()
    close(results)
}
```

### Fan-out / Fan-in
```go
// fan-out: multiple goroutines read from one channel
// fan-in: merge multiple channels into one
func fanIn(channels ...<-chan int) <-chan int {
    out := make(chan int)
    var wg sync.WaitGroup
    wg.Add(len(channels))
    for _, c := range channels {
        go func(c <-chan int) {
            defer wg.Done()
            for v := range c {
                out <- v
            }
        }(c)
    }
    go func() {
        wg.Wait()
        close(out)
    }()
    return out
}
```

### Pipeline
```go
func generate(nums ...int) <-chan int {
    out := make(chan int)
    go func() {
        defer close(out)
        for _, n := range nums { out <- n }
    }()
    return out
}
func square(in <-chan int) <-chan int {
    out := make(chan int)
    go func() {
        defer close(out)
        for n := range in { out <- n * n }
    }()
    return out
}
// usage: for v := range square(generate(1, 2, 3)) { ... }
```

### errgroup — coordinated goroutines with error propagation
```go
import "golang.org/x/sync/errgroup"

func fetchAll(ctx context.Context, urls []string) ([][]byte, error) {
    g, ctx := errgroup.WithContext(ctx)
    results := make([][]byte, len(urls))
    for i, url := range urls {
        i, url := i, url
        g.Go(func() error {
            data, err := fetch(ctx, url)
            if err != nil { return err }
            results[i] = data
            return nil
        })
    }
    if err := g.Wait(); err != nil { // first error cancels ctx for the rest
        return nil, err
    }
    return results, nil
}
```

### Rate limiting
```go
limiter := time.Tick(200 * time.Millisecond)
for req := range requests {
    <-limiter
    go handle(req)
}

// or golang.org/x/time/rate for token-bucket limiting
lim := rate.NewLimiter(rate.Every(time.Second), 5) // 5 events/sec burst
if lim.Allow() {
    handle()
}
```

### Semaphore (bounding concurrency)
```go
sem := make(chan struct{}, 3) // max 3 concurrent
for _, job := range jobs {
    sem <- struct{}{}
    go func(j Job) {
        defer func() { <-sem }()
        process(j)
    }(job)
}
```

---

## 21. Profiling & Performance

```go
// CPU/memory profiling via net/http/pprof (just import for side effects)
import _ "net/http/pprof"
go func() { log.Println(http.ListenAndServe("localhost:6060", nil)) }()
// then: go tool pprof http://localhost:6060/debug/pprof/profile?seconds=30

// benchmark-driven optimization
// go test -bench=. -benchmem -cpuprofile=cpu.out -memprofile=mem.out
// go tool pprof cpu.out

// common performance tips:
// - preallocate slices/maps with known size: make([]T, 0, n)
// - avoid unnecessary allocations in hot paths (check with -benchmem)
// - use strings.Builder for repeated string concatenation, not +=
// - pass large structs by pointer, small ones by value
// - reuse buffers with sync.Pool for high-frequency allocations
// - avoid defer in extremely hot loops (small but nonzero overhead)

var bufPool = sync.Pool{
    New: func() any { return new(bytes.Buffer) },
}
buf := bufPool.Get().(*bytes.Buffer)
buf.Reset()
defer bufPool.Put(buf)

var sb strings.Builder
for _, s := range parts {
    sb.WriteString(s)
}
result := sb.String()
```

---

## 22. Idioms & Gotchas

```go
// - "Accept interfaces, return structs" — take the narrowest interface
//   needed as a parameter, return concrete types.

// - Errors are values: check them immediately, don't ignore with `_`
//   unless deliberate and documented.

// - Prefer early returns over deep nesting ("guard clauses").
func process(x int) error {
    if x < 0 {
        return errors.New("negative")
    }
    // main logic, unindented
    return nil
}

// - Zero values should be useful: design types so their zero value works
//   without explicit initialization where possible (e.g. sync.Mutex, bytes.Buffer).

// - Struct field alignment affects memory: group similarly-sized fields
//   to reduce padding (matters in high-volume allocations).

// - Slices share backing arrays — be deliberate about when to copy vs. share.

// - "go vet" and "golangci-lint" catch common mistakes; run them in CI.

// - go fmt / gofmt is non-negotiable — code is always auto-formatted.

// - Avoid global mutable state; prefer dependency injection via constructors.

// - Table-driven tests + subtests (t.Run) are the default testing style.

// - Use context for cancellation/timeouts, never for passing core dependencies.

// - "go build -race" / "go test -race" during development for any
//   concurrent code — the race detector catches real bugs cheaply.

// - time.Duration arithmetic is type-safe:
d := 2 * time.Second
d2 := 100 * time.Millisecond

// - String/byte conversions copy data:
b := []byte("hello") // allocates
s := string(b)         // allocates

// - Named return values + naked return are fine for short functions,
//   avoid in long ones (hurts readability).

// - Embedding vs. explicit fields: use embedding for "is-a"/interface
//   satisfaction, explicit named fields for "has-a" composition.
```

---

## Quick Reference: Common go commands

```bash
go run main.go
go build -o app .
go test ./... -race -cover
go vet ./...
go fmt ./...
go mod tidy
go doc fmt.Println
go list -m all
GOOS=linux GOARCH=amd64 go build -o app-linux .   # cross-compile
```
