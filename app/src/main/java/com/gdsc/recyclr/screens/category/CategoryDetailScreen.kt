package com.gdsc.recyclr.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.design.CurvedHeaderShape
import com.gdsc.recyclr.components.preferences.ThemeToggleIconButton
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors

private data class CategoryContent(
    val recyclableItems: List<String>,
    val nonRecyclableItems: List<String>,
    val factBody: String,
)

private val categoryContent = mapOf(
    "plastic" to CategoryContent(
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
        factBody = "A pair of jeans could include an average of 8 recycled plastic bottles.",
    ),
    "paper" to CategoryContent(
        recyclableItems = listOf(
            "Newspapers and magazines",
            "Cardboard boxes",
            "Office paper and envelopes",
        ),
        nonRecyclableItems = listOf(
            "Greasy pizza boxes",
            "Waxed or laminated paper",
        ),
        factBody = "Recycling one ton of paper can save about 17 trees.",
    ),
    "glass" to CategoryContent(
        recyclableItems = listOf(
            "Clear and colored glass bottles",
            "Glass jars for food",
        ),
        nonRecyclableItems = listOf(
            "Window glass",
            "Mirrors and ceramics",
        ),
        factBody = "Glass can be recycled endlessly without losing quality.",
    ),
    "metal" to CategoryContent(
        recyclableItems = listOf(
            "Aluminum cans",
            "Steel food tins",
        ),
        nonRecyclableItems = listOf(
            "Paint cans with residue",
            "Aerosol cans that are not empty",
        ),
        factBody = "Recycling aluminum saves up to 95% of the energy needed to make new metal.",
    ),
    "textile" to CategoryContent(
        recyclableItems = listOf(
            "Clean clothing in good condition",
            "Shoes paired together",
        ),
        nonRecyclableItems = listOf(
            "Wet or moldy textiles",
            "Heavily soiled workwear",
        ),
        factBody = "Donating textiles keeps materials in use and reduces landfill waste.",
    ),
)

private data class CategoryDetailLayout(
    val screenPaddingH: Dp,
    val headerHeight: Dp,
    val cardOverlap: Dp,
    val heroIconSize: Dp,
    val titleSp: TextUnit,
    val subtitleSp: TextUnit,
    val sectionTitleSp: TextUnit,
    val bodySp: TextUnit,
    val cardInnerPaddingH: Dp,
    val cardInnerPaddingV: Dp,
    val sectionSpacing: Dp,
)

@Composable
private fun rememberCategoryDetailLayout(): CategoryDetailLayout {
    val widthDp = LocalConfiguration.current.screenWidthDp.coerceIn(280, 960)
    return remember(widthDp) {
        val t = ((widthDp - 280f) / 680f).coerceIn(0f, 1f)
        CategoryDetailLayout(
            screenPaddingH = (12f + 12f * t).dp,
            headerHeight = (152f + 48f * t).dp,
            cardOverlap = (28f + 12f * t).dp,
            heroIconSize = (44f + 16f * t).dp,
            titleSp = (22f + 6f * t).sp,
            subtitleSp = (13f + 1.5f * t).sp,
            sectionTitleSp = (15f + 2f * t).sp,
            bodySp = (14f + 1f * t).sp,
            cardInnerPaddingH = (16f + 8f * t).dp,
            cardInnerPaddingV = (16f + 8f * t).dp,
            sectionSpacing = (14f + 8f * t).dp,
        )
    }
}

@Composable
private fun categoryHeroBackground(categoryKey: String): Color = when (categoryKey) {
    "plastic" -> RecyclrThemeColors.categoryPlastic
    "paper" -> RecyclrThemeColors.categoryPaper
    "glass" -> RecyclrThemeColors.categoryGlass
    "metal" -> RecyclrThemeColors.categoryMetal
    "textile" -> RecyclrThemeColors.categoryTextile
    else -> RecyclrThemeColors.headerBackground
}

@Composable
private fun categoryHeroIconTint(categoryKey: String): Color = when (categoryKey) {
    "plastic" -> RecyclrThemeColors.categoryPlasticIcon
    "paper" -> RecyclrThemeColors.categoryPaperIcon
    "glass" -> RecyclrThemeColors.categoryGlassIcon
    "metal" -> RecyclrThemeColors.categoryMetalIcon
    "textile" -> RecyclrThemeColors.categoryTextileIcon
    else -> MaterialTheme.colorScheme.onPrimaryContainer
}

private fun categoryHeroIcon(categoryKey: String): ImageVector = when (categoryKey) {
    "plastic" -> Icons.Default.LocalDrink
    "paper" -> Icons.Default.Newspaper
    "glass" -> Icons.Default.WineBar
    "metal" -> Icons.Default.LocalDrink
    "textile" -> Icons.Default.Checkroom
    else -> Icons.Outlined.Recycling
}

private fun readableOnCategoryHeader(background: Color): Color =
    if (background.luminance() > 0.52f) Color(0xE6000000) else Color(0xF2FFFFFF)

