package id.co.ingatin.data.model

import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun TaskDto.toDomain(): Task {
    val zonedDateTime = dueDate.toDate().toInstant().atZone(ZoneId.systemDefault())

    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    return Task(
        id = taskId,
        title = title,
        description = description,
        category = category,
        date = zonedDateTime.format(dateFormatter),
        time = zonedDateTime.format(timeFormatter)
    )
}


fun List<TaskDto>.toCategoryCount(): CategoryCount {
    val categories = this.groupingBy { it.category }
        .eachCount()
        .entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .take(2)
        .map { CategorySummary(name = it.key, count = it.value) }
    return CategoryCount(allTask = this.size, categories = categories)
}


fun CategoryDto.toCategory(): Category{
    return Category(
        id = categoryId,
        name = name
    )
}