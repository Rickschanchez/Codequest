package com.codequest.app.data

import com.codequest.app.data.questions.QuestionBank
import com.codequest.app.domain.model.Difficulty
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.Lesson
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.domain.model.UserProgress
import java.util.Calendar

/**
 * Single source of the local mock content for the MVP.
 *
 * Nothing here reaches the network or a database; [MockCodeQuestRepository]
 * copies this data into reactive state and owns every mutation.
 *
 * Questions live in [QuestionBank] — see data/questions.
 */
object MockData {

    // ----------------------------------------------------------------- topics

    private data class TopicBlueprint(
        val id: String,
        val name: String,
        val description: String,
        val order: Int,
        val difficultyLabel: String,
        val difficultyDots: Int,
    )

    private val topicBlueprints = listOf(
        TopicBlueprint(
            id = "foundations",
            name = "DSA Foundations",
            description = "Complexity thinking and the problem-solving habits every interview leans on.",
            order = 1,
            difficultyLabel = "Beginner",
            difficultyDots = 1,
        ),
        TopicBlueprint(
            id = "arrays",
            name = "Arrays",
            description = "Contiguous storage, indexing and the patterns built on top of it.",
            order = 2,
            difficultyLabel = "Easy → Medium",
            difficultyDots = 2,
        ),
        TopicBlueprint(
            id = "strings",
            name = "Strings",
            description = "Character sequences, immutability and string manipulation tricks.",
            order = 3,
            difficultyLabel = "Easy → Medium",
            difficultyDots = 2,
        ),
        TopicBlueprint(
            id = "searching",
            name = "Searching",
            description = "Linear scans, binary search and finding elements efficiently.",
            order = 4,
            difficultyLabel = "Medium",
            difficultyDots = 3,
        ),
        TopicBlueprint(
            id = "sorting",
            name = "Sorting",
            description = "Comparison sorts, stability and complexity trade-offs.",
            order = 5,
            difficultyLabel = "Medium",
            difficultyDots = 3,
        ),
        TopicBlueprint(
            id = "linked-lists",
            name = "Linked Lists",
            description = "Pointer manipulation and dynamic linear structures.",
            order = 6,
            difficultyLabel = "Medium",
            difficultyDots = 3,
        ),
        TopicBlueprint(
            id = "stacks",
            name = "Stacks",
            description = "LIFO discipline for backtracking, parsing and depth-first work.",
            order = 7,
            difficultyLabel = "Medium",
            difficultyDots = 3,
        ),
        TopicBlueprint(
            id = "queues",
            name = "Queues",
            description = "FIFO discipline for scheduling, buffering and breadth-first search.",
            order = 8,
            difficultyLabel = "Medium",
            difficultyDots = 3,
        ),
        TopicBlueprint(
            id = "trees",
            name = "Trees",
            description = "Hierarchical data, traversals and binary search trees.",
            order = 9,
            difficultyLabel = "Medium → Hard",
            difficultyDots = 4,
        ),
        TopicBlueprint(
            id = "graphs",
            name = "Graphs",
            description = "Vertices, edges, traversal and shortest paths.",
            order = 10,
            difficultyLabel = "Hard",
            difficultyDots = 5,
        ),
        TopicBlueprint(
            id = "greedy",
            name = "Greedy",
            description = "Locally optimal choices backed by a global guarantee.",
            order = 11,
            difficultyLabel = "Medium → Hard",
            difficultyDots = 4,
        ),
        TopicBlueprint(
            id = "dp",
            name = "Dynamic Programming",
            description = "Memoisation, state design and optimal substructure.",
            order = 12,
            difficultyLabel = "Hard",
            difficultyDots = 5,
        ),
    )

    /**
     * Every roadmap stop is open in presentation mode: each one ships lessons
     * and questions, so tapping any topic always lands on a working screen.
     */
    private val interactiveTopicIds: Set<String> = topicBlueprints.mapTo(mutableSetOf()) { it.id }

    // ---------------------------------------------------------------- lessons

