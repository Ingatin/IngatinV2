package id.co.ingatin.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ButtonCategory(
    btnTitle: String,
    onCategoryClick: (String) -> Unit,
    isSelected: Boolean,
    onLongClick: (() -> Unit)? = null
) {

    val buttonColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    }

    val textColor = if (isSelected) {
        Color.Gray // Warna teks saat dipilih
    } else {
        Color.White // Warna teks default
    }

    Surface(
        modifier = Modifier
            .height(40.dp)
            .combinedClickable(
                onClick = { onCategoryClick(btnTitle) },
                onLongClick = { onLongClick?.invoke() }
            ),
        shape = RoundedCornerShape(12.dp),
        color = buttonColor,
        contentColor = textColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = btnTitle,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}
