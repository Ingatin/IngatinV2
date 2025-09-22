package id.co.brainy.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ButtonCategory(
    btnTitle: String,
    onCategoryClick: (String) -> Unit,
    isSelected: Boolean
    ){

    val buttonColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    }else{
        MaterialTheme.colorScheme.primary
    }

    val textColor = if (isSelected) {
        Color.Gray // Warna teks saat dipilih
    } else {
        Color.White // Warna teks default
    }

    Button(
        onClick = {
            onCategoryClick(btnTitle)

        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(
            text = btnTitle,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = textColor,
            modifier = Modifier.padding(6.dp),
        )
    }
}