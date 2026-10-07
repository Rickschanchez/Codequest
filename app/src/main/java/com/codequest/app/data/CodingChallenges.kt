package com.codequest.app.data

import com.codequest.app.data.questions.QuestionBank
import com.codequest.app.domain.model.CodingChallenge
import com.codequest.app.domain.model.CodingSpec
import com.codequest.app.domain.model.TestCase

/**
 * The coding ramp: five write-code challenges, one per rung from 6 to 10,
 * mirroring the MCQ ladder (read → complete → debug → implement → master).
 *
 * Nothing here is executed — [com.codequest.app.domain.code.CodeExecutor]
 * states that plainly. The specs exist so a learner can trace a solution
 * against real test cases by hand.
 */
object CodingChallenges {

    val all: List<CodingChallenge> = listOf(
        CodingChallenge(
            id = "code-arrays-l6-sum-range",
            topicId = "arrays",
            level = 6,
            title = "Sum of a range",
            summary = "Add every integer from a to b, inclusive.",
            xpReward = QuestionBank.xpForLevel(6),
            spec = CodingSpec(
                language = "cpp",
                problemStatement = "Write a function `int sumRange(int a, int b)` that returns the " +
                    "sum of every integer from a to b, inclusive. Assume a <= b.",
                starterCode = "int sumRange(int a, int b) {\n    // your code here\n}\n",
                constraints = listOf(
                    "-1000 <= a <= b <= 1000",
                    "Return a single integer",
                ),
                expectedBehavior = "sumRange(1, 5) returns 15 · sumRange(-2, 2) returns 0",
                testCases = listOf(
                    TestCase(input = "1 5", expectedOutput = "15"),
                    TestCase(input = "-2 2", expectedOutput = "0"),
                    TestCase(input = "7 7", expectedOutput = "7", isHidden = true),
                ),
                timeComplexityTarget = "O(n)",
                spaceComplexityTarget = "O(1)",
                hiddenTestCount = 1,
            ),
        ),
        CodingChallenge(
            id = "code-strings-l7-reverse",
            topicId = "strings",
            level = 7,
            title = "Reverse the characters",
            summary = "Reverse a string without allocating a second buffer.",
            xpReward = QuestionBank.xpForLevel(7),
            spec = CodingSpec(
                language = "cpp",
                problemStatement = "Write `string reversed(string s)` that returns the characters " +
                    "of s in reverse order. Build it by swapping from both ends, not by " +
                    "concatenating backwards.",
                starterCode = "string reversed(string s) {\n    int i = 0, j = (int)s.size() - 1;\n    // swap until the ends meet\n    return s;\n}\n",
                constraints = listOf(
                    "0 <= s.length <= 1000",
                    "s contains printable ASCII",
                ),
                expectedBehavior = "reversed(\"quest\") returns \"tseuq\" · reversed(\"\") returns \"\"",
                testCases = listOf(
                    TestCase(input = "quest", expectedOutput = "tseuq"),
                    TestCase(input = "abba", expectedOutput = "abba"),
                    TestCase(input = "q", expectedOutput = "q", isHidden = true),
                ),
                timeComplexityTarget = "O(n)",
                spaceComplexityTarget = "O(1)",
                hiddenTestCount = 1,
            ),
        ),
        CodingChallenge(
            id = "code-searching-l8-off-by-one",
            topicId = "searching",
            level = 8,
            title = "Fix the search",
            summary = "The binary search misses the last candidate — repair it.",
            xpReward = QuestionBank.xpForLevel(8),
            spec = CodingSpec(
                language = "cpp",
                problemStatement = "The function below must return the index of target in a " +
                    "sorted array, or -1 when it is absent. It currently reports -1 for the " +
                    "final element. Keep it O(log n) and fix the boundary.",
                starterCode = "int search(const vector<int>& a, int target) {\n    int lo = 0, hi = (int)a.size(); // suspect\n    while (lo < hi) {\n        int mid = lo + (hi - lo) / 2;\n        if (a[mid] == target) return mid;\n        if (a[mid] < target) lo = mid + 1;\n        else hi = mid;\n    }\n    return -1;\n}\n",
                constraints = listOf(
                    "1 <= a.length <= 100000",
                    "a is sorted ascending, values may repeat",
                    "Target must be found in O(log n)",
                ),
                expectedBehavior = "search({1,3,5}, 5) returns 2 · search({2,4}, 3) returns -1",
                testCases = listOf(
                    TestCase(input = "1 3 5 / 5", expectedOutput = "2"),
                    TestCase(input = "2 4 / 3", expectedOutput = "-1"),
                    TestCase(input = "1 1 1 / 1", expectedOutput = "0", isHidden = true),
                ),
                timeComplexityTarget = "O(log n)",
                spaceComplexityTarget = "O(1)",
                hiddenTestCount = 1,
            ),
        ),
        CodingChallenge(
            id = "code-linked-lists-l9-kth",
            topicId = "linked-lists",
            level = 9,
            title = "Kth from the end",
            summary = "One traversal, no length count, no second pass.",
            xpReward = QuestionBank.xpForLevel(9),
            spec = CodingSpec(
                language = "cpp",
                problemStatement = "Given the head of a singly linked list and an integer k " +
                    "(0-indexed from the end), return the value at that position. Solve it in " +
                    "a single pass without measuring the length first.",
                starterCode = "struct Node { int v; Node* next; };\n\nint kthFromEnd(Node* head, int k) {\n    // one pass, one gap\n    return -1;\n}\n",
                constraints = listOf(
                    "1 <= number of nodes <= 10000",
                    "0 <= k < number of nodes",
                    "Exactly one pass over the list",
                ),
                expectedBehavior = "list 1->2->3->4 with k=1 returns 4",
                testCases = listOf(
                    TestCase(input = "1 2 3 4 / k=1", expectedOutput = "4"),
                    TestCase(input = "9 / k=0", expectedOutput = "9"),
                    TestCase(input = "5 6 7 / k=2", expectedOutput = "5", isHidden = true),
                ),
                timeComplexityTarget = "O(n)",
                spaceComplexityTarget = "O(1)",
                hiddenTestCount = 1,
            ),
        ),
        CodingChallenge(
            id = "code-dp-l10-distinct",
            topicId = "dp",
            level = 10,
            title = "Longest distinct run",
            summary = "The longest substring with no repeated character.",
            xpReward = QuestionBank.xpForLevel(10),
            spec = CodingSpec(
                language = "cpp",
                problemStatement = "Write `int longestDistinct(const string& s)` returning the " +
                    "length of the longest substring with no repeating character. One pass over " +
                    "the string, constant extra memory.",
                starterCode = "int longestDistinct(const string& s) {\n    vector<int> last(256, -1);\n    int start = 0, best = 0;\n    // slide the window\n    return best;\n}\n",
                constraints = listOf(
                    "0 <= s.length <= 100000",
                    "One pass: O(n) time, O(1) extra memory",
                ),
                expectedBehavior = "longestDistinct(\"abba\") returns 2 · longestDistinct(\"abc\") returns 3",
                testCases = listOf(
                    TestCase(input = "abba", expectedOutput = "2"),
                    TestCase(input = "abcabcbb", expectedOutput = "3"),
                    TestCase(input = "", expectedOutput = "0", isHidden = true),
                ),
                timeComplexityTarget = "O(n)",
                spaceComplexityTarget = "O(1)",
                hiddenTestCount = 1,
            ),
        ),
    )

    fun find(id: String): CodingChallenge? = all.firstOrNull { it.id == id }
}
