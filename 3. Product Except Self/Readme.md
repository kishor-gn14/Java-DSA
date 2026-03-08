# Product of Array Except Self — Prefix × Suffix Approach

## The Core Insight

For any index `i`, the answer is:

```
result[i] = (product of everything LEFT of i)
           × (product of everything RIGHT of i)
```

Those two halves are called the **prefix product** and the **suffix product**.

---

## Walkthrough

```
nums = [1, 2, 3, 4]
```

**Step 1 — Build prefix products (everything to the left):**

| Index  | 0 | 1 | 2 | 3 |
|--------|---|---|---|---|
| nums   | 1 | 2 | 3 | 4 |
| prefix | 1 | 1 | 2 | 6 |

- Index 0: nothing to the left → `1` (identity)
- Index 1: just `nums[0]` = `1`
- Index 2: `nums[0] × nums[1]` = `1 × 2` = `2`
- Index 3: `nums[0] × nums[1] × nums[2]` = `1 × 2 × 3` = `6`

**Step 2 — Multiply suffix products from the right (everything to the right):**

| Index              | 0  | 1  | 2 | 3 |
|--------------------|----|----|---|---|
| prefix             | 1  | 1  | 2 | 6 |
| suffix (running)   | 24 | 12 | 4 | 1 |
| result = prefix × suffix | **24** | **12** | **8** | **6** |

- Index 3: nothing to the right → suffix = `1`, result = `6 × 1` = `6`
- Index 2: suffix = `nums[3]` = `4`, result = `2 × 4` = `8`
- Index 1: suffix = `nums[3] × nums[2]` = `12`, result = `1 × 12` = `12`
- Index 0: suffix = `nums[3] × nums[2] × nums[1]` = `24`, result = `1 × 24` = `24`

```
Output: [24, 12, 8, 6]  ✅
```

---

## Java Code

Two-pass version (clearest to understand):

```java
public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];

    // Pass 1: fill result with prefix products
    result[0] = 1;
    for (int i = 1; i < n; i++) {
        result[i] = result[i - 1] * nums[i - 1];
    }

    // Pass 2: multiply suffix products from the right
    int suffix = 1;
    for (int i = n - 1; i >= 0; i--) {
        result[i] *= suffix;   // combine prefix already stored with running suffix
        suffix    *= nums[i];  // extend suffix rightward for next iteration
    }

    return result;
}
```

The key elegance: `result[]` stores prefix products after pass 1. Pass 2 then multiplies each position by its suffix in-place — no second array needed.

---

## Dry Run of the Code

```
nums = [1, 2, 3, 4]
```

**After Pass 1** (prefix products in result):

```
result = [1, 1, 2, 6]
```

**Pass 2** (suffix = 1, walking right → left):

| `i` | `result[i]` before | `suffix` | `result[i]` after  | `suffix` after |
|-----|--------------------|----------|--------------------|----------------|
| 3   | 6                  | 1        | 6 × 1 = **6**      | 1 × 4 = 4      |
| 2   | 2                  | 4        | 2 × 4 = **8**      | 4 × 3 = 12     |
| 1   | 1                  | 12       | 1 × 12 = **12**    | 12 × 2 = 24    |
| 0   | 1                  | 24       | 1 × 24 = **24**    | 24 × 1 = 24    |

```
result = [24, 12, 8, 6]  ✅
```

---

## Why No Division?

Division seems tempting:

```
totalProduct / nums[i]
```

It fails in two ways:

- **Zeros** — division by zero crashes instantly. If `nums[i] = 0`, you can't divide.
- **The constraint** — the problem explicitly forbids it, testing whether you know this pattern.

The prefix × suffix approach handles zeros naturally — if `nums[i] = 0`, its prefix and suffix products simply don't include it, so the result at every other index is unaffected. The index at `i` itself gets its neighbours' products, which is correct.

---

## Edge Case — Array With Zeros

```
nums = [1, 0, 3, 4]
```

**Prefix:**

```
[1, 1, 0, 0]
```

**Suffix pass:**

| `i` | `result` before | `suffix` | `result` after | `suffix` after |
|-----|-----------------|----------|----------------|----------------|
| 3   | 0               | 1        | **0**          | 4              |
| 2   | 0               | 4        | **0**          | 12             |
| 1   | 1               | 12       | **12**         | 0              |
| 0   | 1               | 0        | **0**          | 0              |

```
result = [0, 12, 0, 0]  ✅
```

Correct — only index 1 (the non-zero neighbour of the zero) has a non-zero product.

---

## Complexity

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n) | Two linear passes |
| **Space** | O(1) extra | The output array doesn't count; `suffix` is just one variable |

---

## The Mental Model

Imagine standing at each index and looking both ways down the array. Everything to your left is your **prefix**. Everything to your right is your **suffix**. Your answer is what you'd get if you multiplied all of it together — without touching yourself. Two passes, one from each direction, meet at every index to deliver exactly that.