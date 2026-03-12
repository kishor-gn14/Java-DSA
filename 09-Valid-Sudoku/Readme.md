# Valid Sudoku — 3×3 Box Hashing

The problem: given a 9×9 board partially filled with digits `'1'`–`'9'` and empty cells marked `'.'`, determine if the board is valid. A board is valid if:

- Each row contains no repeated digits
- Each column contains no repeated digits
- Each of the nine 3×3 sub-boxes contains no repeated digits

You are **not** solving the puzzle — only validating the current state. A valid board has no conflicts in what's already placed. Empty cells are ignored entirely.

---

## The Core Insight

For rows and columns, detecting duplicates is straightforward — track what you've seen in each row and each column as you scan. The interesting part is the **3×3 box**.

The key question: given a cell at position `(row, col)`, which of the nine boxes does it belong to?

```
boxRow = row / 3
boxCol = col / 3
```

Integer division maps every cell to a box index from `(0,0)` to `(2,2)`:

```
Columns:   0 1 2 | 3 4 5 | 6 7 8
           ------+-------+------
Rows 0-2:  (0,0) | (0,1) | (0,2)
Rows 3-5:  (1,0) | (1,1) | (1,2)
Rows 6-8:  (2,0) | (2,1) | (2,2)
```

Cell `(4, 7)` → box `(4/3, 7/3)` = box `(1, 2)`. Every cell maps to exactly one box in O(1).

---

## Walkthrough

Consider just these three cells being checked:

```
(0,0) = '5'  →  row 0, col 0, box (0,0)
(0,5) = '5'  →  row 0, col 5, box (0,1)
(1,0) = '5'  →  row 1, col 0, box (0,0)
```

- `(0,0)` and `(0,5)` share row 0 — but `'5'` appears once in row 0 so far, and they're in different boxes. OK so far.
- Add a second `'5'` in row 0 at `(0,5)` — **row conflict**. Invalid.
- `(0,0)` and `(1,0)` share box `(0,0)` — if both are `'5'`, **box conflict**. Invalid.

The algorithm catches all three types of conflict simultaneously in one pass.

---

## Java Code

```java
import java.util.*;

public boolean isValidSudoku(char[][] board) {
    // 9 sets for rows, 9 for cols, 9 for boxes
    HashSet<Character>[] rows  = new HashSet[9];
    HashSet<Character>[] cols  = new HashSet[9];
    HashSet<Character>[] boxes = new HashSet[9];

    for (int i = 0; i < 9; i++) {
        rows[i]  = new HashSet<>();
        cols[i]  = new HashSet<>();
        boxes[i] = new HashSet<>();
    }

    for (int row = 0; row < 9; row++) {
        for (int col = 0; col < 9; col++) {
            char val = board[row][col];

            if (val == '.') continue;    // empty cell — skip

            // Which box does this cell belong to?
            int boxIndex = (row / 3) * 3 + (col / 3);

            // Check all three constraints simultaneously
            if (!rows[row].add(val) ||
                !cols[col].add(val) ||
                !boxes[boxIndex].add(val)) {
                return false;            // duplicate found in row, col, or box
            }
        }
    }

    return true;
}
```

`HashSet.add()` returns `false` if the element already exists — the same trick used in Contains Duplicate. A single `||` chain across all three checks means the moment any duplicate is found, validation fails immediately.

**The box index formula:**

```
boxIndex = (row / 3) * 3 + (col / 3)
```

This flattens the 2D box coordinate `(row/3, col/3)` into a single integer from 0 to 8:

```
box (0,0)→0   box (0,1)→1   box (0,2)→2
box (1,0)→3   box (1,1)→4   box (1,2)→5
box (2,0)→6   box (2,1)→7   box (2,2)→8
```

---

## Dry Run

```
board (partial, showing only row 0 and col 0):

Row 0: ['5','3','.','.','7','.','.','.','.']
Col 0: ['5','6','.','.','.','.','.','.','8']  (vertical)
```

**Cell (0,0) = `'5'`:**
```
boxIndex = (0/3)*3 + (0/3) = 0*3 + 0 = 0
rows[0].add('5')   → true  (new)
cols[0].add('5')   → true  (new)
boxes[0].add('5')  → true  (new)
No conflict.
```

