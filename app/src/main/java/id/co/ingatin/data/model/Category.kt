package id.co.ingatin.data.model

import com.google.firebase.firestore.DocumentId

data class CategoryDto(
    @DocumentId
    val categoryId: String = "",
    val name: String
)