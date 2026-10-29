package com.kanzan.feature.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

/**
 * Onboarding screen for first-time users
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState()
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to Kanzan",
            description = "Record and replay touch gestures automatically"
        ),
        OnboardingPage(
            title = "Enable Accessibility",
            description = "Grant accessibility permission to allow gesture recording"
        ),
        OnboardingPage(
            title = "Auto Click",
            description = "Set up single or multi-target auto-clicking"
        ),
        OnboardingPage(
            title = "Ready!",
            description = "Tap Get Started to begin"
        )
    )
    
    Scaffold(
        bottomBar = {
            Surface(
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        TextButton(
                            onClick = {
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        ) {
                            Text("Back")
                        }
                    } else {
                        Spacer(Modifier.width(1.dp))
                    }
                    
                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Button(
                        onClick = {
                            if (pagerState.currentPage == pages.size - 1) {
                                onComplete()
                            } else {
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                    ) {
                        Text(
                            if (pagerState.currentPage == pages.size - 1) "Get Started"
                            else "Next"
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            OnboardingPageContent(pages[page])
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = page.title,
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class OnboardingPage(
    val title: String,
    val description: String
)
