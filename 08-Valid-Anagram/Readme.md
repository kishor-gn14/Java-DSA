# Valid Anagram — Character Frequency Array

The problem: given two strings `s` and `t`, return `true` if `t` is an anagram of `s` — meaning both strings contain exactly the same characters in exactly the same quantities, just possibly in a different order.

```
s = "anagram",  t = "nagaram"  →  true
s = "rat",      t = "car"      →  false
```

---

## The Core Insight

Two strings are anagrams if and only if their **character frequency profiles are identical**. You don't care about order — only about counts. So instead of sorting both strings and comparing (O(n log n)), you count the frequency of each character in both strings and check if the counts match (O(n)).

The cleanest way: use a single `int[26]` array indexed by letter. **Increment** for every character in `s`, **decrement** for every character in `t`. If the strings are anagrams, every increment has a matching decrement — the array ends up all zeros.

---

## Walkthrough

```
s = "anagram",  t = "nagaram"
```

Count array (index 0 = `'a'`, index 1 = `'b'`, ... index 25 = `'z'`):

**Processing `s = "anagram"` — increment:**

| `char` | index | count |
|--------|-------|-------|
| `a`    | 0     | +1 → 1 |
| `n`    | 13    | +1 → 1 |
| `a`    | 0     | +1 → 2 |
| `g`    | 6     | +1 → 1 |
| `r`    | 17    | +1 → 1 |
| `a`    | 0     | +1 → 3 |
| `m`    | 12    | +1 → 1 |

**Processing `t = "nagaram"` — decrement:**

| `char` | index | count |
|--------|-------|-------|
| `n`    | 13    | -1 → 0 |
| `a`    | 0     | -1 → 2 |
| `g`    | 6     | -1 → 0 |
| `a`    | 0     | -1 → 1 |
| `r`    | 17    | -1 → 0 |
| `a`    | 0     | -1 → 0 |
| `m`    | 12    | -1 → 0 |

```
Final count array: all zeros ✅ → true
```

---

## Java Code

```java
public boolean isAnagram(String s, String t) {
    // Different lengths can never be anagrams
    if (s.length() != t.length()) return false;

    int[] count = new int[26];

    for (int i = 0; i < s.length(); i++) {
        count[s.charAt(i) - 'a']++;   // increment for s
        count[t.charAt(i) - 'a']--;   // decrement for t
    }

    for (int n : count) {
        if (n != 0) return false;      // mismatch found
    }

    return true;
}
```

Both strings are the same length, so you can process them together in a single loop — one increment and one decrement per iteration. That's one pass instead of two.

The expression `s.charAt(i) - 'a'` converts a character to an array index: `'a'` → 0, `'b'` → 1, ..., `'z'` → 25. Java characters are numeric under the hood, so subtracting `'a'` gives the offset directly.

---

## Dry Run

```
s = "rat",  t = "car"
Length check: both length 3 ✅
```

| `i` | `s.charAt(i)` | index | `count` | `t.charAt(i)` | index | `count` |
|-----|---------------|-------|---------|---------------|-------|---------|
| 0   | `'r'`         | 17    | +1 → 1  | `'c'`         | 2     | -1 → -1 |
| 1   | `'a'`         | 0     | +1 → 1  | `'a'`         | 0     | -1 → 0  |
| 2   | `'t'`         | 19    | +1 → 1  | `'r'`         | 17    | -1 → 0  |

```
Final non-zero entries: count[2] = -1 (c),  count[19] = 1 (t)
Not all zeros → return false ✅
```

`"rat"` has a `t`, `"car"` doesn't. `"car"` has a `c`, `"rat"` doesn't. The count array exposes both mismatches simultaneously.

---

## What a Negative Count Means

- A **negative** value at index `i` means `t` has more of that character than `s`
- A **positive** value means `s` has more
- **Zero** means they match perfectly for that character

Anagram requires every slot to be exactly zero — any deviation in either direction is a failure.

---

## Three Approaches Compared

**Sorting — O(n log n):**

```java
public boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    char[] sArr = s.toCharArray();
    char[] tArr = t.toCharArray();
    Arrays.sort(sArr);
    Arrays.sort(tArr);
    return Arrays.equals(sArr, tArr);
}
```

Simple and readable. Fails the O(n) bar. Modifies the input. Fine if constraints are loose.

**HashMap — O(n):**

```java
public boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    HashMap<Character, Integer> map = new HashMap<>();
    for (char c : s.toCharArray())
        map.put(c, map.getOrDefault(c, 0) + 1);
    for (char c : t.toCharArray()) {
        map.put(c, map.getOrDefault(c, 0) - 1);
        if (map.get(c) < 0) return false;  // early exit
    }
    return true;
}
```

Handles Unicode characters beyond a–z. Necessary if the problem says input can be any character, not just lowercase English letters. Slightly more overhead than the array due to boxing and hashing.

**`int[26]` array — O(n):**

```java
// The approach above — optimal for lowercase a–z
```

Fastest in practice. Fixed 26-slot array means O(1) space (constant, not input-dependent). Zero boxing overhead. The right default for this problem.

| | Sorting | HashMap | `int[26]` Array |
|---|---|---|---|
| **Time** | O(n log n) | O(n) | O(n) |
| **Space** | O(n) | O(n) | O(1) — fixed 26 slots |
| **Handles Unicode** | ✅ | ✅ | ❌ a–z only |
| **Interview default** | Mention | Unicode variant | ✅ Standard answer |

---

## The Unicode Follow-Up

Interviewers sometimes ask: *"What if the strings contain Unicode characters, not just lowercase letters?"* The `int[26]` array breaks because Unicode has over a million code points. Switch to a HashMap:

```java
public boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;

    HashMap<Character, Integer> count = new HashMap<>();

    for (char c : s.toCharArray())
        count.put(c, count.getOrDefault(c, 0) + 1);

    for (char c : t.toCharArray()) {
        if (!count.containsKey(c)) return false;
        count.put(c, count.get(c) - 1);
        if (count.get(c) == 0) count.remove(c);
    }

    return count.isEmpty();
}
```

The map only holds characters that appear in `s`. Processing `t` decrements each count, removing entries that hit zero. If `t` introduces a character not in `s`, it's caught immediately by `!count.containsKey(c)`. At the end, an empty map confirms perfect balance.

---

## Early Exit Optimisation

The single-pass version always completes both strings before returning. You can exit earlier by checking during the decrement phase:

```java
for (int i = 0; i < s.length(); i++) {
    count[s.charAt(i) - 'a']++;
}
for (int i = 0; i < t.length(); i++) {
    count[t.charAt(i) - 'a']--;
    if (count[t.charAt(i) - 'a'] < 0) return false;  // t has excess of this char
}
return true;
```

In the worst case (genuine anagrams or mismatch at the last character) this makes no difference. For inputs that diverge early it saves unnecessary iterations. Not required in an interview but worth mentioning.

---

## Complexity

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n) | One pass through both strings |
| **Space** | O(1) | Fixed 26-element array regardless of input size |

Technically the space is O(1) because the array size never changes with input. This is better than HashMap which is O(k) where `k` is the number of distinct characters.

---

## The Mental Model

Imagine a balance scale with 26 slots — one per letter. Every character from `s` drops a coin into its slot. Every character from `t` removes a coin from its slot. If the strings are anagrams, every coin added has exactly one coin removed — the scale is perfectly balanced at zero across all 26 slots. Any imbalance, positive or negative, means the strings differ in at least one character's count.