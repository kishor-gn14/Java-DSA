# Two Sum II — Left/Right Converge

The problem: given a **1-indexed sorted array** and a target, return the indices of the two numbers that add up to the target. Exactly one solution is guaranteed.

```
numbers = [2, 7, 11, 15],  target = 9
Answer:  [1, 2]            (1-indexed)
```

The array being sorted is the entire key. It lets you make a guaranteed decision at every step — something an unsorted array cannot offer.

---

## Why Sorting Changes Everything

In the original Two Sum (unsorted), you need a HashMap because you have no structural information — any element could pair with any other. The HashMap gives you O(1) lookup to compensate for the lack of order.

Here the array is already sorted. Order gives you **directionality**:

- Sum too small → you need a larger number → move left pointer right
- Sum too large → you need a smaller number → move right pointer left
- Sum exact → done

Every comparison eliminates one candidate with certainty. No HashMap needed. O(1) space.

---

## The Core Insight

Place one pointer at the leftmost element (smallest) and one at the rightmost (largest). Check their sum:

```
sum = numbers[left] + numbers[right]
```

- If `sum < target` — the right element is as large as it can be for this pair. No right element can save the left element. Move left forward to a larger value.
- If `sum > target` — the left element is as small as it can be for this pair. No left element can save the right element. Move right backward to a smaller value.
- If `sum == target` — return both indices.

The pointers squeeze inward from both ends until they meet. Since exactly one solution is guaranteed, they will always find it before crossing.

---

## Walkthrough

```
numbers = [2, 7, 11, 15],  target = 9

left=0, right=3
↓                ↓
[2,  7,  11,  15]
```

| Step | `left` | `right` | `numbers[left]` | `numbers[right]` | `sum` | Decision |
|------|--------|---------|-----------------|------------------|-------|----------|
| 1    | 0      | 3       | 2               | 15               | 17    | 17 > 9 → move right left |
| 2    | 0      | 2       | 2               | 11               | 13    | 13 > 9 → move right left |
| 3    | 0      | 1       | 2               | 7                | 9     | 9 == 9 → ✅ return `[1, 2]` |

Found in 3 steps. No extra space used.

---

## Java Code

```java
public int[] twoSum(int[] numbers, int target) {
    int left  = 0;
    int right = numbers.length - 1;

    while (left < right) {
        int sum = numbers[left] + numbers[right];

        if (sum == target) {
            return new int[]{left + 1, right + 1};  // convert to 1-indexed
        } else if (sum < target) {
            left++;    // need larger sum → advance left
        } else {
            right--;   // need smaller sum → retreat right
        }
    }

    return new int[]{};  // guaranteed solution exists, never reached
}
```

The `+ 1` on both indices converts from 0-indexed (Java arrays) to 1-indexed (problem requirement). Easy to forget under pressure — worth noting explicitly.

---

## Dry Run

**Example 1:**
```
numbers = [2, 3, 4],  target = 6

left=0, right=2
Step 1: sum = 2 + 4 = 6 == target
return [0+1, 2+1] = [1, 3]  ✅
```

**Example 2:**
```
numbers = [-1, 0, 1, 2],  target = 0
```

| Step | `left` | `right` | `sum`    | Decision |
|------|--------|---------|----------|----------|
| 1    | 0      | 3       | -1+2 = 1 | 1 > 0 → right-- |
| 2    | 0      | 2       | -1+1 = 0 | 0 == 0 → return `[1, 3]` ✅ |

**Example 3:**
```
numbers = [1, 2, 3, 4, 5],  target = 9
```

| Step | `left` | `right` | `sum`   | Decision |
|------|--------|---------|---------|----------|
| 1    | 0      | 4       | 1+5 = 6 | 6 < 9 → left++ |
| 2    | 1      | 4       | 2+5 = 7 | 7 < 9 → left++ |
| 3    | 2      | 4       | 3+5 = 8 | 8 < 9 → left++ |
| 4    | 3      | 4       | 4+5 = 9 | 9 == 9 → return `[4, 5]` ✅ |

---

## The Correctness Argument

Why is it safe to discard a pointer's current position permanently?

**When `sum < target` and you move `left++`:**
The current `left` element paired with every possible right element has been implicitly tested. The current `right` is the largest available. If `numbers[left] + numbers[right] < target`, then `numbers[left] + numbers[anything smaller than right]` is also `< target`. The current `left` value cannot form a valid pair with any element. Discard it.

**When `sum > target` and you move `right--`:**
Symmetric argument. The current `right` paired with the current `left` — the smallest available — still overshoots. `numbers[left] + numbers[right] > target` means `numbers[anything larger than left] + numbers[right]` also `> target`. The current `right` value cannot form a valid pair. Discard it.

Every move permanently eliminates one candidate with mathematical certainty. No valid pair is ever skipped.

---

## Two Sum I vs Two Sum II — Side by Side

| | Two Sum (unsorted) | Two Sum II (sorted) |
|---|---|---|
| **Array** | Unsorted | Sorted |
| **Approach** | HashMap complement lookup | Two pointers converge |
| **Time** | O(n) | O(n) |
| **Space** | O(n) — HashMap | O(1) — two pointers only |
| **Indexing** | 0-indexed | 1-indexed |
| **Key idea** | Store seen values for instant lookup | Use order to eliminate candidates |

Sorting buys you O(1) space at no time cost. If you're ever given a sorted array and asked for pairs, two pointers should be your first thought.

---

## What Happens With Duplicates

```
numbers = [2, 2, 3],  target = 4
```

| Step | `left` | `right` | `sum`   | Decision |
|------|--------|---------|---------|----------|
| 1    | 0      | 2       | 2+3 = 5 | 5 > 4 → right-- |
| 2    | 0      | 1       | 2+2 = 4 | 4 == 4 → return `[1, 2]` ✅ |

Duplicates are handled naturally — the pointers treat each index as independent regardless of value. No special casing needed.

---

## The `left < right` Guard

The loop condition `left < right` ensures pointers never cross and never point to the same element. Since the problem guarantees distinct indices (you cannot use the same element twice), this guard enforces that constraint automatically. When `left == right`, both pointers are on the same element — an invalid state — so the loop exits.

In practice, the guaranteed solution means the loop always returns before `left` and `right` meet.

---

## Complexity

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n) | Pointers together traverse at most `n` elements total |
| **Space** | O(1) | Two integer pointers, nothing else |

---

## The Mental Model

Imagine the sorted array laid out on a number line. You place one hand at the far left (smallest) and one at the far right (largest). You check if your two hands sum to the target. If the sum is too small, your left hand is not doing enough — slide it right to a larger number. If the sum is too large, your right hand is too aggressive — slide it left to a smaller number. Your hands squeeze inward, never needing to backtrack, until they land on the exact pair. The sorted order guarantees every slide is a step toward the answer, never away from it.