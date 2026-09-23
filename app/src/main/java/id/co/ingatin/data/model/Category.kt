package id.co.ingatin.data.model

import com.google.firebase.firestore.DocumentId

data class CategoryDto(
    @DocumentId
    val categoryId: String = "",
    val name: String = ""
)

data class CategorySummary(
    val name: String,
    val count: Int
)

data class CategoryCount(
    val allTask: Int = 0,
    val categories: List<CategorySummary>
)

data class Category(
    val id: String = "",
    val name: String = ""
)