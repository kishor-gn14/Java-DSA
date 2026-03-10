# Longest Consecutive Sequence — HashSet O(n) Streak Detection

The problem: given an unsorted array, find the length of the longest sequence of consecutive integers. For `[100, 4, 200, 1, 3, 2]` the answer is `4` because `1, 2, 3, 4` form the longest consecutive run.

The constraint that makes it interesting: must run in **O(n)**. Sorting would work but costs O(n log n). The HashSet approach hits O(n) by detecting exactly where each streak begins and never retracing the same numbers twice.

---

## The Core Insight

A number `n` is the **start of a streak** if and only if `n - 1` does not exist in the array. If `n - 1` exists, then `n` is in the middle of someone else's streak — let that streak handle it.

Once you find a streak start, extend it forward (`n+1`, `n+2`, ...) for as long as consecutive numbers exist in the Set. Count the length. Move on.

Every number is visited **at most twice total** — once to check if it's a streak start, and once while extending a streak. That's where O(n) comes from.

---

## Walkthrough

```
nums = [100, 4, 200, 1, 3, 2]
```

**Build the HashSet:**

```
set = {100, 4, 200, 1, 3, 2}
```

**Check each number:**

| `num` | Is `num-1` in set? | Action |
|-------|-------------------|--------|
| 100   | 99 → ❌ No         | Streak start! Extend: 100, 101? No. Length = 1 |
| 4     | 3 → ✅ Yes         | Skip — part of another streak |
| 200   | 199 → ❌ No        | Streak start! Extend: 200, 201? No. Length = 1 |
| 1     | 0 → ❌ No          | Streak start! Extend: 1→2→3→4→5? No. Length = 4 |
| 3     | 2 → ✅ Yes         | Skip |
| 2     | 1 → ✅ Yes         | Skip |

```
Longest = 4  ✅  (the sequence 1, 2, 3, 4)
```

Only three numbers triggered streak detection (`100`, `200`, `1`). The rest were skipped instantly.

---

## Java Code

```java
import java.util.*;

public int longestConsecutive(int[] nums) {
    // Step 1: load everything into a HashSet for O(1) lookup
    HashSet<Integer> set = new HashSet<>();
    for (int num : nums) {
        set.add(num);
    }

    int longest = 0;

    // Step 2: find streak starts and measure each streak
    for (int num : set) {                          // iterate set, not array
        if (!set.contains(num - 1)) {              // num is a streak start
            int current = num;
            int length  = 1;

            while (set.contains(current + 1)) {    // extend streak forward
                current++;
                length++;
            }

            longest = Math.max(longest, length);
        }
    }

    return longest;
}
```

One detail worth noting: the outer loop iterates over `set`, not the original `nums` array. If `nums` has duplicates (e.g. `[1, 1, 2, 2]`), iterating the set automatically skips them — each unique value is considered exactly once.

---

## Dry Run

```
nums = [100, 4, 200, 1, 3, 2]
set  = {1, 2, 3, 4, 100, 200}
```

**num = 1:**
```
set.contains(0)? No → streak start
current=1, length=1
  contains(2)? Yes → current=2, length=2
  contains(3)? Yes → current=3, length=3
  contains(4)? Yes → current=4, length=4
  contains(5)? No  → stop
longest = max(0, 4) = 4
```

**num = 2:**
```
set.contains(1)? Yes → skip
```

**num = 3:**
```
set.contains(2)? Yes → skip
```

**num = 4:**
```
set.contains(3)? Yes → skip
```

**num = 100:**
```
set.contains(99)? No → streak start
current=100, length=1
  contains(101)? No → stop
longest = max(4, 1) = 4
```

**num = 200:**
```
set.contains(199)? No → streak start
current=200, length=1
  contains(201)? No → stop
longest = max(4, 1) = 4
```

```
return 4  ✅
```

The `while` loop only ran **3 times** across the entire execution — for `2`, `3`, and `4` while extending the streak starting at `1`. Every other number was either skipped immediately or stopped after one failed `contains` check.

---

## Why It's O(n) — The Key Argument

The concern is the nested `while` loop. It looks like it could make this O(n²). It doesn't, and here's the precise reason:

The `while` loop only runs for streak-start numbers. Non-starts are skipped in O(1) by the `num - 1` check. And across all streak starts, the `while` loop's **total iterations** equals the total number of elements consumed across all streaks — which is **at most `n`**.

Think of it this way: each number in the set can be "consumed" by the `while` loop at most once — when the streak it belongs to is being measured. After that, it's never touched again. So the `while` loop's iterations summed across all streak starts ≤ `n`. Combined with the O(n) outer loop, total work is **O(n)**.

---

## Common Mistakes

**Iterating the array instead of the set:**

```java
// ⚠️ Works but wastes time if nums has duplicates
for (int num : nums) {
    if (!set.contains(num - 1)) { ... }
}
```

If `nums = [1, 1, 1, 1, 2, 3]`, you'd start streak detection from `1` four times. Still correct, still O(n) amortised, but unnecessarily repetitive. Iterating the set is cleaner.

**Using a sorted structure instead of HashSet:**

```java
// ❌ TreeSet gives O(log n) contains — breaks the O(n) guarantee
TreeSet<Integer> set = new TreeSet<>();
```

`TreeSet` is sorted but every `contains()` costs O(log n). You need `HashSet` for O(1) lookup.

**Forgetting to handle empty input:**

```java
if (nums.length == 0) return 0;  // good habit, though HashSet handles it gracefully
```

---

## Three Approaches Compared

**1. Sorting — O(n log n):**

```java
Arrays.sort(nums);
// walk sorted array, count consecutive runs, reset on gaps
```

Simple to reason about. Fails the O(n) constraint. Fine if the constraint is relaxed.

**2. HashMap with frequency count — O(n):**

```java
// Same idea but use HashMap<Integer,Integer> for frequency
// Extra overhead, no benefit over HashSet for this problem
```

Overcomplicated. `HashSet` is sufficient since you only need existence, not count.

**3. HashSet with streak detection — O(n):**

```java
// The approach above — optimal
```

Clean, correct, hits the constraint exactly.

---

## Handling Duplicates

```
nums = [1, 2, 2, 3, 3, 3, 4]
```

The `HashSet` deduplicates automatically:

```
set = {1, 2, 3, 4}
```

Streak starting at `1`: `1 → 2 → 3 → 4`, length = `4`. Duplicates don't inflate or distort the count — they simply collapse in the Set and are never double-counted.

---

## Complexity

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n) | Each element added once; each element consumed by `while` loop at most once |
| **Space** | O(n) | HashSet holds all unique elements |

---

## The Mental Model

Imagine the numbers scattered across a number line. You want to find the longest unbroken chain. Instead of checking every number for every possible chain — which is O(n²) — you **only start measuring a chain from its leftmost end**. You identify leftmost ends by checking whether the number to the left is absent. If the left neighbour is missing, you're at the start of a new chain, so you measure forward. If the left neighbour exists, someone else started this chain — ignore it. Every chain is measured exactly once from its start, and every number participates in exactly one chain. Total work: **O(n)**.