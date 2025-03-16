val nums = Vector(1, 2, 3, 4, 5)

// Basic Operations
println(nums.head) // First element
println(nums.last) // Last element
println(nums.tail) // All except first
println(nums.init) // All except last
println(nums.length) // Length of vector
println(nums.isEmpty) // Check if empty
println(nums.nonEmpty) // Check if non-empty

// Accessing elements
println(nums(2)) // Access element at index 2
println(nums.indexOf(3)) // Get index of element
println(nums.contains(4)) // Check if element exists

// Modifications
val updatedNums = nums.updated(1, 10)
println(updatedNums) // Update index 1 to value 10

val appended = nums :+ 6 // Append 6
println(appended)

val prepended = 0 +: nums // Prepend 0
println(prepended)

val concatenated = nums ++ Vector(6, 7)
println(concatenated)

// Slicing and dropping elements
println(nums.drop(2)) // Drop first 2 elements
println(nums.take(3)) // Take first 3 elements
println(nums.slice(1, 4)) // Slice from index 1 to 3

// Transformations
val squared = nums.map(x => x * x)
println(squared)

val filtered = nums.filter(_ % 2 == 0)
println(filtered)

val flatMapped = nums.flatMap(x => Vector(x, x * 2))
println(flatMapped)

// Reductions
println(nums.sum) // Sum of elements
println(nums.product) // Product of elements
println(nums.min) // Minimum element
println(nums.max) // Maximum element
println(nums.reduce(_ + _)) // Reduce by addition

// Grouping and partitioning
val grouped = nums.groupBy(_ % 2)
println(grouped)

val (evens, odds) = nums.partition(_ % 2 == 0)
println(evens, odds)

// Sorting
val sortedAsc = nums.sorted
println(sortedAsc)

val sortedDesc = nums.sortWith(_ > _)
println(sortedDesc)
