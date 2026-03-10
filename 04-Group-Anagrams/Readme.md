# Group Anagrams — Sorted String as HashMap Key

The challenge: given a list of words, group all anagrams together. Anagrams are words that contain exactly the same letters, just rearranged — like `"eat"`, `"tea"`, `"ate"`.

The insight: if two words are anagrams, sorting their characters produces the same string. That sorted string becomes the key that groups them together in a HashMap.

---

## The Core Insight

```
"eat"  → sort → "aet"  ┐
"tea"  → sort → "aet"  ├── same key → same group
"ate"  → sort → "aet"  ┘

"tan"  → sort → "ant"  ┐
"nat"  → sort → "ant"  ┘── same key → same group

"bat"  → sort → "abt"  ──── unique key → own group
```

Every anagram family shares exactly one sorted signature. That signature is the HashMap key, and the value is the list of original words that share it.

---

## Walkthrough

```
input = ["eat", "tea", "tan", "ate", "nat", "bat"]
```

| Word    | Sorted Key | Map State |
|---------|------------|-----------|
| `"eat"` | `"aet"`    | `{"aet": ["eat"]}` |
| `"tea"` | `"aet"`    | `{"aet": ["eat","tea"]}` |
| `"tan"` | `"ant"`    | `{"aet": ["eat","tea"], "ant": ["tan"]}` |
| `"ate"` | `"aet"`    | `{"aet": ["eat","tea","ate"], "ant": ["tan"]}` |
| `"nat"` | `"ant"`    | `{"aet": ["eat","tea","ate"], "ant": ["tan","nat"]}` |
| `"bat"` | `"abt"`    | `{"aet": [...], "ant": [...], "abt": ["bat"]}` |

```
Output: [["eat","tea","ate"], ["tan","nat"], ["bat"]]  ✅
```

---

## Java Code

```java
import java.util.*;

public List<List<String>> groupAnagrams(String[] strs) {
    HashMap<String, List<String>> map = new HashMap<>();

    for (String word : strs) {
        // Create the sorted signature for this word
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        String key = new String(chars);

        // If this key doesn't exist yet, create an empty list for it
        map.putIfAbsent(key, new ArrayList<>());

        // Add the original word to its anagram group
        map.get(key).add(word);
    }

    return new ArrayList<>(map.values());
}
```

Clean alternative using `getOrDefault`:

```java
for (String word : strs) {
    char[] chars = word.toCharArray();
    Arrays.sort(chars);
    String key = new String(chars);

    List<String> group = map.getOrDefault(key, new ArrayList<>());
    group.add(word);
    map.put(key, group);
}
```

Both work. `putIfAbsent` is slightly cleaner since it avoids re-putting when the key already exists.

---

## Dry Run of the Code

```
strs = ["eat", "tea", "bat"]
```

**Iteration 1 — `"eat"`:**
```
chars = ['e','a','t'] → sort → ['a','e','t'] → key = "aet"
map does not have "aet" → putIfAbsent creates it
map.get("aet").add("eat")
map = { "aet": ["eat"] }
```

**Iteration 2 — `"tea"`:**
```
chars = ['t','e','a'] → sort → ['a','e','t'] → key = "aet"
map already has "aet" → putIfAbsent does nothing
map.get("aet").add("tea")
map = { "aet": ["eat", "tea"] }
```

**Iteration 3 — `"bat"`:**
```
chars = ['b','a','t'] → sort → ['a','b','t'] → key = "abt"
map does not have "abt" → putIfAbsent creates it
map.get("abt").add("bat")
map = { "aet": ["eat", "tea"], "abt": ["bat"] }
```

```
return map.values() → [["eat","tea"], ["bat"]]  ✅
```

---

## The Alternative Key — Character Frequency Array

Sorting costs O(k log k) per word where `k` is the word length. You can get that down to O(k) by using a character count array as the key instead.

```java
public List<List<String>> groupAnagrams(String[] strs) {
    HashMap<String, List<String>> map = new HashMap<>();

    for (String word : strs) {
        // Count frequency of each letter a–z
        int[] count = new int[26];
        for (char c : word.toCharArray()) {
            count[c - 'a']++;
        }

        // Encode the count array as a string key
        // e.g. "eat" → "#1#0#0#0#1#0...#1#0#0#0" (counts for a,b,c...t...)
        StringBuilder sb = new StringBuilder();
        for (int n : count) {
            sb.append('#');   // delimiter prevents collisions like [1,12] vs [11,2]
            sb.append(n);
        }
        String key = sb.toString();

        map.putIfAbsent(key, new ArrayList<>());
        map.get(key).add(word);
    }

    return new ArrayList<>(map.values());
}
```

> **Why the `#` delimiter?** Without it, counts `[1, 12]` and `[11, 2]` would both produce `"112"` — a collision. The delimiter makes them `"#1#12"` vs `"#11#2"`, which are distinct.

---

## Sorted Key vs Frequency Key — Which to Use?

| | Sorted Key | Frequency Key |
|---|---|---|
| **Time per word** | O(k log k) | O(k) |
| **Code simplicity** | ✅ Cleaner | More verbose |
| **Interview default** | ✅ Say this first | Mention as optimisation |
| **When it matters** | Fine for most inputs | Very long words, tight constraints |

> In an interview: implement the sorted key version first, then say *"if word length is very large, I can replace the sort with a 26-element frequency count to get O(k) per word instead of O(k log k)."* That shows you know both.

---

## Complexity

**Sorted key approach:**

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n × k log k) | `n` words, each sorted in `k log k` |
| **Space** | O(n × k)       | Storing all words across all groups in the map |

**Frequency key approach:**

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(n × k) | `n` words, each scanned once |
| **Space** | O(n × k) | Same |

---

## Why This Pattern Is Powerful

The sorted-string-as-key trick is an instance of a broader idea: **canonical form hashing**. You transform each item into a normalised representation that strips away irrelevant differences (letter order) and preserves only what matters (letter identity and count). Items that are equivalent under your transformation end up at the same key and group themselves automatically.

You'll see this same idea in problems involving equivalent fractions, isomorphic strings, and Roman numerals — anytime you need to group things that look different but are fundamentally the same.

---

## The Mental Model

Imagine every word walks up and hands you its letters in alphabetical order — its **sorted fingerprint**. Two words that are anagrams hand you the exact same fingerprint. You file each word under its fingerprint in a folder. At the end, each folder is one anagram group. One pass through the words, one lookup per word, done.