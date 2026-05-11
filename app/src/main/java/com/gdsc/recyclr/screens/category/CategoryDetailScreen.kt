package com.gdsc.recyclr.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.design.CurvedHeaderShape
import com.gdsc.recyclr.components.preferences.ThemeToggleIconButton
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors
import com.gdsc.recyclr.ui.theme.recyclrScreenBackground

private data class CategoryContent(
    val title: String,
    val recyclableItems: List<String>,
    val nonRecyclableItems: List<String>,
    val factTitle: String,
    val factBody: String,
)

private val categoryContent = mapOf(
    "plastic" to CategoryContent(
        title = "Plastics",
        recyclableItems = listOf(
            "Clear and colored plastic bottles",
            "Cleaning product bottles",
            "Detergent and shampoo bottles",
            "Milk and juice bottles",
        ),
        nonRecyclableItems = listOf(
            "Bottles that contained chemicals",
            "Crushed or heavily soiled containers",
            "Plastic bags and film",
        ),
        factTitle = "Plastic recycling facts",
        factBody = "A pair of jeans could include an average of 8 recycled plastic bottles.",
    ),
    "paper" to CategoryContent(
        title = "Paper",
        recyclableItems = listOf(
            "Newspapers and magazines",
            "Cardboard boxes",
            "Office paper and envelopes",
        ),
        nonRecyclableItems = listOf(
            "Greasy pizza boxes",
            "Waxed or laminated paper",
        ),
        factTitle = "Paper recycling facts",
        factBody = "Recycling one ton of paper can save about 17 trees.",
    ),
    "glass" to CategoryContent(
        title = "Glass",
        recyclableItems = listOf(
            "Clear and colored glass bottles",
            "Glass jars for food",
        ),
        nonRecyclableItems = listOf(
            "Window glass",
            "Mirrors and ceramics",
        ),
        factTitle = "Glass recycling facts",
        factBody = "Glass can be recycled endlessly without losing quality.",
    ),
    "metal" to CategoryContent(
        title = "Metal",
        recyclableItems = listOf(
            "Aluminum cans",
            "Steel food tins",
        ),
        nonRecyclableItems = listOf(
            "Paint cans with residue",
            "Aerosol cans that are not empty",
        ),
        factTitle = "Metal recycling facts",
        factBody = "Recycling aluminum saves up to 95% of the energy needed to make new metal.",
    ),
    "textile" to CategoryContent(
        title = "Textile",
        recyclableItems = listOf(
            "Clean clothing in good condition",
            "Shoes paired together",
        ),
        nonRecyclableItems = listOf(
            "Wet or moldy textiles",
            "Heavily soiled workwear",
        ),
        factTitle = "Textile recycling facts",
        factBody = "Donating textiles keeps materials in use and reduces landfill waste.",
    ),
)

@Composable
fun CategoryDetailScreen(
    categoryKey: String,
    onBack: () -> Unit,
) {
    val content = categoryContent[categoryKey] ?: categoryContent.getValue("plastic")
    val localizedTitle = categoryTitle(categoryKey)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(recyclrScreenBackground()),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            CurvedHeaderShape(color = RecyclrThemeColors.headerBackground, height = 180.dp)
            ThemeToggleIconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = null,
                        tint = Color(0xFF4A4A4A),
                        modifier = Modifier.height(42.dp),
                    )
                    Text(text = localizedTitle, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 170.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                BulletSection(
                    title = stringResource(R.string.category_recyclable_title, localizedTitle.lowercase()),
                    items = content.recyclableItems,
                )
                BulletSection(
                    title = stringResource(R.string.category_not_recyclable_title, localizedTitle.lowercase()),
                    items = content.nonRecyclableItems,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.category_fact_title, localizedTitle.lowercase()),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFD6E8F8), RoundedCornerShape(60.dp)),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = content.factBody,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun categoryTitle(categoryKey: String): String = when (categoryKey) {
    "plastic" -> stringResource(R.string.category_title_plastics)
    "paper" -> stringResource(R.string.category_title_paper)
    "glass" -> stringResource(R.string.category_title_glass)
    "metal" -> stringResource(R.string.category_title_metal)
    "textile" -> stringResource(R.string.category_title_textile)
    else -> stringResource(R.string.category_title_plastics)
}

@Composable
private fun BulletSection(title: String, items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        items.forEach { item ->
            Text(text = "• $item", fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}
