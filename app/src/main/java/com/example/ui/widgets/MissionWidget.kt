package com.example.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.Mission
import com.example.ui.theme.*

@Composable
fun MissionWidget(
    mission: Mission,
    isCompleted: Boolean,
    onCompleteToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onWidgetClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current

    val diffColor = when (mission.difficulty) {
        "Easy" -> EmeraldGreen
        "Medium" -> RetroCyan
        "Hard" -> QuestAmber
        else -> ElectricIndigo
    }

    val categoryIcon = getCategoryIcon(mission.category)

    val cardBorder = if (isCompleted) {
        BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.3f))
    } else {
        BorderStroke(1.dp, Color(0xFF1E293B))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mission_widget_${mission.id}")
            .let {
                if (onWidgetClick != null) {
                    it.clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onWidgetClick()
                    }
                } else {
                    it
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) SlateCardBackground.copy(alpha = 0.5f) else SlateCardBackground
        ),
        shape = RoundedCornerShape(16.dp),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(diffColor.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = mission.category,
                    tint = diffColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mission.category.uppercase(),
                        color = diffColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Difficulty Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(diffColor.copy(alpha = 0.12f))
                            .border(0.5.dp, diffColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = mission.difficulty.uppercase(),
                            color = diffColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${mission.repeatType}",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = mission.title,
                    color = if (isCompleted) TextGray else TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // XP Reward
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "XP Icon",
                        tint = XPBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${mission.xpReward} XP",
                        color = XPBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Completion check circle button
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCompleteToggle()
                },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("mission_widget_toggle_${mission.id}")
            ) {
                if (isCompleted) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color(0xFF05070A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .border(2.dp, ElectricIndigo, CircleShape)
                    )
                }
            }
        }
    }
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Programming" -> Icons.Default.Code
        "Linux" -> Icons.Default.Terminal
        "Basketball" -> Icons.Default.SportsBasketball
        "Fitness" -> Icons.Default.FitnessCenter
        "School" -> Icons.Default.School
        "Reading" -> Icons.Default.MenuBook
        "Health" -> Icons.Default.LocalHospital
        "Life" -> Icons.Default.SelfImprovement
        else -> Icons.Default.RocketLaunch
    }
}
