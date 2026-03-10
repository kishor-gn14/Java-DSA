# Top K Frequent Elements — HashMap + Bucket Sort / Min-Heap

The problem: given an array of numbers, return the `k` most frequent elements. Two approaches solve this cleanly — a min-heap at O(n log k) and bucket sort at O(n). Both are worth knowing.

---

## Step 1 — Count Frequencies (Both Approaches Start Here)

Before anything else, count how many times each number appears using a HashMap.

```
nums = [1, 1, 1, 2, 2, 3],  k = 2
frequency map = { 1→3, 2→2, 3→1 }
```

Now the problem becomes: given these frequency counts, find the `k` entries with the highest values.

---

## Approach 1 — Min-Heap (O(n log k))

### The Idea

Maintain a min-heap of size exactly `k`. It always holds the `k` most frequent elements seen so far, with the least frequent among them sitting at the top (min-heap property).

For each element in the frequency map:
- Push it onto the heap
- If the heap grows beyond size `k`, pop the minimum — the weakest candidate is evicted
- Whatever remains in the heap after processing everything is your answer

The heap never grows beyond `k` entries, so every push/pop costs O(log k) instead of O(log n).

---

### Walkthrough

```
frequency map = { 1→3, 2→2, 3→1 },  k = 2
```

Heap stores pairs of `(frequency, number)`, ordered by frequency (min at top).

| Action | Heap state (min at top) |
|--------|------------------------|
| Push `(3, 1)` | `[(3,1)]` |
| Push `(2, 2)` | `[(2,2), (3,1)]` — size = k = 2, no eviction |
| Push `(1, 3)` → size = 3 > k | `[(1,3), (3,1), (2,2)]` |
| Pop min → evict `(1,3)` | `[(2,2), (3,1)]` |

```
Remaining in heap: [2, 1]  ✅  (the 2 most frequent elements)
```

---

### Java Code — Min-Heap

```java
import java.util.*;

public int[] topKFrequent(int[] nums, int k) {
    // Step 1: count frequencies
    HashMap<Integer, Integer> freqMap = new HashMap<>();
    for (int num : nums) {
        freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
    }

    // Step 2: min-heap ordered by frequency (smallest freq at top)
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(
        (a, b) -> freqMap.get(a) - freqMap.get(b)
    );

    // Step 3: maintain heap of size k
    for (int num : freqMap.keySet()) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();   // evict least frequent
        }
    }

    // Step 4: extract results
    int[] result = new int[k];
    for (int i = 0; i < k; i++) {
        result[i] = minHeap.poll();
    }

    return result;
}
```

The comparator `(a, b) -> freqMap.get(a) - freqMap.get(b)` orders by frequency ascending — so the element with the **lowest** frequency sits at the top and gets evicted first when the heap exceeds size `k`.

---

### Dry Run

```
nums = [1,1,1,2,2,3],  k = 2
freqMap = {1→3, 2→2, 3→1}
```

Processing keys `[1, 2, 3]`:

```
offer(1) → heap: [1]         size=1 ≤ k, no eviction
offer(2) → heap: [2, 1]      size=2 ≤ k, no eviction
                              (2 is min: freq 2 < freq 3)
offer(3) → heap: [3, 1, 2]   size=3 > k
poll()   → evict 3 (freq=1)
           heap: [2, 1]       size=2 ✅

result = [2, 1]  ✅
```

---

### Complexity — Min-Heap

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n log k) | `n` elements in freq map, each heap op costs log k |
| **Space** | O(n + k)   | Freq map holds `n` entries, heap holds `k` |

When `k` is much smaller than `n`, log k is significantly cheaper than log n. This is the standard interview answer.

---

## Approach 2 — Bucket Sort (O(n))

### The Idea

The maximum possible frequency any element can have is `n` (appears every time). So create an array of `n + 1` buckets where **index = frequency**. Drop each element into its frequency bucket, then read from the highest-frequency bucket downward until you've collected `k` elements.

No sorting. No heap. Just direct indexing.