    private val baseLessons: List<Lesson> = listOf(
        // ---------------------------------------------------- DSA Foundations
        Lesson(
            id = "les-foundations-1",
            topicId = "foundations",
            order = 1,
            title = "What Big-O actually measures",
            explanation = "Big-O describes how the work of an algorithm grows as the input grows. " +
                "It ignores the machine, the constants and the lower-order terms, because those " +
                "fade once n gets large — 3n² + 100n and n² are the same class.",
            example = "Two nested loops over n elements do n x n steps, so the cost is O(n²). " +
                "A single pass is O(n).",
            keyTakeaway = "Read Big-O as a growth rate, never as a running time in milliseconds.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-foundations-2",
            topicId = "foundations",
            order = 2,
            title = "Best, worst and average case",
            explanation = "The same algorithm behaves differently depending on the input it is given. " +
                "Best case is the luckiest arrangement, worst case is the most hostile one, and " +
                "average case is the expected cost across a realistic distribution.",
            example = "Linear search finds a target sitting at index 0 in O(1); if the target is " +
                "absent it scans every slot, O(n).",
            keyTakeaway = "Interviewers ask for the worst case because it is the guarantee you can keep.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-foundations-3",
            topicId = "foundations",
            order = 3,
            title = "Space complexity",
            explanation = "Space complexity counts the extra memory an algorithm needs beyond its " +
                "input. Recursion counts too: every live call frame is memory, which is why a deep " +
                "recursion can overflow the stack even when the answer is small.",
            example = "Swapping two array slots through a temporary uses O(1) extra space; copying " +
                "the whole array first uses O(n).",
            keyTakeaway = "Ask what is allocated, not how big the input already is.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-foundations-4",
            topicId = "foundations",
            order = 4,
            title = "Iteration against recursion",
            explanation = "Recursion expresses a problem in terms of smaller copies of itself, which " +
                "often mirrors the definition exactly. Iteration keeps one set of variables alive " +
                "and never pays for call frames.",
            example = "factorial(n) as a loop multiplies an accumulator; the recursive form calls " +
                "itself until n <= 1 and then unwinds.",
            keyTakeaway = "Reach for recursion when the structure is natural, then check the depth.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-foundations-5",
            topicId = "foundations",
            order = 5,
            title = "Amortised cost",
            explanation = "Some operations are occasionally expensive but cheap on average across a " +
                "long run. Amortised analysis judges the whole sequence rather than the single worst " +
                "call, which is why vector growth is described as constant per push.",
            example = "Eight push_back calls may trigger one or two resizes; the copying is paid " +
                "once and spread across every push that followed.",
            keyTakeaway = "A rare resize does not make the hot path slow.",
            durationMinutes = 4,
            xpReward = 0,
        ),

        // ------------------------------------------------------------ Arrays
        Lesson(
            id = "les-arrays-1",
            topicId = "arrays",
            order = 1,
            title = "Contiguous storage and indexing",
            explanation = "An array holds same-type elements in one contiguous block and addresses " +
                "them with a zero-based index. Because the memory is contiguous, index i always sits " +
                "exactly i slots after the start — which is why reads are instant.",
            example = "int[] a = {5, 10, 15}; a[1] is 10, because index 0 holds 5.",
            keyTakeaway = "Index 0 is the first element; index n-1 is the last.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-arrays-2",
            topicId = "arrays",
            order = 2,
            title = "Traversing an array",
            explanation = "A single loop from 0 to length-1 visits every element exactly once. This " +
                "one pattern underpins searching, summing, filtering and most interview warm-ups.",
            example = "int sum = 0; for (int v : a) sum += v;   // one pass, O(n)",
            keyTakeaway = "If you only need one pass, never nest a second loop inside it.",
            durationMinutes = 3,
            xpReward = 0,
        ),
        Lesson(
            id = "les-arrays-3",
            topicId = "arrays",
            order = 3,
            title = "Why insertion costs so much",
            explanation = "Pushing into the middle shifts every later element one slot to keep the " +
                "block contiguous. Removing does the same in reverse, so both are linear while reads " +
                "stay constant.",
            example = "Inserting at index 0 of a 5-element array moves all 5 elements before the " +
                "new value lands.",
            keyTakeaway = "Arrays win at access and lose at insertion — that is the trade.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-arrays-4",
            topicId = "arrays",
            order = 4,
            title = "Two pointers",
            explanation = "Two indices walking through an array can replace a nested loop when the " +
                "task involves pairs, partitions or removal in place. Each pointer moves at most n " +
                "times, so the whole scan stays linear.",
            example = "In a sorted array, start one pointer at each end and move the smaller one " +
                "forward until the pair sums to the target.",
            keyTakeaway = "Two pointers turn O(n²) pair searching into O(n).",
            durationMinutes = 5,
            xpReward = 0,
        ),

        // ----------------------------------------------------------- Strings
        Lesson(
            id = "les-strings-1",
            topicId = "strings",
            order = 1,
            title = "Strings are immutable",
            explanation = "Once created, a string's characters cannot change. Every concatenation " +
                "produces a new object, so building a string inside a loop quietly allocates a fresh " +
                "copy on every pass.",
            example = "s += c inside a loop makes n intermediate strings; a builder or a character " +
                "buffer does it in place.",
            keyTakeaway = "Accumulate into a builder, not into the string itself.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-strings-2",
            topicId = "strings",
            order = 2,
            title = "Characters, indices and scanning",
            explanation = "A string is a sequence of characters addressed by index, exactly like an " +
                "array. Comparing case, checking digits and walking from both ends all reduce to " +
                "index arithmetic.",
            example = "A palindrome check compares charAt(i) with charAt(n - 1 - i) while i advances.",
            keyTakeaway = "Most string questions are array questions wearing a different name.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-strings-3",
            topicId = "strings",
            order = 3,
            title = "Frequency counting",
            explanation = "A fixed alphabet lets you trade memory for speed: one counter per " +
                "character gives constant-time updates and turns repeated scans into a single pass.",
            example = "int[26] counts every letter in 'codequest' in one traversal of the string.",
            keyTakeaway = "Count first, answer second — one pass beats many.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-strings-4",
            topicId = "strings",
            order = 4,
            title = "The sliding window",
            explanation = "A window is a start and an end pointer holding a candidate substring. " +
                "Shrink it when a constraint breaks and grow it when everything still holds, and each " +
                "character is touched twice at most.",
            example = "For the longest substring without repeats, advance the end and move the start " +
                "past the last repeated character.",
            keyTakeaway = "Sliding window keeps every character in at most two operations.",
            durationMinutes = 5,
            xpReward = 0,
        ),

        // --------------------------------------------------------- Searching
        Lesson(
            id = "les-searching-1",
            topicId = "searching",
            order = 1,
            title = "Linear search",
            explanation = "Linear search walks the collection from the front and stops at the first " +
                "match. It needs no ordering, which makes it the correct default for unsorted data.",
            example = "Scanning int[] a for 7 checks a[0], then a[1] and so on until it lands.",
            keyTakeaway = "Unsorted input means O(n) — no shortcut exists.",
            durationMinutes = 3,
            xpReward = 0,
        ),
        Lesson(
            id = "les-searching-2",
            topicId = "searching",
            order = 2,
            title = "Binary search setup",
            explanation = "On sorted data you can discard half the range each step. Keep two bounds, " +
                "compare against the middle element and narrow until the value is found or the bounds " +
                "cross.",
            example = "int mid = lo + (hi - lo) / 2; avoids the overflow that (lo + hi) / 2 invites.",
            keyTakeaway = "Half the range per step gives O(log n).",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-searching-3",
            topicId = "searching",
            order = 3,
            title = "Binary search variants",
            explanation = "Duplicates break the assumption that a match is unique. To find the first " +
                "or last occurrence, keep searching on the side that could still hold an earlier or " +
                "later copy instead of stopping at the first hit.",
            example = "When a[mid] matches, move hi to mid - 1 to keep hunting for an earlier index.",
            keyTakeaway = "Decide whether you want any match or a specific edge match.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-searching-4",
            topicId = "searching",
            order = 4,
            title = "Choosing the right search",
            explanation = "The precondition decides the algorithm. Sorted data earns a logarithmic " +
                "search; unsorted data must be scanned or sorted first, and sorting costs more than " +
                "one linear pass unless you search repeatedly.",
            example = "One lookup on unsorted data: scan. A thousand lookups: sort once, then bisect.",
            keyTakeaway = "Paying O(n log n) to sort only pays back across many queries.",
            durationMinutes = 4,
            xpReward = 0,
        ),

        // ----------------------------------------------------------- Sorting
        Lesson(
            id = "les-sorting-1",
            topicId = "sorting",
            order = 1,
            title = "Bubble sort and early exit",
            explanation = "Bubble sort repeatedly swaps adjacent out-of-order pairs, so the largest " +
                "remaining value floats to the end on every pass. If a pass completes without a single " +
                "swap, the array is already sorted.",
            example = "An extra boolean tracking swaps in a pass turns the worst case O(n²) into O(n) " +
                "on already-sorted input.",
            keyTakeaway = "Adaptive behaviour is what makes a simple sort tolerable.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-sorting-2",
            topicId = "sorting",
            order = 2,
            title = "Selection sort",
            explanation = "Selection sort scans the unsorted suffix for the smallest value and swaps " +
                "it into place. It always does the same number of passes, so no input can rescue it.",
            example = "On every pass exactly one swap happens, which is the fewest of the quadratic sorts.",
            keyTakeaway = "Few writes, always O(n²) comparisons.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-sorting-3",
            topicId = "sorting",
            order = 3,
            title = "Insertion sort",
            explanation = "Insertion sort grows a sorted prefix by taking the next value and sliding it " +
                "backwards into position. Nearly-sorted data needs almost no sliding, so it beats its " +
                "quadratic siblings in practice.",
            example = "Sorting a hand of cards is insertion sort: each card is placed against the " +
                "sorted cards already held.",
            keyTakeaway = "Small or nearly-sorted input: insertion sort wins.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-sorting-4",
            topicId = "sorting",
            order = 4,
            title = "Merge sort",
            explanation = "Merge sort splits the array to single elements and merges sorted halves " +
                "back together. Every level touches all n elements, and there are log n levels, so the " +
                "cost is always O(n log n).",
            example = "Merging [1,3] and [2,4] compares the fronts repeatedly until both halves drain.",
            keyTakeaway = "Predictable O(n log n) at the price of O(n) extra memory.",
            durationMinutes = 5,
            xpReward = 0,
        ),

        // ------------------------------------------------------ Linked Lists
        Lesson(
            id = "les-linked-lists-1",
            topicId = "linked-lists",
            order = 1,
            title = "Nodes and references",
            explanation = "A linked list is a chain of nodes, each holding a value and a reference to " +
                "the next. There is no index: reaching the fifth node means walking from the head five " +
                "times.",
            example = "head -> [10] -> [20] -> [30] -> null",
            keyTakeaway = "No arithmetic on positions — only walking.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-linked-lists-2",
            topicId = "linked-lists",
            order = 2,
            title = "Inserting at the head",
            explanation = "Pointing a new node at the current head and then updating the head is O(1). " +
                "The order matters: reverse it and the rest of the chain is lost.",
            example = "n.next = head; head = n; prepends without touching any other node.",
            keyTakeaway = "Link first, reassign second.",
            durationMinutes = 3,
            xpReward = 0,
        ),
        Lesson(
            id = "les-linked-lists-3",
            topicId = "linked-lists",
            order = 3,
            title = "Fast and slow pointers",
            explanation = "Two pointers moving at different speeds detect a cycle: if a cycle exists " +
                "the fast one eventually laps the slow one. The same pair finds the middle of a list in " +
                "one pass.",
            example = "slow moves one node, fast moves two; they meet inside a cycle or fast hits null.",
            keyTakeaway = "One pass, two pointers, cycle or midpoint found.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-linked-lists-4",
            topicId = "linked-lists",
            order = 4,
            title = "Reversing a list",
            explanation = "Reversal walks the chain once, redirecting each next pointer back to the " +
                "node before it. Three references — previous, current and next — keep you from losing " +
                "the tail of the chain.",
            example = "next = curr.next; curr.next = prev; prev = curr; curr = next;",
            keyTakeaway = "Save the next node before you overwrite the link.",
            durationMinutes = 5,
            xpReward = 0,
        ),

        // ----------------------------------------------------------- Stacks
        Lesson(
            id = "les-stacks-1",
            topicId = "stacks",
            order = 1,
            title = "LIFO discipline",
            explanation = "A stack serves the most recently added item first. Push adds to the top, " +
                "pop removes from it, and peek reads without removing — all in constant time.",
            example = "push(1), push(2), push(3) then pop() returns 3, then 2, then 1.",
            keyTakeaway = "Reach for a stack whenever order is 'most recent first'.",
            durationMinutes = 3,
            xpReward = 0,
        ),
        Lesson(
            id = "les-stacks-2",
            topicId = "stacks",
            order = 2,
            title = "Balanced parentheses",
            explanation = "Every opening bracket promises a matching close later. A stack records the " +
                "promises still outstanding, and each closing bracket either settles the most recent " +
                "one or proves the input is invalid.",
            example = "Push '(' on open, pop and compare on ')'; an empty pop means unbalanced.",
            keyTakeaway = "Nesting is a stack in disguise.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-stacks-3",
            topicId = "stacks",
            order = 3,
            title = "Monotonic stacks",
            explanation = "Keeping the stack in decreasing order answers 'next greater element' in one " +
                "pass. Each element is pushed and popped at most once, so the whole scan stays linear.",
            example = "For [2,1,4] the stack holds 4 after processing, having answered 1 and 2 instantly.",
            keyTakeaway = "A sorted stack turns a nested search into a single sweep.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-stacks-4",
            topicId = "stacks",
            order = 4,
            title = "Building a stack from an array",
            explanation = "An array plus a top index is the simplest stack there is. The array holds " +
                "the values, the index tracks the top, and growth is a matter of reallocating when the " +
                "capacity runs out.",
            example = "top starts at -1; push does data[++top] = value; pop returns data[top--].",
            keyTakeaway = "The structure is the contract — the storage is negotiable.",
            durationMinutes = 4,
            xpReward = 0,
        ),

        // ----------------------------------------------------------- Queues
        Lesson(
            id = "les-queues-1",
            topicId = "queues",
            order = 1,
            title = "FIFO scheduling",
            explanation = "A queue serves the oldest item first, which is exactly what fairness " +
                "demands: whoever arrived first gets served first. Enqueue adds at the back, dequeue " +
                "removes from the front.",
            example = "enqueue(1) enqueue(2) enqueue(3) then dequeue() returns 1, then 2, then 3.",
            keyTakeaway = "Order of arrival must survive: use a queue.",
            durationMinutes = 3,
            xpReward = 0,
        ),
        Lesson(
            id = "les-queues-2",
            topicId = "queues",
            order = 2,
            title = "Circular buffers",
            explanation = "Shifting every element on each dequeue wastes work. Wrapping the front and " +
                "rear indices around the end of a fixed array keeps both operations constant time.",
            example = "rear = (rear + 1) % capacity lets the queue reuse slots it already freed.",
            keyTakeaway = "Indices that wrap beat arrays that shift.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-queues-3",
            topicId = "queues",
            order = 3,
            title = "Breadth-first search",
            explanation = "Queuing a node's neighbours as soon as it is visited explores the graph one " +
                "hop at a time. Because every edge has the same weight in an unweighted graph, the " +
                "first time you reach a node is the shortest way there.",
            example = "Start at A, queue B and C; then visit B and queue D — level by level.",
            keyTakeaway = "FIFO gives shortest hop counts for free.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-queues-4",
            topicId = "queues",
            order = 4,
            title = "Double-ended queues",
            explanation = "A deque allows insertion and removal at both ends, which makes it the natural " +
                "home for sliding-window problems and for queues implemented with two stacks.",
            example = "For the maximum in every window, drop expired items from the front and smaller " +
                "rivals from the back.",
            keyTakeaway = "Two ends of access, one structure.",
            durationMinutes = 4,
            xpReward = 0,
        ),

        // ------------------------------------------------------------- Trees
        Lesson(
            id = "les-trees-1",
            topicId = "trees",
            order = 1,
            title = "Roots, nodes and leaves",
            explanation = "A tree is a node with children that are themselves trees. The root has no " +
                "parent, a leaf has no children, and every node has exactly one path back to the root.",
            example = "A binary tree gives each node at most two children: left and right.",
            keyTakeaway = "Every tree problem is a smaller tree problem one level down.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-trees-2",
            topicId = "trees",
            order = 2,
            title = "Preorder, inorder, postorder",
            explanation = "The three traversals differ only in when they visit the node relative to " +
                "its children. Inorder on a binary search tree emits the values in sorted order, which " +
                "is the property most solutions lean on.",
            example = "Inorder on 8 with children 3 and 10 gives 3, 8, 10.",
            keyTakeaway = "Inorder of a BST is its sorted sequence.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-trees-3",
            topicId = "trees",
            order = 3,
            title = "Height and depth",
            explanation = "Depth is the distance from the root to a node; height is the longest drop " +
                "from a node down to a leaf. Both are computed recursively from the heights of the " +
                "children.",
            example = "height(node) = 1 + max(height(left), height(right)), with a leaf at 0.",
            keyTakeaway = "Height looks down, depth looks up.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-trees-4",
            topicId = "trees",
            order = 4,
            title = "The BST ordering property",
            explanation = "In a binary search tree everything smaller sits left and everything larger " +
                "sits right. That single invariant turns insertion, deletion and lookup into guided " +
                "descents instead of scans.",
            example = "Searching 7 from the root compares, then follows only the branch that can hold it.",
            keyTakeaway = "Keep the invariant and search halves the tree each step.",
            durationMinutes = 5,
            xpReward = 0,
        ),

        // ------------------------------------------------------------ Graphs
        Lesson(
            id = "les-graphs-1",
            topicId = "graphs",
            order = 1,
            title = "Adjacency lists against matrices",
            explanation = "A matrix answers 'is there an edge' in constant time but always costs v x v " +
                "memory. A list stores only edges that exist, which is far cheaper for sparse graphs " +
                "at the cost of a scan per lookup.",
            example = "A road network with 10,000 cities and 30,000 roads needs the list, not the matrix.",
            keyTakeaway = "Sparse uses lists; dense can justify a matrix.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-graphs-2",
            topicId = "graphs",
            order = 2,
            title = "BFS against DFS",
            explanation = "Breadth-first explores neighbours in waves and finds the fewest hops; " +
                "depth-first dives along one branch and backtracks, which suits cycle detection and " +
                "path exploration.",
            example = "BFS keeps a queue; DFS keeps a stack, or the call stack itself.",
            keyTakeaway = "Shortest unweighted path: BFS. Exhaustive search: DFS.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-graphs-3",
            topicId = "graphs",
            order = 3,
            title = "Directed and undirected edges",
            explanation = "An undirected edge runs both ways and contributes to the degree of both " +
                "ends. A directed edge has a source and a target, so in-degrees and out-degrees are " +
                "counted separately.",
            example = "A one-way street is directed; a two-lane road is undirected.",
            keyTakeaway = "Check whether traversal is allowed both ways before counting edges.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-graphs-4",
            topicId = "graphs",
            order = 4,
            title = "Shortest path intuition",
            explanation = "With unweighted edges, BFS guarantees the first arrival is the shortest. " +
                "Once edges carry weights that guarantee breaks, and a greedy best-first strategy with " +
                "a priority queue becomes necessary.",
            example = "Unit weights: BFS. Arbitrary non-negative weights: Dijkstra.",
            keyTakeaway = "The weight of the edges picks the algorithm.",
            durationMinutes = 5,
            xpReward = 0,
        ),

        // ----------------------------------------------------------- Greedy
        Lesson(
            id = "les-greedy-1",
            topicId = "greedy",
            order = 1,
            title = "Local choice, global guarantee",
            explanation = "A greedy algorithm commits to the best-looking option right now and never " +
                "revisits it. That only works when the local choice can be shown not to damage the " +
                "global optimum — the proof is the whole game.",
            example = "Choosing the largest coin first works for some currencies and fails for others " +
                "with unusual denominations.",
            keyTakeaway = "Greedy without a correctness argument is just a guess.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-greedy-2",
            topicId = "greedy",
            order = 2,
            title = "Activity selection",
            explanation = "To fit the most activities into one room, repeatedly take the one that ends " +
                "earliest. Every choice leaves the maximum amount of room for everything that follows, " +
                "which is why the greedy rule holds.",
            example = "Sort by finish time, accept the first, then skip anything that starts earlier.",
            keyTakeaway = "Earliest finish time maximises what still fits.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-greedy-3",
            topicId = "greedy",
            order = 3,
            title = "Fractional knapsack",
            explanation = "When items can be split, take as much as possible of the best value per unit " +
                "of weight first. Ratios order the choices, and taking a fraction never hurts the total.",
            example = "Items of ratio 5, 3 and 1 are consumed in that order until the bag fills.",
            keyTakeaway = "Fractional admits ratios; 0/1 knapsack does not.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-greedy-4",
            topicId = "greedy",
            order = 4,
            title = "When greedy fails",
            explanation = "Some problems punish the short-sighted choice: taking the largest piece now " +
                "can block a much better combination later. Recognising that failure is what pushes you " +
                "towards dynamic programming instead.",
            example = "With coins of 1, 3 and 4, greedy on 6 picks 4+1+1 while 3+3 uses two coins.",
            keyTakeaway = "If a counterexample exists, switch to DP.",
            durationMinutes = 4,
            xpReward = 0,
        ),

        // --------------------------------------------- Dynamic Programming
        Lesson(
            id = "les-dp-1",
            topicId = "dp",
            order = 1,
            title = "Overlapping subproblems",
            explanation = "Dynamic programming earns its keep when a naive recursion recomputes the " +
                "same subproblem many times. Caching each result once turns exponential work into " +
                "linear or quadratic work.",
            example = "fib(5) needs fib(4) and fib(3), and fib(4) needs fib(3) again.",
            keyTakeaway = "Same subproblem twice means memoise it.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-dp-2",
            topicId = "dp",
            order = 2,
            title = "Optimal substructure",
            explanation = "A problem has optimal substructure when the best answer is built from the " +
                "best answers to smaller pieces. Without it, combining sub-answers can never produce " +
                "the global optimum.",
            example = "The shortest path from A to C passes through the shortest path from A to B.",
            keyTakeaway = "Sub-answers must themselves be optimal.",
            durationMinutes = 4,
            xpReward = 0,
        ),
        Lesson(
            id = "les-dp-3",
            topicId = "dp",
            order = 3,
            title = "Bottom-up tabulation",
            explanation = "Instead of recursing down and caching on the way back, fill a table from the " +
                "smallest case upwards. The order of the fill guarantees every value needed is already " +
                "computed.",
            example = "dp[0] and dp[1] are seeded, then dp[i] = dp[i-1] + dp[i-2] for i >= 2.",
            keyTakeaway = "Top-down stores results; bottom-up schedules them.",
            durationMinutes = 5,
            xpReward = 0,
        ),
        Lesson(
            id = "les-dp-4",
            topicId = "dp",
            order = 4,
            title = "Designing the state",
            explanation = "Before writing a transition, decide what one table cell means. The state " +
                "must capture every fact the next decision depends on — no more, or the table explodes; " +
                "no less, or the answer is wrong.",
            example = "For a capacity-limited bag, dp[i][w] is the best value using the first i items " +
                "with room w.",
            keyTakeaway = "Name the cell before you write the recurrence.",
            durationMinutes = 6,
            xpReward = 0,
        ),
    )

