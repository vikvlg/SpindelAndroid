package ru.vik.spindel.feature.rawdata

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

object BrewPickerDefaults {
    const val RecentCount = 3
}

/** Поле с выбором варки. **/
@Composable
fun BrewSelectorTitle(
    title: String,
    subtitle: String?,
    isPlaceholder: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrowRotation",
    )

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                onClickLabel = stringResource(R.string.rawdata_selector_action),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f, fill = false)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = if (isPlaceholder) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.rotate(arrowRotation),
        )
    }
}

/** Всплывающий диалог с поиском/выбором варки. **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewPickerSheet(
    brews: List<BrewUi>,          // отсортированы по дате, свежие сверху
    selection: BrewSelection,
    unassignedCount: Int,
    onSelect: (BrewSelection) -> Unit,
    onDismiss: () -> Unit,
    recentCount: Int = BrewPickerDefaults.RecentCount,
) {
    // Сразу на всю высоту: недавние видны без прокрутки
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var query by rememberSaveable { mutableStateOf("") }
    val q = query.trim()

    val recent = remember(brews, recentCount) { brews.take(recentCount.coerceAtLeast(0)) }
    val (active, finished) = remember(brews, recentCount) {
        brews.drop(recentCount.coerceAtLeast(0)).partition { it.isActive }
    }
    val searchResults = remember(brews, q) {
        if (q.isEmpty()) emptyList() else brews.filter { it.name.contains(q, ignoreCase = true) }
    }

    val select: (BrewSelection) -> Unit = { value ->
        onSelect(value)
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismiss()
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Text(
            text = stringResource(R.string.rawdata_brew_picker_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text(stringResource(R.string.rawdata_brew_picker_search)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.rawdata_clear))
                    }
                }
            },
            singleLine = true,
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        LazyColumn {
            if (q.isEmpty()) {
                item(key = "unassigned") {
                    BrewPickerItem(
                        headline = stringResource(R.string.rawdata_brew_unassigned),
                        supporting = stringResource(R.string.rawdata_metrics_count, unassignedCount),
                        selected = selection == BrewSelection.Unassigned,
                        onClick = { select(BrewSelection.Unassigned) },
                    )
                    HorizontalDivider()
                }
                brewSection(R.string.rawdata_brew_section_recent, recent, selection, select)
                brewSection(R.string.rawdata_brew_section_active, active, selection, select)
                brewSection(R.string.rawdata_brew_section_finished, finished, selection, select)
            } else if (searchResults.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = stringResource(R.string.rawdata_brew_picker_nothing_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(24.dp),
                    )
                }
            } else {
                brewSection(R.string.rawdata_brew_section_search_results, searchResults, selection, select)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun LazyListScope.brewSection(
    @StringRes titleRes: Int,
    brews: List<BrewUi>,
    selection: BrewSelection,
    onSelect: (BrewSelection) -> Unit,
) {
    if (brews.isEmpty()) return

    stickyHeader(key = "header_$titleRes") {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .background(BottomSheetDefaults.ContainerColor) // чтобы под заголовком не просвечивали пункты
                .padding(horizontal = 24.dp, vertical = 8.dp),
        )
    }
    items(brews, key = { it.id }) { brew ->
        BrewPickerItem(
            headline = brew.name,
            supporting = brew.dateText + " · " + stringResource(R.string.rawdata_metrics_count, brew.metricsCount),
//            supporting = brew.dateText + " · " +
//                    pluralStringResource(R.plurals.metrics_count, brew.metricsCount, brew.metricsCount),
            selected = selection == BrewSelection.Brew(brew),
            onClick = { onSelect(BrewSelection.Brew(brew)) },
        )
    }
}

@Composable
private fun BrewPickerItem(
    headline: String,
    supporting: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(headline, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = supporting?.let { { Text(it) } },
        trailingContent = if (selected) {
            { Icon(Icons.Filled.Check, contentDescription = null) }
        } else {
            null
        },
        colors = ListItemDefaults.colors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                Color.Transparent
            },
        ),
        modifier = Modifier.selectable(
            selected = selected,
            role = Role.RadioButton,
            onClick = onClick,
        ),
    )
}
