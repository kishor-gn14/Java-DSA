# Two Sum — HashMap Approach

## The Logic

For each number `num` at index `i`, you need its complement:

```
complement = target - num
```

If that complement already exists in your HashMap → you're done, you found the pair.  
If not → store `num` with its index in the map, and move on.

---

## Walkthrough

```
nums = [2, 7, 11, 15],  target = 9
```

| Step | `num` | `complement (9 - num)` | Map contains it? | Action |
|------|-------|------------------------|------------------|--------|
| 1    | 2     | 7                      | ❌ No             | Store `{2 → 0}` |
| 2    | 7     | 2                      | ✅ Yes!           | Return `[map.get(2), 1]` → `[0, 1]` |

Done in 2 steps. Never looked at `11` or `15`.

---

## Java Code

```java
import java.util.HashMap;

public int[] twoSum(int[] nums, int target) {
    HashMap<Integer, Integer> map = new HashMap<>(); // value → index

    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];

        if (map.containsKey(complement)) {
            return new int[]{map.get(complement), i};
        }

        map.put(nums[i], i); // store AFTER checking, handles edge cases
    }

    return new int[]{}; // no solution (problem guarantees one exists)
}
```

---

## Why `map.put()` comes AFTER the check

This is a subtle but important detail. Consider:

```
nums = [3, 3],  target = 6
```

If you stored first, then checked — index `0` would find itself as its own complement, giving you `[0, 0]` which is wrong (you need two distinct elements).  
By checking first, then storing, you guarantee the complement you find is always a **previous element at a different index**.

---

## Complexity

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n) | One pass through the array |
| **Space** | O(n) | Map stores at most `n` elements |

> vs brute force: **O(n²)** time, **O(1)** space.  
> The HashMap trades space for speed.

---

## The Mental Model

Think of the HashMap as a **"have I seen you before?" registry**. Every number you visit either finds its match already registered, or signs itself in for a future number to find. One pass, no going back.