# Subarray Sum Equals K — Prefix Sum + HashMap of Counts

The problem: given an array of integers and a target `k`, return the count of subarrays whose elements sum to exactly `k`.

```
nums = [1, 2, 3],  k = 3
Answer: 2  → subarrays [1,2] and [3]
```

The brute force checks every possible subarray — O(n²). The prefix sum + HashMap approach counts all valid subarrays in a single pass — O(n).

---

## The Core Insight

Define the **prefix sum** at index `i` as the sum of all elements from index `0` to `i`:

```
prefixSum[i] = nums[0] + nums[1] + ... + nums[i]
```

The sum of any subarray from index `j` to `i` can be expressed as:

```
sum(j → i) = prefixSum[i] - prefixSum[j-1]
```

You want this to equal `k`:

```
prefixSum[i] - prefixSum[j-1] = k
```

Rearranging:

```
prefixSum[j-1] = prefixSum[i] - k
```

So as you walk the array building the running prefix sum, you ask at each step: *"How many times have I seen the value `prefixSum - k` before?"* Each occurrence represents a valid subarray ending exactly here.

A HashMap tracks how many times each prefix sum has occurred so far. One lookup per index gives the count instantly.

---

## Walkthrough

```
nums = [1, 2, 3],  k = 3
```

Always initialise the map with `{0 → 1}` — this represents the empty prefix (sum of zero elements = 0), which handles subarrays that start from index 0.

| `i` | `num` | `prefixSum` | `prefixSum - k` | `map.get(ps-k)` | `count` | map after |
|-----|-------|-------------|-----------------|-----------------|---------|-----------|
| —   | —     | 0           | —               | —               | 0       | `{0:1}` |
| 0   | 1     | 1           | 1 - 3 = -2      | 0               | 0       | `{0:1, 1:1}` |
| 1   | 2     | 3           | 3 - 3 = 0       | 1               | 1       | `{0:1, 1:1, 3:1}` |
| 2   | 3     | 6           | 6 - 3 = 3       | 1               | 2       | `{0:1, 1:1, 3:2, 6:1}` |

```
Answer: 2  ✅  ([1,2] and [3])
```

- **At index 1:** `prefixSum = 3`, looking for `3 - 3 = 0`. The map has seen `0` once — meaning there's one earlier point where prefix sum was `0` (the start), so the subarray from index 0 to 1 sums to `k`. That's `[1, 2]`.
- **At index 2:** `prefixSum = 6`, looking for `6 - 3 = 3`. The map has seen `3` once — the prefix sum at index 1. So the subarray from index 2 to 2 sums to `k`. That's `[3]`.

---

## Java Code

```java
import java.util.*;

public int subarraySum(int[] nums, int k) {
    HashMap<Integer, Integer> prefixCounts = new HashMap<>();
    prefixCounts.put(0, 1);   // empty prefix — crucial base case

    int prefixSum = 0;
    int count     = 0;

    for (int num : nums) {
        prefixSum += num;                                      // extend prefix

        int complement = prefixSum - k;
        count += prefixCounts.getOrDefault(complement, 0);    // how many valid starts exist

        prefixCounts.put(prefixSum,
            prefixCounts.getOrDefault(prefixSum, 0) + 1);     // record this prefix sum
    }

    return count;
}
```

The order inside the loop matters:
1. Update `prefixSum` first
2. Look up `prefixSum - k` in the map (count valid subarrays ending here)
3. Then store `prefixSum` in the map

If you stored first and looked up second, a prefix sum could match itself — falsely counting a subarray of length zero.

---

## Dry Run

```
nums = [1, 2, 3, -3, 3],  k = 3
prefixCounts = {0:1},  prefixSum = 0,  count = 0
```

**i=0, num=1:**
```
prefixSum = 0 + 1 = 1
complement = 1 - 3 = -2  →  map has no -2  →  count += 0  →  count = 0
store 1  →  map = {0:1, 1:1}
```