---

### Walkthrough

```
nums = [1,1,1,2,2,3],  n = 6,  k = 2
freqMap = { 1→3, 2→2, 3→1 }
```

**Build buckets** (index = frequency):

```
index:  0     1       2       3       4    5    6
value: []   [3]     [2]     [1]     []   []   []
         (freq=1) (freq=2) (freq=3)
```

**Read from right (highest freq) to left**, collect until you have `k` elements:

```
index 6 → empty
index 5 → empty
index 4 → empty
index 3 → [1]  → collect 1   (have 1 of k=2)
index 2 → [2]  → collect 2   (have 2 of k=2) ✅ stop
```

```
result = [1, 2]  ✅
```

---

### Java Code — Bucket Sort

```java
import java.util.*;

public int[] topKFrequent(int[] nums, int k) {
    // Step 1: count frequencies
    HashMap<Integer, Integer> freqMap = new HashMap<>();
    for (int num : nums) {
        freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
    }

    // Step 2: create buckets — index represents frequency
    List<Integer>[] buckets = new List[nums.length + 1];
    for (int i = 0; i < buckets.length; i++) {
        buckets[i] = new ArrayList<>();
    }
    for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
        int freq = entry.getValue();
        buckets[freq].add(entry.getKey());
    }

    // Step 3: collect top k from highest frequency bucket downward
    int[] result = new int[k];
    int idx = 0;
    for (int freq = buckets.length - 1; freq >= 0 && idx < k; freq--) {
        for (int num : buckets[freq]) {
            result[idx++] = num;
            if (idx == k) break;
        }
    }

    return result;
}
```

---

### Dry Run

```
nums = [1,1,1,2,2,3],  k = 2
freqMap = {1→3, 2→2, 3→1}
buckets.length = 7  (indices 0–6)
```

**Filling buckets:**

```
num=1, freq=3 → buckets[3].add(1)
num=2, freq=2 → buckets[2].add(2)
num=3, freq=1 → buckets[1].add(3)

buckets = [[], [3], [2], [1], [], [], []]
```

**Reading right to left:**

```
freq=6 → []   skip
freq=5 → []   skip
freq=4 → []   skip
freq=3 → [1]  → result[0] = 1,  idx=1
freq=2 → [2]  → result[1] = 2,  idx=2 = k, stop

result = [1, 2]  ✅
```

---

### Complexity — Bucket Sort

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n) | Frequency count O(n) + bucket fill O(n) + bucket read O(n) |
| **Space** | O(n) | Freq map + `n+1` buckets |

True O(n) — no log factor anywhere.

---

## Side-by-Side Comparison

| | Min-Heap | Bucket Sort |
|---|---|---|
| **Time** | O(n log k) | O(n) |
| **Space** | O(n + k) | O(n) |
| **When k << n** | Very efficient | Same O(n) |
| **Code complexity** | Moderate | Moderate |
| **Handles streaming data** | ✅ Yes | ❌ No (needs `n` upfront) |
| **Interview default** | ✅ Most common answer | Mention as optimisation |

---

## What to Say in an Interview

**Start with the heap:** *"I'll count frequencies with a HashMap, then use a min-heap of size k to track the top k elements — O(n log k) time."*

**Then offer the upgrade:** *"If we want true O(n), we can use bucket sort — since frequency is bounded by n, we index directly into an array of n+1 buckets and read from the top."*

That progression — working solution first, then optimal — is exactly what interviewers want to see.

---

## The Mental Model

**Min-heap:** Imagine a VIP room with exactly `k` seats. Every candidate walks in. If there's a free seat, they sit. If the room is full, the least popular person already seated gets bumped to make room — but only if the newcomer is more popular. At the end, whoever's still seated are your top `k`.

**Bucket sort:** Imagine a leaderboard wall with `n` shelves, one per possible score. Each element walks straight to its shelf and sits down. You then read names off shelves from the top down until you've called `k` names. No comparisons, no shuffling — just direct placement and direct retrieval.