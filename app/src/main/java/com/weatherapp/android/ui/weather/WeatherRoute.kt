package com.weatherapp.android.ui.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private enum class PermissionRequestReason { InitialLaunch, ManualRequest }

/**
 * Stateful entry point for the weather screen, and the one place that talks
 * to Android's permission system directly — only an Activity/Compose context
 * can show the system permission dialog, so that responsibility can't live
 * in [WeatherViewModel]. Once permission is resolved (granted or denied),
 * the *decision* of what to load with that answer is handed back to the
 * ViewModel, which is where that logic is unit tested.
 */
@Composable
fun WeatherRoute(viewModel: WeatherViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(context.hasCoarseLocationPermission()) }
    var pendingReason by remember { mutableStateOf<PermissionRequestReason?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        when (pendingReason) {
            PermissionRequestReason.InitialLaunch -> viewModel.onAppear(hasLocationPermission = granted)
            PermissionRequestReason.ManualRequest -> viewModel.onUseCurrentLocationTapped(hasLocationPermission = granted)
            null -> Unit
        }
        pendingReason = null
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            viewModel.onAppear(hasLocationPermission = true)
        } else {
            pendingReason = PermissionRequestReason.InitialLaunch
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val icon by viewModel.iconBitmap.collectAsStateWithLifecycle()

    WeatherScreen(
        uiState = uiState,
        searchText = searchText,
        icon = icon,
        onSearchTextChanged = viewModel::onSearchTextChanged,
        onSearchSubmit = viewModel::onSearchSubmitted,
        onUseCurrentLocation = {
            if (hasLocationPermission) {
                viewModel.onUseCurrentLocationTapped(hasLocationPermission = true)
            } else {
                pendingReason = PermissionRequestReason.ManualRequest
                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    )
}

private fun Context.hasCoarseLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
