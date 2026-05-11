package com.gdsc.recyclr.screens.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.ui.theme.LeafGreen

@Composable
fun ShopItemCard(
    shopItem: ShopItem,
    onRedeem: (ShopItem) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White,
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = shopItem.imageUrl.ifBlank { null }),
                contentDescription = null,
                modifier = Modifier.size(88.dp),
            )
            Text(
                text = shopItem.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${shopItem.price} ${stringResource(R.string.coins)}", fontSize = 12.sp)
                Image(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(15.dp),
                    painter = painterResource(R.drawable.coins),
                    contentDescription = "coins",
                )
            }
            Button(
                onClick = { onRedeem(shopItem) },
                colors = ButtonDefaults.buttonColors(backgroundColor = LeafGreen),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.redeem),
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
