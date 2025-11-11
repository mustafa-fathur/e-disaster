package com.example.e_disaster.ui.features.disaster.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun ImageSlider(images: List<Int>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Disaster Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { index ->
                val color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .size(8.dp)
                        .background(color)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Image Slider")
@Composable
fun ImageSliderPreview() {
    EDisasterTheme{
        val images = listOf(
            com.example.e_disaster.R.drawable.placeholder,
            com.example.e_disaster.R.drawable.placeholder,
            com.example.e_disaster.R.drawable.placeholder
        )
        ImageSlider(images = images)
    }
}