    val lessons: List<Lesson> = baseLessons.map { it.copy(xpReward = 20) }

    // ------------------------------------------------------------ questions

    /**
     * The ten-level DSA question bank, built once from data/questions.
     *
     * Levels 1-5 stay conceptual; 6-10 move into code reading, completion,
     * debugging, implementation and optimisation.
     */
    val questions: List<PracticeQuestion> = QuestionBank.all

    /** Questions already solved when the app first opens. */
    val solvedQuestionIds: Set<String> = setOf(
        "foundations-l1-bigo",
        "foundations-l2-bigo",
        "arrays-l1-indexing",
    )

    /**
     * The bank bucketed by topic, computed once at init. Rebuilding the full
     * topic list runs after every answer, so per-topic lookups skip the scan.
     */
    private val questionsByTopic: Map<String, List<PracticeQuestion>> =
        questions.groupBy { it.topicId }

    /** Questions inside one roadmap topic, for derived counters. */
    fun questionsInTopic(topicId: String): List<PracticeQuestion> =
        questionsByTopic[topicId].orEmpty()

    // ------------------------------------------------------------ topic model

    fun topics(
        lessonList: List<Lesson> = lessons,
        solvedIds: Set<String> = solvedQuestionIds,
    ): List<DsaTopic> {
        val lessonsByTopic = lessonList.groupBy { it.topicId }
        return topicBlueprints.map { blueprint ->
            val topicLessons = lessonsByTopic[blueprint.id].orEmpty()
            val topicQuestions = questionsByTopic[blueprint.id].orEmpty()
            val lessonsCompleted = topicLessons.count { it.isCompleted }
            val questionsSolved = topicQuestions.count { it.id in solvedIds }
            val totalUnits = topicLessons.size + topicQuestions.size
            val interactive = blueprint.id in interactiveTopicIds

            DsaTopic(
                id = blueprint.id,
                name = blueprint.name,
                description = blueprint.description,
                order = blueprint.order,
                isInteractive = interactive,
                difficultyLabel = blueprint.difficultyLabel,
                difficultyDots = blueprint.difficultyDots,
                lessonCount = topicLessons.size,
                lessonsCompleted = if (interactive) lessonsCompleted else 0,
                questionCount = topicQuestions.size,
                questionsSolved = if (interactive) questionsSolved else 0,
                progress = if (!interactive || totalUnits == 0) {
                    0f
                } else {
                    (lessonsCompleted + questionsSolved).toFloat() / totalUnits
                },
            )
        }
    }

