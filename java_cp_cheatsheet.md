# Java for Competitive Programming — Operations Cheat Sheet

---

## 0. Fast I/O

```java
import java.io.*;
import java.util.*;

BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
StringTokenizer st = new StringTokenizer(br.readLine());
int n = Integer.parseInt(st.nextToken());
int[] arr = new int[n];
st = new StringTokenizer(br.readLine());
for (int i = 0; i < n; i++) arr[i] = Integer.parseInt(st.nextToken());

StringBuilder sb = new StringBuilder(); // buffer output, print once at end
sb.append(ans).append("\n");
System.out.print(sb);
```

---

## 1. Arrays

```java
int[] a = new int[n];                       // default 0
int[] a = {1, 2, 3};
int[][] grid = new int[r][c];               // 2D
int[][] grid = new int[r][];                // jagged

Arrays.fill(a, -1);                         // fill whole array
Arrays.fill(a, from, to, 0);                // fill range [from, to)

Arrays.sort(a);                             // primitive: ascending only
Arrays.sort(a, from, to);                   // sort subrange
Integer[] b = {5, 3, 1};
Arrays.sort(b, Collections.reverseOrder()); // descending (needs wrapper type)
Arrays.sort(b, (x, y) -> y - x);            // custom comparator (wrapper only)

int idx = Arrays.binarySearch(a, key);      // array MUST be sorted; returns -(insertionPoint)-1 if absent

int[] c = Arrays.copyOf(a, newLen);         // truncate/extend
int[] d = Arrays.copyOfRange(a, from, to);  // [from, to)

boolean eq = Arrays.equals(a, b2);          // element-wise, 1D
boolean deq = Arrays.deepEquals(grid1, grid2); // for 2D/nested arrays

System.out.println(Arrays.toString(a));     // [1, 2, 3]
System.out.println(Arrays.deepToString(grid)); // for 2D

List<Integer> list = Arrays.asList(1, 2, 3);   // FIXED-SIZE view (no add/remove!)
List<Integer> mutable = new ArrayList<>(Arrays.asList(1, 2, 3)); // real mutable list

int max = Arrays.stream(a).max().getAsInt();
int sum = Arrays.stream(a).sum();
```

**2D array sort by column** (e.g. sort intervals by start):
```java
int[][] intervals = {{1,3},{2,6},{8,10}};
Arrays.sort(intervals, (x, y) -> x[0] - y[0]);
```

---

## 2. ArrayList

```java
List<Integer> list = new ArrayList<>();
list.add(5);
list.add(0, 10);            // insert at index
list.get(i);
list.set(i, val);           // overwrite
list.remove(i);             // by INDEX (int) — ambiguous with remove(Object) for Integer!
list.remove(Integer.valueOf(5)); // by VALUE
list.size();
list.isEmpty();
list.contains(5);
list.indexOf(5);            // -1 if absent
list.clear();

Collections.sort(list);                         // ascending
Collections.sort(list, Collections.reverseOrder());
list.sort((x, y) -> x - y);                      // instance method, same effect
Collections.reverse(list);
Collections.max(list); Collections.min(list);
Collections.frequency(list, val);
Collections.swap(list, i, j);
Collections.shuffle(list);

List<Integer> sub = list.subList(from, to);      // VIEW, not a copy — mutating it mutates original
list.removeIf(x -> x % 2 == 0);                  // conditional removal
Integer[] arr = list.toArray(new Integer[0]);

List<List<Integer>> adj = new ArrayList<>();     // adjacency list pattern
for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
adj.get(u).add(v);

// Iterating
for (int x : list) { }
for (Iterator<Integer> it = list.iterator(); it.hasNext(); ) {
    int x = it.next();
    if (x == target) it.remove();   // safe removal during iteration
}
```

---

## 3. String / StringBuilder

Strings are **immutable** — every "modification" creates a new object. Use `StringBuilder` in loops.