**i=1, num=2:**
```
prefixSum = 1 + 2 = 3
complement = 3 - 3 = 0  →  map has 0 once  →  count += 1  →  count = 1  ([1,2])
store 3  →  map = {0:1, 1:1, 3:1}
```

**i=2, num=3:**
```
prefixSum = 3 + 3 = 6
complement = 6 - 3 = 3  →  map has 3 once  →  count += 1  →  count = 2  ([3])
store 6  →  map = {0:1, 1:1, 3:1, 6:1}
```

**i=3, num=-3:**
```
prefixSum = 6 + (-3) = 3
complement = 3 - 3 = 0  →  map has 0 once  →  count += 1  →  count = 3  ([1,2,3,-3] = 3 ✅)
store 3  →  map = {0:1, 1:1, 3:2, 6:1}
```

**i=4, num=3:**
```
prefixSum = 3 + 3 = 6
complement = 6 - 3 = 3  →  map has 3 twice  →  count += 2  →  count = 5
```

Those two hits represent:
- Subarray from index 2 to 4: `3 + (-3) + 3 = 3` ✅
- Subarray from index 4 to 4: `3` ✅

```
store 6  →  map = {0:1, 1:1, 3:2, 6:2}
return 5  ✅
```

This shows the pattern handles **negative numbers** and **multiple valid subarrays ending at the same index** correctly — something sliding window cannot do.

---

## Why the Base Case `{0 → 1}` Is Essential

Without it, subarrays starting from index 0 are missed.

```
nums = [3, 1, 2],  k = 3
```

At index 0: `prefixSum = 3`, looking for `3 - 3 = 0`. If the map doesn't contain `0`, this returns 0 — missing the subarray `[3]`.

The entry `{0 → 1}` says: *"Before seeing any element, there is exactly one prefix whose sum is zero — the empty prefix."* A subarray from index 0 to `i` summing to `k` means `prefixSum[i] - 0 = k`. The lookup for `prefixSum - k = 0` finds the base case and counts it correctly.

Think of it as the **ghost starting point** every valid from-zero subarray points back to.

---

## Why Sliding Window Doesn't Work Here

Sliding window is the natural O(n) tool for subarray problems — but it requires the array to have a key property: **adding elements always increases the sum, removing always decreases it**. That holds for non-negative numbers only.

```
nums = [1, -1, 1],  k = 1
```

With negatives, shrinking the window can increase the sum, and expanding can decrease it. The window has no reliable direction to move. The prefix sum + HashMap approach has no such restriction — it handles negatives, zeros, and positives equally.

| | Sliding Window | Prefix Sum + HashMap |
|---|---|---|
| **Non-negative arrays** | ✅ O(n) | ✅ O(n) |
| **Arrays with negatives** | ❌ Breaks | ✅ O(n) |
| **Count of subarrays** | ✅ | ✅ |
| **Space** | O(1) | O(n) |

---

## Visualising What the Map Stores

At any point in the loop, `prefixCounts` is a **histogram of prefix sums seen so far**. Each entry says: *"this total running sum has occurred this many times at earlier indices."*

```
nums = [1, 2, 3, 2, 1],  k = 3
```

After processing all elements:

```
prefixCounts = {0:1, 1:1, 3:2, 6:2, 8:1, 9:1}
```

The value `3` appears twice because `prefixSum` hit `3` at index 1 and again at index 3. That means two earlier "starting points" exist for subarrays ending later — and both get counted when a future lookup finds `prefixSum - k = 3`.

---

## Complexity

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n) | Single pass; each HashMap operation is O(1) |
| **Space** | O(n) | Map holds at most `n+1` distinct prefix sums |

---

## The Mental Model

Imagine walking the array holding a running total on a counter. At each step you ask: *"Is there a point I passed earlier where my counter read exactly `current - k`?"* If yes, the stretch of array between that earlier point and right now sums to exactly `k`. The HashMap is simply a memory of every counter reading you've had, and how many times you've had it. One lookup per step, one answer at the end.