    // ----------------------------------------------------------- user progress

    fun userProgress(): UserProgress {
        val ordered = orderedLessons()
        val solved = questions.filter { it.id in solvedQuestionIds }
        val interactive = topics().filter { it.isInteractive }

        return UserProgress(
            displayName = "Shekhar",
            greeting = greetingFor("Shekhar"),
            level = 7,
            totalXp = 1240,
            xpIntoLevel = 740,
            xpLevelCap = 1000,
            streakDays = 6,
            dailyChallengeCompleted = false,
            dailyChallengeXp = 100,
            lessonsCompleted = lessons.count { it.isCompleted },
            questionsSolved = solved.size,
            easySolved = solved.count { it.difficulty == Difficulty.EASY },
            mediumSolved = solved.count { it.difficulty == Difficulty.MEDIUM },
            hardSolved = solved.count { it.difficulty == Difficulty.HARD },
            dsaProgress = if (interactive.isEmpty()) {
                0f
            } else {
                interactive.map { it.progress }.average().toFloat()
            },
            continueLesson = ordered.firstOrNull { !it.isCompleted },
            // Same answer as scanning the bank again: the filtered pass keeps
            // bank order, so its last element is the most recently solved.
            recentlySolvedTitle = solved.lastOrNull()?.question,
        )
    }

    /** Lessons in roadmap order, so "continue" always follows the curriculum. */
    fun orderedLessons(lessonList: List<Lesson> = lessons): List<Lesson> {
        val topicOrder = topicBlueprints.associate { it.id to it.order }
        return lessonList.sortedWith(
            compareBy({ topicOrder[it.topicId] ?: Int.MAX_VALUE }, { it.order }),
        )
    }

    fun greetingFor(name: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val part = when (hour) {
            in 5..11 -> "morning"
            in 12..16 -> "afternoon"
            else -> "evening"
        }
        return "Good $part, $name \uD83D\uDC4B"
    }
}
