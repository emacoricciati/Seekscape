package it.polito.mad.lab5g10.seekscape.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5g10.seekscape.R
import it.polito.mad.lab5g10.seekscape.models.AppState

sealed class BottomNavIcon {
    data class Vector(val imageVector: ImageVector) : BottomNavIcon()
    data class Resource(val resId: Int) : BottomNavIcon()
}

sealed class BottomNavItem(val route: String, val label: String, val icon: BottomNavIcon) {
    data object Explore : BottomNavItem("explore", "Explore", BottomNavIcon.Vector(Icons.Default.Search))
    data object Travels : BottomNavItem("travels", "Travels", BottomNavIcon.Resource(R.drawable.icon_travels))
    data object Add : BottomNavItem("add", "Add", BottomNavIcon.Vector(Icons.Default.Add))
    data object Profile : BottomNavItem("profile", "Profile", BottomNavIcon.Vector(Icons.Default.Person))
}


@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onTabSelected: (BottomNavItem) -> Unit
) {
    val doneFirstFetch by AppState.doneFirstFetch.collectAsState()
    val items = listOf(
        BottomNavItem.Explore,
        BottomNavItem.Travels,
        BottomNavItem.Add,
        BottomNavItem.Profile
    )

    Surface(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        val iconSizeModifier = Modifier.size(24.dp)
                        when (val icon = item.icon) {
                            is BottomNavIcon.Vector -> Icon(imageVector = icon.imageVector, contentDescription = item.label, modifier = iconSizeModifier)
                            is BottomNavIcon.Resource -> Icon(painter = painterResource(id = icon.resId), contentDescription = item.label, modifier = iconSizeModifier)
                        }
                    },
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    onClick = { onTabSelected(item) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled=doneFirstFetch
                )
            }
        }
    }
}