@Composable
fun CategoryDetailScreen(
    categoryKey: String,
    onBack: () -> Unit,
) {
    val key = categoryKey.ifBlank { "plastic" }
    val content = categoryContent[key] ?: categoryContent.getValue("plastic")
    val localizedTitle = categoryTitle(key)
    val subtitle = categorySubtitle(key)
    val layout = rememberCategoryDetailLayout()
    val scheme = MaterialTheme.colorScheme
    val headerBg = categoryHeroBackground(key)
    val onHeader = readableOnCategoryHeader(headerBg)
    val iconTint = categoryHeroIconTint(key)
    val heroIcon = categoryHeroIcon(key)

    val cardTop = layout.headerHeight - layout.cardOverlap

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
        ) {
            CurvedHeaderShape(
                color = headerBg,
                height = layout.headerHeight,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = layout.screenPaddingH, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = scheme.surface.copy(alpha = 0.92f),
                        contentColor = scheme.onSurface,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_navigate_up),
                    )
                }
                ThemeToggleIconButton(
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = layout.screenPaddingH,
                        end = layout.screenPaddingH,
                        top = 52.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    shape = CircleShape,
                    color = scheme.surface.copy(alpha = 0.22f),
                    tonalElevation = 0.dp,
                ) {
                    Icon(
                        imageVector = heroIcon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(layout.heroIconSize),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = localizedTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = layout.titleSp,
                    color = onHeader,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = layout.subtitleSp,
                    color = onHeader.copy(alpha = 0.88f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = cardTop,
                    start = layout.screenPaddingH,
                    end = layout.screenPaddingH,
                    bottom = 0.dp,
                ),
            shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
            color = scheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 3.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(
                        horizontal = layout.cardInnerPaddingH,
                        vertical = layout.cardInnerPaddingV,
                    ),
                verticalArrangement = Arrangement.spacedBy(layout.sectionSpacing),
            ) {
                CategoryBulletCard(
                    title = stringResource(R.string.category_recyclable_title, localizedTitle.lowercase()),
                    items = content.recyclableItems,
                    leadingIcon = Icons.Outlined.Recycling,
                    containerColor = scheme.primaryContainer.copy(alpha = 0.45f),
                    contentColor = scheme.onPrimaryContainer,
                    accentIconTint = scheme.primary,
                    sectionTitleSp = layout.sectionTitleSp,
                    bodySp = layout.bodySp,
                )
                CategoryBulletCard(
                    title = stringResource(R.string.category_not_recyclable_title, localizedTitle.lowercase()),
                    items = content.nonRecyclableItems,
                    leadingIcon = Icons.Outlined.Block,
                    containerColor = scheme.errorContainer.copy(alpha = 0.35f),
                    contentColor = scheme.onErrorContainer,
                    accentIconTint = scheme.error,
                    sectionTitleSp = layout.sectionTitleSp,
                    bodySp = layout.bodySp,
                )
                CategoryFactCard(
                    title = stringResource(R.string.category_fact_title, localizedTitle.lowercase()),
                    body = content.factBody,
                    sectionTitleSp = layout.sectionTitleSp,
                    bodySp = layout.bodySp,
                )
            }
        }
    }
}

@Composable
private fun CategoryBulletCard(
    title: String,
    items: List<String>,
    leadingIcon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    accentIconTint: Color,
    sectionTitleSp: TextUnit,
    bodySp: TextUnit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accentIconTint.copy(alpha = 0.18f),
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = accentIconTint,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = sectionTitleSp,
                    color = contentColor,
                )
                items.forEachIndexed { index, item ->
                    if (index > 0) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = contentColor.copy(alpha = 0.12f),
                        )
                    }
                    Text(
                        text = "• $item",
                        fontSize = bodySp,
                        lineHeight = (bodySp.value * 1.45f).sp,
                        color = contentColor.copy(alpha = 0.92f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFactCard(
    title: String,
    body: String,
    sectionTitleSp: TextUnit,
    bodySp: TextUnit,
) {
    val scheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = scheme.secondaryContainer.copy(alpha = 0.55f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = sectionTitleSp,
                color = scheme.onSecondaryContainer,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                scheme.primary,
                                scheme.tertiary,
                            ),
                        ),
                    ),
            )
            Text(
                text = body,
                fontSize = bodySp,
                lineHeight = (bodySp.value * 1.5f).sp,
                color = scheme.onSecondaryContainer.copy(alpha = 0.92f),
                modifier = Modifier.fillMaxWidth(),
            )
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
private fun categorySubtitle(categoryKey: String): String = when (categoryKey) {
    "plastic" -> stringResource(R.string.category_plastic_subtitle)
    "paper" -> stringResource(R.string.category_paper_subtitle)
    "glass" -> stringResource(R.string.category_glass_subtitle)
    "metal" -> stringResource(R.string.category_metal_subtitle)
    "textile" -> stringResource(R.string.category_textile_subtitle)
    else -> stringResource(R.string.category_plastic_subtitle)
}
