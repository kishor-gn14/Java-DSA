# Contains Duplicate — HashSet Approach

## The Logic

Walk the array once. For each number:

- If it's already in the Set → duplicate found, return `true`
- If it's not in the Set → add it and continue

If you finish the entire array without a hit → return `false`

---

## Walkthrough

```
nums = [1, 3, 4, 3, 2]
```

| Step | `num` | Set before check | Duplicate? | Action |
|------|-------|------------------|------------|--------|
| 1    | 1     | `{}`             | ❌ No       | Add → `{1}` |
| 2    | 3     | `{1}`            | ❌ No       | Add → `{1, 3}` |
| 3    | 4     | `{1, 3}`         | ❌ No       | Add → `{1, 3, 4}` |
| 4    | 3     | `{1, 3, 4}`      | ✅ Yes!     | `return true` |

Stopped early at step 4. Never processed `2`.

---

## Java Code

```java
import java.util.HashSet;

public boolean containsDuplicate(int[] nums) {
    HashSet<Integer> seen = new HashSet<>();

    for (int num : nums) {
        if (!seen.add(num)) {   // .add() returns false if already present
            return true;
        }
    }

    return false;
}
```

The neat trick here is `seen.add(num)` already returns a boolean — `true` if the element was added (new), `false` if it was already there (duplicate). So you don't need a separate `contains()` check.

A more explicit version that makes the intent clearer:

```java
public boolean containsDuplicate(int[] nums) {
    HashSet<Integer> seen = new HashSet<>();

    for (int num : nums) {
        if (seen.contains(num)) {
            return true;            // found it before → duplicate
        }
        seen.add(num);
    }

    return false;
}
```

Both are correct. The first is more concise; the second is easier to read at a glance.

---

## Why HashSet and not an Array or List?

| Structure   | `contains()` cost                  | Why it matters            |
|-------------|------------------------------------|---------------------------|
| `ArrayList` | O(n) — scans every element         | Gives you O(n²) overall   |
| `HashSet`   | O(1) — direct hash lookup          | Keeps you at O(n) overall |

HashSet internally uses a hash table — it computes a hash of the number and jumps directly to that bucket. No scanning, no sorting needed.

---

## Complexity

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n) | One pass; each lookup and insert is O(1) |
| **Space** | O(n) | Worst case the Set holds all `n` elements (no duplicates) |

---

## Three Ways to Solve It (and why HashSet wins)

**1. Brute force** — check every pair:

```java
// O(n²) time, O(1) space — too slow for large inputs
for (int i = 0; i < nums.length; i++)
    for (int j = i + 1; j < nums.length; j++)
        if (nums[i] == nums[j]) return true;
```

**2. Sort first** — duplicates become neighbours:

```java
// O(n log n) time, O(1) space — faster but modifies the input
Arrays.sort(nums);
for (int i = 1; i < nums.length; i++)
    if (nums[i] == nums[i-1]) return true;
```

**3. HashSet** — one pass, O(1) lookup:

```java
// O(n) time, O(n) space — fastest, doesn't modify input
HashSet<Integer> seen = new HashSet<>();
for (int num : nums)
    if (!seen.add(num)) return true;
```

> In interviews, mention all three and their tradeoffs. Then code the HashSet version — it's the clearest demonstration that you understand the pattern.

---

## The Mental Model

Think of the HashSet as a **guest list at a door**. Each number walks up. The bouncer checks the list — if the name's already there, it's a duplicate, party's over. If not, the name gets added and the next number walks up. One pass down the line, instant checks throughout.