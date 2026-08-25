package com.weatherapp.android.ui.weather

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

// Stateless - takes plain values/callbacks, no ViewModel reference, so it's
// easy to preview and doesn't need Hilt to render. WeatherRoute wires it up.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    uiState: WeatherUiState,
    searchText: String,
    icon: Bitmap?,
    onSearchTextChanged: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onUseCurrentLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Weather") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                searchText = searchText,
                onSearchTextChanged = onSearchTextChanged,
                onSearchSubmit = onSearchSubmit
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(onClick = onUseCurrentLocation) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use Current Location")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is WeatherUiState.Idle -> StatusMessage(
                        icon = Icons.Default.WbSunny,
                        title = "Search for a city",
                        message = "Or allow location access to see local weather."
                    )
                    is WeatherUiState.Loading -> CircularProgressIndicator()
                    is WeatherUiState.Loaded -> WeatherCard(weather = uiState.weather, icon = icon)
                    is WeatherUiState.Failed -> StatusMessage(
                        icon = Icons.Default.Warning,
                        title = "Something went wrong",
                        message = uiState.message
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchSubmit: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChanged,
            modifier = Modifier.weight(1f),
            singleLine = true,
            placeholder = { Text("Search a US city (e.g. Austin, TX)") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() })
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onSearchSubmit, enabled = searchText.isNotBlank()) {
            Text("Search")
        }
    }
}
