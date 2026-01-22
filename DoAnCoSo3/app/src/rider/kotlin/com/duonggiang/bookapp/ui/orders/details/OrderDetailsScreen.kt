package com.duonggiang.bookapp.ui.orders.details

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.duonggiang.bookapp.R
import com.duonggiang.bookapp.data.models.Order
import com.duonggiang.bookapp.data.models.SocketLocation
import com.duonggiang.bookapp.ui.features.notifications.ErrorScreen
import com.duonggiang.bookapp.ui.features.notifications.LoadingScreen
import com.duonggiang.bookapp.ui.features.orders.order_map.OrderTrackerMapView
import com.duonggiang.bookapp.utils.OrdersUtils
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    navController: NavController,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = orderId) {
        viewModel.getOrderDetails(orderId)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = "Order Details")
        LaunchedEffect(key1 = true) {
            viewModel.event.collectLatest {
                when (it) {
                    is OrderDetailsViewModel.OrderDetailsEvent.NavigateBack -> {
                        navController.popBackStack()
                    }

                    is OrderDetailsViewModel.OrderDetailsEvent.ShowPopUp -> {
                        Toast.makeText(navController.context, it.msg, Toast.LENGTH_SHORT).show()
                    }

                    else -> {

                    }
                }
            }
        }

        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        when (uiState.value) {
            is OrderDetailsViewModel.OrderDetailsUiState.Loading -> {
                LoadingScreen()
            }

            is OrderDetailsViewModel.OrderDetailsUiState.Error -> {
                ErrorScreen(message = "Something went wrong") {
                    viewModel.getOrderDetails(orderId)
                }
            }

            is OrderDetailsViewModel.OrderDetailsUiState.Success -> {
                val order =
                    (uiState.value as OrderDetailsViewModel.OrderDetailsUiState.Success).order
                Text(text = order.id)
                Spacer(modifier = Modifier.padding(8.dp))
                order.items.forEach {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp)
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Text(text = it.menuItemName ?: "")
                        Text(text = it.quantity.toString())
                    }
                }

                if (order.status == OrdersUtils.OrderStatus.DELIVERED.name) {
                    Text(
                        text = "Order Delivered",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Green
                    )
                    Button(onClick = { navController.popBackStack() }) {
                        Text(text = "Back")
                    }
                } else {
                    FlowRow(modifier = Modifier.fillMaxWidth()) {
                        viewModel.listOfStatus.forEach {
                            Button(
                                onClick = { viewModel.updateOrderStatus(orderId, it) },
                                enabled = order.status != it
                            ) {
                                Text(text = it)
                            }
                        }
                    }
                }

            }

            is OrderDetailsViewModel.OrderDetailsUiState.OrderDelivery -> {
                val order =
                    (uiState.value as OrderDetailsViewModel.OrderDetailsUiState.OrderDelivery).order
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = order.id)
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(onClick = {
                        viewModel.updateOrderStatus(
                            orderId,
                            OrdersUtils.OrderStatus.DELIVERED.name
                        )
                    }) {
                        Text(text = "Deliver")
                    }
                    OrderTrackerMapView(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        viewModel = viewModel,
                        order = order
                    )

                }

            }
        }
    }
}
