```java
String s = "hello world";
s.length();
s.charAt(i);
s.substring(from);          // from index to end
s.substring(from, to);      // [from, to)
s.indexOf('o');              s.indexOf('o', fromIdx);
s.lastIndexOf('o');
s.contains("wor");
s.startsWith("he"); s.endsWith("ld");
s.equals(other);             // NEVER use == for content comparison
s.equalsIgnoreCase(other);
s.compareTo(other);          // lexicographic, <0/0/>0
s.toUpperCase(); s.toLowerCase();
s.trim(); s.strip();
s.replace('l', 'L');         // char replace
s.replace("lo", "LO");       // literal string replace
s.split(" ");                // regex split -> String[]
s.split(",", -1);            // keep trailing empty strings
char[] chars = s.toCharArray();
String joined = String.join(",", list);      // list/array of strings -> "a,b,c"
String rep = String.format("%d-%s", 5, "x"); // formatting
boolean isDigit = Character.isDigit(c);
boolean isLetter = Character.isLetter(c);
int digit = c - '0';                          // char to int

StringBuilder sb = new StringBuilder();
sb.append("abc").append(123);
sb.insert(0, "x");
sb.deleteCharAt(i);
sb.delete(from, to);
sb.setCharAt(i, 'z');
sb.reverse();
sb.charAt(i);
sb.length();
sb.toString();
sb.replace(from, to, "new"); // replace range
```

---

## 4. HashMap / TreeMap / LinkedHashMap

```java
Map<String, Integer> map = new HashMap<>();       // no order guarantee, O(1) avg
Map<String, Integer> lmap = new LinkedHashMap<>(); // insertion order, O(1) avg
TreeMap<String, Integer> tmap = new TreeMap<>();   // sorted by key, O(log n)

map.put("a", 1);
map.get("a");                    // null if absent
map.getOrDefault("a", 0);
map.containsKey("a");
map.containsValue(1);
map.remove("a");
map.size(); map.isEmpty();

map.put("a", map.getOrDefault("a", 0) + 1);        // classic frequency-count pattern
map.merge("a", 1, Integer::sum);                    // same thing, cleaner
map.putIfAbsent("a", 0);
map.computeIfAbsent(key, k -> new ArrayList<>()).add(val); // map<K, List<V>> pattern
map.compute("a", (k, v) -> (v == null) ? 1 : v + 1);

for (String k : map.keySet()) { }
for (int v : map.values()) { }
for (Map.Entry<String, Integer> e : map.entrySet()) {
    e.getKey(); e.getValue();
}
map.forEach((k, v) -> System.out.println(k + "=" + v));

// TreeMap-only (sorted map) ops — all O(log n)
tmap.firstKey(); tmap.lastKey();
tmap.higherKey(k);   // strictly greater
tmap.lowerKey(k);    // strictly less
tmap.ceilingKey(k);  // >= k
tmap.floorKey(k);    // <= k
tmap.headMap(k);     // keys < k, view
tmap.tailMap(k);     // keys >= k, view
tmap.pollFirstEntry(); tmap.pollLastEntry();
tmap.firstEntry(); tmap.lastEntry();
```

---

## 5. HashSet / TreeSet / LinkedHashSet

```java
Set<Integer> set = new HashSet<>();          // no order, O(1) avg
Set<Integer> lset = new LinkedHashSet<>();   // insertion order, O(1) avg
TreeSet<Integer> tset = new TreeSet<>();     // sorted, O(log n)

set.add(5);
set.remove(5);
set.contains(5);
set.size(); set.isEmpty();
set.addAll(list);
set.retainAll(otherCollection);              // intersection, in place
set.removeAll(otherCollection);              // difference, in place

for (int x : set) { }

// TreeSet-only ops — all O(log n)
tset.first(); tset.last();
tset.higher(x);   // strictly greater than x
tset.lower(x);    // strictly less than x
tset.ceiling(x);  // >= x
tset.floor(x);    // <= x
tset.pollFirst(); tset.pollLast();  // retrieve + remove
tset.headSet(x);        // elements < x, view
tset.headSet(x, true);  // elements <= x
tset.tailSet(x);        // elements >= x, view
tset.subSet(from, to);  // [from, to)

TreeSet<int[]> ts = new TreeSet<>((x, y) -> x[0] != y[0] ? x[0]-y[0] : x[1]-y[1]); // custom comparator
```

