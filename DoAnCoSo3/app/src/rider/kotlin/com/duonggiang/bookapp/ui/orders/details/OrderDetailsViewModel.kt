package com.duonggiang.bookapp.ui.orders.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duonggiang.bookapp.data.FoodApi
import com.duonggiang.bookapp.data.models.Order
import com.duonggiang.bookapp.data.models.SocketLocation
import com.duonggiang.bookapp.data.models.SocketLocationResponse
import com.duonggiang.bookapp.data.remote.ApiResponse
import com.duonggiang.bookapp.data.remote.safeApiCall
import com.duonggiang.bookapp.data.repository.LocationUpdateSocketRepository
import com.duonggiang.bookapp.ui.features.orders.OrderDetailsBaseViewModel
import com.duonggiang.bookapp.utils.OrdersUtils
import com.google.maps.android.PolyUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject


@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    val foodApi: FoodApi,
    repository: LocationUpdateSocketRepository
) : OrderDetailsBaseViewModel(repository) {

    val listOfStatus = OrdersUtils.OrderStatus.entries.map { it.name }

    private val _uiState = MutableStateFlow<OrderDetailsUiState>(OrderDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<OrderDetailsEvent?>()
    val event = _event.asSharedFlow()
    var order: Order? = null

    fun getOrderDetails(orderID: String) {
        viewModelScope.launch {
            _uiState.value = OrderDetailsUiState.Loading
            val result = safeApiCall { foodApi.getOrderDetails(orderID) }
            when (result) {
                is ApiResponse.Success -> {
                    if (result.data.status == OrdersUtils.OrderStatus.OUT_FOR_DELIVERY.name) {
                        _uiState.value = OrderDetailsUiState.OrderDelivery(result.data)
                        result.data.riderId?.let {
                            connectSocket(orderID, it)
                        }
                    } else {

                        if (result.data.status == OrdersUtils.OrderStatus.DELIVERED.name
                            || result.data.status == OrdersUtils.OrderStatus.CANCELLED.name
                            || result.data.status == OrdersUtils.OrderStatus.REJECTED.name
                        ) {
                            disconnectSocket()
                        }
                        _uiState.value = OrderDetailsUiState.Success(result.data)
                    }
                    order = result.data
                }

                is ApiResponse.Error -> {
                    _uiState.value = OrderDetailsUiState.Error
                }

                else -> {
                    _uiState.value = OrderDetailsUiState.Error
                }
            }
        }
    }

    fun updateOrderStatus(orderID: String, status: String) {
        viewModelScope.launch {
            val result =
                safeApiCall { foodApi.updateOrderStatus(orderID, mapOf("status" to status)) }
            when (result) {
                is ApiResponse.Success -> {
                    _event.emit(OrderDetailsEvent.ShowPopUp("Order Status updated"))
                    getOrderDetails(orderID)
                }

                else -> {
                    _event.emit(OrderDetailsEvent.ShowPopUp("Order Status update failed"))
                }
            }
        }
    }

    sealed class OrderDetailsUiState {
        object Loading : OrderDetailsUiState()
        data class Success(val order: Order) : OrderDetailsUiState()
        data class OrderDelivery(val order: Order) : OrderDetailsUiState()
        object Error : OrderDetailsUiState()
    }

    sealed class OrderDetailsEvent {
        object NavigateBack : OrderDetailsEvent()
        data class ShowPopUp(val msg: String) : OrderDetailsEvent()
    }
}