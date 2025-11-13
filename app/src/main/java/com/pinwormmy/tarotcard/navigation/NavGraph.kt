package com.pinwormmy.tarotcard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pinwormmy.tarotcard.data.TarotRepository
import com.pinwormmy.tarotcard.ui.screens.CardDetailScreen
import com.pinwormmy.tarotcard.ui.screens.HomeScreen

@Composable
fun TarotNavGraph(
    repository: TarotRepository
) {
    val navController = rememberNavController()
    val cards = repository.getCards()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                cards = cards,
                onCardRevealed = { card ->
                    navController.navigate(Screen.CardDetail.createRoute(card.id))
                }
            )
        }

        composable(
            route = Screen.CardDetail.route,
            arguments = listOf(navArgument("cardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            val card = remember(cardId) { repository.getCard(cardId) }
            CardDetailScreen(
                card = card,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object CardDetail : Screen("card_detail/{cardId}") {
        fun createRoute(cardId: String) = "card_detail/$cardId"
    }
}