**Cell (0,1) = `'3'`:**
```
boxIndex = (0/3)*3 + (1/3) = 0 + 0 = 0
rows[0].add('3')   → true
cols[1].add('3')   → true
boxes[0].add('3')  → true
No conflict.
```

**Cell (0,4) = `'7'`:**
```
boxIndex = (0/3)*3 + (4/3) = 0 + 1 = 1
rows[0].add('7')   → true
cols[4].add('7')   → true
boxes[1].add('7')  → true
No conflict.
```

**Cell (1,0) = `'6'`:**
```
boxIndex = (1/3)*3 + (0/3) = 0 + 0 = 0
rows[1].add('6')   → true
cols[0].add('6')   → true  ('5' was there, '6' is new)
boxes[0].add('6')  → true  (box 0 has '5','3' — '6' is new)
No conflict.
```

**Hypothetical conflict — cell (0,5) = `'5'`:**
```
boxIndex = (0/3)*3 + (5/3) = 0 + 1 = 1
rows[0].add('5')   → false  ← '5' already in row 0!
return false immediately  ✅
```

---

## The Box Index Formula — Why It Works

The formula `(row / 3) * 3 + (col / 3)` is worth internalising:

```
row=0,col=0 → (0)*3+(0) = 0    row=0,col=3 → (0)*3+(1) = 1    row=0,col=6 → (0)*3+(2) = 2
row=3,col=0 → (1)*3+(0) = 3    row=3,col=3 → (1)*3+(1) = 4    row=3,col=6 → (1)*3+(2) = 5
row=6,col=0 → (2)*3+(0) = 6    row=6,col=3 → (2)*3+(1) = 7    row=6,col=6 → (2)*3+(2) = 8
```

It's the same logic as flattening a 2D grid into 1D: `row * width + col`, where `width = 3` (the number of boxes per row).

---

## Alternative — Boolean Arrays Instead of HashSets

For slightly better performance (no boxing, no hashing overhead), use `boolean[9][10]` — one boolean per digit per unit:

```java
public boolean isValidSudoku(char[][] board) {
    boolean[][] rows  = new boolean[9][10];  // rows[r][d] = digit d seen in row r?
    boolean[][] cols  = new boolean[9][10];
    boolean[][] boxes = new boolean[9][10];

    for (int row = 0; row < 9; row++) {
        for (int col = 0; col < 9; col++) {
            if (board[row][col] == '.') continue;

            int d        = board[row][col] - '0';   // '1'–'9' → 1–9
            int boxIndex = (row / 3) * 3 + (col / 3);

            if (rows[row][d] || cols[col][d] || boxes[boxIndex][d]) {
                return false;
            }

            rows[row][d]       = true;
            cols[col][d]       = true;
            boxes[boxIndex][d] = true;
        }
    }

    return true;
}
```

Index `0` of the digit dimension is unused (digits are 1–9). That's fine — a fixed tiny waste, not a problem.

| | HashSet approach | Boolean array approach |
|---|---|---|
| **Readability** | ✅ Clearer intent | Slightly more verbose |
| **Performance** | Slight overhead (boxing) | ✅ Faster, no hashing |
| **Space** | O(1) — fixed 27 sets of ≤9 chars | O(1) — fixed 3×9×10 booleans |
| **Interview default** | ✅ Natural to explain | Mention as optimisation |

---

## What You're Not Doing

Worth being explicit about two things the problem does **not** ask for:

- **Not checking if the board is solvable.** A valid board might have no solution — that's a different (much harder) problem. You only check that existing digits don't conflict.
- **Not filling empty cells.** `'.'` cells are completely ignored. Their future values are irrelevant to current validity.

---

## Complexity

| | Complexity | Notes |
|---|---|---|
| **Time**  | O(1) | The board is always 9×9 — exactly 81 cells, fixed iterations |
| **Space** | O(1) | 27 HashSets each holding at most 9 characters, fixed size |

Technically O(1) because the input size never changes — it's always a 9×9 grid. In practice you'd say O(n²) if the board were n×n.

---

## The Mental Model

Think of **27 bouncers** standing at the door of 27 clubs — one club per row, one per column, one per box. Each bouncer holds a guest list of digits already inside their club. When a digit arrives at cell `(row, col)`, it knocks on **three doors simultaneously**: the row club, the column club, and the box club. If any bouncer says *"you're already in here"*, the board is invalid. If all 81 filled cells get through all three doors without a repeat, the board is valid.