---

## 6. PriorityQueue (Heap)

```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();                 // min-heap by default
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> x[0] - y[0]);   // custom comparator

pq.offer(x);   // or add(x)
pq.poll();     // removes & returns smallest (or null if empty)
pq.peek();     // views smallest without removing
pq.size(); pq.isEmpty();
pq.remove(x);  // O(n) — removes specific element, avoid in hot loops
```

---

## 7. Stack / Queue / Deque

Prefer **`ArrayDeque`** over the legacy `Stack` class (synchronized, slower) and over `LinkedList` (more overhead) for both stack and queue use.

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(x);      // add to front
stack.pop();         // remove from front
stack.peek();        // view front

Deque<Integer> queue = new ArrayDeque<>();
queue.offer(x);       // add to back
queue.poll();          // remove from front
queue.peek();          // view front

Deque<Integer> deque = new ArrayDeque<>();  // double-ended, for sliding window etc.
deque.addFirst(x); deque.addLast(x);
deque.pollFirst(); deque.pollLast();
deque.peekFirst(); deque.peekLast();
deque.isEmpty(); deque.size();
```

---

## 8. Comparator patterns

```java
// Sort list of int[] pairs by second element, then first
list.sort((a, b) -> a[1] != b[1] ? a[1] - b[1] : a[0] - b[0]);

// Sort objects by field
List<Person> people = ...;
people.sort(Comparator.comparingInt(p -> p.age));
people.sort(Comparator.comparing((Person p) -> p.name).thenComparingInt(p -> p.age));
people.sort(Comparator.comparingInt((Person p) -> p.age).reversed());

// CAUTION: for large ints, avoid `a - b` if overflow possible — use Integer.compare(a, b)
list.sort((a, b) -> Integer.compare(a, b));
```

---

## 9. "Pair" workarounds (Java has no built-in Pair/Tuple)

```java
int[] pair = {x, y};                                   // simplest, works in TreeSet/PQ with comparator
Map.Entry<Integer, Integer> p = Map.entry(x, y);        // immutable entry (Java 9+), p.getKey()/getValue()
AbstractMap.SimpleEntry<Integer, Integer> p2 =
        new AbstractMap.SimpleEntry<>(x, y);            // mutable version

// or define your own small class when you need 3+ fields / clarity
class Edge {
    int to, weight;
    Edge(int to, int weight) { this.to = to; this.weight = weight; }
}
```

---

## 10. Collections utility quick reference

```java
Collections.sort(list);
Collections.reverse(list);
Collections.max(list); Collections.min(list);
Collections.max(list, comparator);
Collections.frequency(list, x);
Collections.swap(list, i, j);
Collections.fill(list, val);
Collections.nCopies(n, val);           // immutable list of n copies
Collections.unmodifiableList(list);    // read-only view
Collections.emptyList();
Collections.binarySearch(list, key);   // list must be sorted
```

---

### Quick complexity notes
| Structure | Add/Remove | Search/Contains | Ordered? |
|---|---|---|---|
| ArrayList | O(1) amortized end, O(n) middle | O(n) | insertion |
| LinkedList | O(1) ends | O(n) | insertion |
| HashMap/HashSet | O(1) avg | O(1) avg | none |
| LinkedHashMap/Set | O(1) avg | O(1) avg | insertion |
| TreeMap/TreeSet | O(log n) | O(log n) | sorted |
| PriorityQueue | O(log n) | O(n) | heap order only |
| ArrayDeque | O(1) both ends | O(n) | insertion |
