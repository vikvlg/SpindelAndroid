package ru.vik.spindel.feature.brewing.details

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.vik.spindel.core.brews.api.Brew

@Composable
fun BrewDetailsScreen(brewId: Long, onShowChart: () -> Unit) {
    Column {
        Text(
            text = "Варка: $brewId"
        )
        Button(
            onClick = onShowChart
        ) {
            Text(
                text = "График"
            )
        }
    }
}

//@Composable
//fun BrewDetailsScreen(brew: Brew, onShowChart: () -> Unit) {
//    Column {
//        Text(
//            text = "Варка: ${brew.name}"
//        )
//        Text(
//            text = "Дата: ${brew.date}"
//        )
//        Button(
//            onClick = onShowChart
//        ) {
//            Text(
//                text = "График"
//            )
//        }
//    }
//}
