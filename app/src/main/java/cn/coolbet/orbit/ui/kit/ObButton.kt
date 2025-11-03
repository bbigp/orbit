package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun ObButton(){

}

@Preview(showBackground = true)
@Composable
fun PreviewButton() {
    Column {
        Button(onClick = {}) { }
        TextButton(onClick = {}) { }
        IconButton(onClick = {}) { }
    }
}
