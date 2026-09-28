package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VirtualKeyboard(
    onInsertText: (String) -> Unit,
    onBackspace: () -> Unit,
    onOpenEmoji: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isNumberMode by remember { mutableStateOf(false) }

    val matras = listOf("ा", "ि", "ी", "ु", "ू", "े", "ै", "ो", "ौ", "ं", "ँ", "्", "।", "॥")
    val quickWords = listOf("म", "मेरो", "मलाई", "तपाईं", "तपाईंलाई", "छ", "छैन", "हो", "जान्छु", "पर्छ")

    val qwertyRow1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val qwertyRow2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val qwertyRow3 = listOf("z", "x", "c", "v", "b", "n", "m")

    val numRow1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val nepaliNumRow1 = listOf("१", "२", "३", "४", "५", "६", "७", "८", "९", "०")
    val symRow2 = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/")
    val symRow3 = listOf("*", "\"", "'", ":", ";", "!", "?")

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Quick Particles / Words Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickWords.forEach { word ->
                    Surface(
                        onClick = { onInsertText(" $word ") },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = word,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Matras & Punctuation row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                matras.forEach { matra ->
                    Surface(
                        onClick = { onInsertText(matra) },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .height(34.dp)
                            .widthIn(min = 34.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            Text(
                                text = matra,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Keyboard Keys Rows
            if (!isNumberMode) {
                // Row 1
                KeyRow(keys = qwertyRow1, onKeyClick = onInsertText)
                // Row 2
                KeyRow(keys = qwertyRow2, onKeyClick = onInsertText)
                // Row 3 with Backspace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    qwertyRow3.forEach { key ->
                        KeyButton(
                            text = key,
                            onClick = { onInsertText(key) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Backspace key
                    Surface(
                        onClick = onBackspace,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(42.dp)
                            .testTag("kb_backspace_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Backspace",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            } else {
                // Numbers and Symbols mode
                KeyRow(keys = nepaliNumRow1, onKeyClick = onInsertText)
                KeyRow(keys = numRow1, onKeyClick = onInsertText)
                KeyRow(keys = symRow2, onKeyClick = onInsertText)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    symRow3.forEach { key ->
                        KeyButton(
                            text = key,
                            onClick = { onInsertText(key) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Surface(
                        onClick = onBackspace,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Backspace",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Bottom control row: 123 | Emoji | Space | Nepali Danda । | Done
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 123 / ABC Switcher
                Surface(
                    onClick = { isNumberMode = !isNumberMode },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .height(42.dp)
                        .weight(1.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isNumberMode) "ABC" else "?1२३",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Emoji Button
                IconButton(
                    onClick = onOpenEmoji,
                    modifier = Modifier
                        .height(42.dp)
                        .weight(0.9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEmotions,
                        contentDescription = "Emojis",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Spacebar
                Surface(
                    onClick = { onInsertText(" ") },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .height(42.dp)
                        .weight(3.5f)
                        .testTag("kb_space_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "space",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                }

                // Nepali Danda Key
                Surface(
                    onClick = { onInsertText("। ") },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .height(42.dp)
                        .weight(1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "।",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyRow(
    keys: List<String>,
    onKeyClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { key ->
            KeyButton(
                text = key,
                onClick = { onKeyClick(key) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KeyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.height(42.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
