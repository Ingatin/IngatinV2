package id.co.brainy.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import id.co.brainy.R

@Composable
fun CustomTextField(
    values: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    contentDescription: String,
    keyboardType: KeyboardType,
    isPasswordField: Boolean = false,
    enabled: Boolean = true,
) {

    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = values,
        onValueChange = {
            onValueChange(it)
        },
        placeholder = {
            Text(
                text = placeholder,
                color = Color.LightGray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.LightGray
            )
        },
        trailingIcon = {
            if (isPasswordField) {
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible }
                ) {
                    Icon(
                        painter = painterResource(
                            if (isPasswordVisible)
                                R.drawable.baseline_visibility_24
                            else
                                R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = "Toggle Password Visibility",
                        tint = Color.LightGray
                    )
                }
            }
        },
        visualTransformation = if (isPasswordField && !isPasswordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        enabled = enabled

        )
}