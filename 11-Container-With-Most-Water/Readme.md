# Container With Most Water — Greedy Shrink Shorter Side

The problem: given an array `height` where `height[i]` represents a vertical line of that height at position `i`, find two lines that together with the x-axis form a container holding the most water.

```
height = [1, 8, 6, 2, 5, 4, 8, 3, 7]
Answer: 49
```

You cannot tilt the container. Water level is determined by the **shorter** of the two lines.

---

## The Formula

For any two lines at positions `left` and `right`:

```
water = min(height[left], height[right]) × (right - left)
        ↑ shorter line caps water level    ↑ width between lines
```

The brute force checks every pair — O(n²). The two-pointer approach finds the maximum in O(n) by making a guaranteed greedy decision at every step.

---

## Walkthrough

```
height = [1, 8, 6, 2, 5, 4, 8, 3, 7]
indices:   0  1  2  3  4  5  6  7  8
```

Start with the widest possible container — pointers at both ends:

```
left=0 (height=1),  right=8 (height=7)
water = min(1,7) × (8-0) = 1 × 8 = 8
```

The shorter side is `left` (height 1). Move left inward:

```
left=1 (height=8),  right=8 (height=7)
water = min(8,7) × (8-1) = 7 × 7 = 49
```

The shorter side is `right` (height 7). Move right inward:

```
left=1 (height=8),  right=7 (height=3)
water = min(8,3) × (7-1) = 3 × 6 = 18
```

Move right inward (shorter side):

```
left=1 (height=8),  right=6 (height=8)
water = min(8,8) × (6-1) = 8 × 5 = 40
```

Equal heights — move either (move right):

```
left=1 (height=8),  right=5 (height=4)
water = min(8,4) × (5-1) = 4 × 4 = 16
```

Continuing... pointers converge without beating 49.

```
Maximum water = 49  ✅  (between lines at index 1 and index 8)
```

---

## Java Code

```java
public int maxArea(int[] height) {
    int left  = 0;
    int right = height.length - 1;
    int max   = 0;

    while (left < right) {
        int water = Math.min(height[left], height[right])
                    * (right - left);
        max = Math.max(max, water);

        // Move the shorter side inward
        if (height[left] <= height[right]) {
            left++;
        } else {
            right--;
        }
    }

    return max;
}
```

When heights are equal (`<=`), moving either pointer is correct — both are the bottleneck, so neither is worth keeping. Moving `left` is the convention.

---

## Dry Run

```
height = [1, 8, 6, 2, 5, 4, 8, 3, 7]
```

| `left` | `right` | `h[left]` | `h[right]` | `water` | `max` | move |
|--------|---------|-----------|------------|---------|-------|------|
| 0      | 8       | 1         | 7          | 1×8=8   | 8     | left++ |
| 1      | 8       | 8         | 7          | 7×7=49  | 49    | right-- |
| 1      | 7       | 8         | 3          | 3×6=18  | 49    | right-- |
| 1      | 6       | 8         | 8          | 8×5=40  | 49    | left++ |
| 2      | 6       | 6         | 8          | 6×4=24  | 49    | left++ |
| 3      | 6       | 2         | 8          | 2×3=6   | 49    | left++ |
| 4      | 6       | 5         | 8          | 5×2=10  | 49    | left++ |
| 5      | 6       | 4         | 8          | 4×1=4   | 49    | left++ |
| 6      | 6       | —         | —          | —       | —     | left==right, stop |

```
return 49  ✅
```

---

## The Greedy Argument — Why Moving the Shorter Side Is Always Safe

This is the crux of the problem. You need to be able to explain *why* this works.

At any state with pointers `left` and `right`, consider the current shorter side — say `height[left] ≤ height[right]`. You move `left++`. Is it possible you just skipped the optimal answer?

**No. Here is why:**

Every pair `(left, x)` for `x` between `left+1` and `right-1` has already been implicitly eliminated:

```
water(left, x) = min(height[left], height[x]) × (x - left)
```

Since `height[left]` is the shorter side of the current pair, and `x - left < right - left` (narrower width), the water for any inner pair `(left, x)` is at most:

```
min(height[left], height[x]) × (x - left)
≤ height[left] × (right - left)    ← bounded by current left height and current width
```

The current pair already achieves `height[left] × (right - left)` since left is shorter. So no pair involving the current `left` can beat what you've already computed. The current `left` is exhausted — move it.

In short: **keeping the shorter side can never lead to a larger container, because the shorter side is the ceiling on all remaining water involving that pointer.**

---

## Why Moving the Taller Side Would Be Wrong

The taller side might still pair well with something closer in. Moving the shorter side discards a pointer that cannot do better regardless of what it pairs with. Moving the taller side discards a pointer that could still be the answer for an inner pair.

Consider:
```
height = [2, 10, 4, 10, 2]
Optimal: min(10,10) × 2 = 20  (indices 1 and 3)
```

Starting at the ends, the greedy correctly moves the shorter side each time and will find `20`. A strategy that moves the taller side risks discarding one of the tall interior lines prematurely.

**The principle:** the tall wall is never the problem. Keeping it costs only width, which is already shrinking. You keep the tall wall as long as possible and only move it when it becomes the shorter side.

---

## Common Mistake — Moving Both Pointers

```java
// ❌ Wrong
if (height[left] < height[right]) {
    left++;
    right--;   // never move both — you skip valid pairs
}
```

Moving both pointers simultaneously skips pairs and can miss the optimal answer. Always move **exactly one pointer** per step.

---

## This Problem vs Two Sum II

Both use left-right two pointers. The decision logic differs:

| | Two Sum II | Container With Most Water |
|---|---|---|
| **Move condition** | `sum < target` → left++, `sum > target` → right-- | Always move shorter side |
| **Goal** | Find exact pair | Maximise a formula |
| **Termination** | Found target sum | Pointers meet |
| **Sorted input?** | ✅ Required | ❌ Not required |

Container With Most Water does not need a sorted array — it works on any heights. The greedy logic comes from the water formula, not from sorted order.

---

## Complexity

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n) | `left` and `right` together traverse `n` elements; each step advances one pointer |
| **Space** | O(1) | Two pointers and a running maximum |

---

## The Mental Model

You start with the widest possible container. Width is your biggest asset — you want to give it up only when forced to. You are forced when the **shorter wall is the bottleneck**: no matter how wide you stay, that short wall caps your water. The only hope of finding more water is to swap that short wall for something taller — which means moving inward. The tall wall is never the problem; keeping it costs you nothing but width, and since width is already shrinking, you keep the tall wall as long as possible. Every move sacrifices width in exchange for a chance at a taller minimum. You track the best trade seen so far.