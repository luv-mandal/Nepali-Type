package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "About Nepali Type",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "“English ma type gara, Nepali ma lekha.”",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                )

                Text(
                    text = "Nepali Type is a specialized Roman Nepali to Devanagari transliteration tool designed specifically for Nepali users' everyday messaging, homework, and social media.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Nepali Typing Shortcuts:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )

                val tips = listOf(
                    "• cha / chha → छ",
                    "• xaina / chhaina → छैन",
                    "• garxu / garchu → गर्छु",
                    "• bhayo / vayo → भयो",
                    "• maile / mayle → मैले",
                    "• tapai lai → तपाईंलाई",
                    "• school ko → स्कूलको",
                    "• .. or | → । (Nepali Danda)"
                )
                tips.forEach { tip ->
                    Text(text = tip, style = MaterialTheme.typography.bodySmall)
                }

                HorizontalDivider()

                Text(
                    text = "Privacy & Offline Mode:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "All typing and dictionary conversions run 100% offline on your device. Your personal texts are never saved or sent to any server.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Got It")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
