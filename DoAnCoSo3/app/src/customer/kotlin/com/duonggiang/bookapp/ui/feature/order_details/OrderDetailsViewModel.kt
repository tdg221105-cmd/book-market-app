package com.duonggiang.bookapp.ui.feature.order_details

import androidx.lifecycle.viewModelScope
import com.duonggiang.bookapp.data.FoodApi
import com.duonggiang.bookapp.data.models.Order
import com.duonggiang.bookapp.data.remote.safeApiCall
import com.duonggiang.bookapp.ui.features.orders.LocationUpdateBaseRepository
import com.duonggiang.bookapp.ui.features.orders.OrderDetailsBaseViewModel
import com.duonggiang.bookapp.utils.OrdersUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi, repository: LocationUpdateBaseRepository
) : OrderDetailsBaseViewModel(repository) {

    private val _state = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<OrderDetailsEvent>()
    val event get() = _event.asSharedFlow()


    fun getOrderDetails(orderId: String) {
        viewModelScope.launch {
            _state.value = OrderDetailsState.Loading
            val result = safeApiCall { foodApi.getOrderDetails(orderId) }
            when (result) {
                is com.duonggiang.bookapp.data.remote.ApiResponse.Success -> {
                    _state.value = OrderDetailsState.OrderDetails(result.data)

                    if (result.data.status == OrdersUtils.OrderStatus.OUT_FOR_DELIVERY.name) {
                        result.data.riderId?.let {
                            connectSocket(orderId, it)
                        }
                    } else {
                        if (result.data.status == OrdersUtils.OrderStatus.DELIVERED.name
                            || result.data.status == OrdersUtils.OrderStatus.CANCELLED.name
                            || result.data.status == OrdersUtils.OrderStatus.REJECTED.name) {
                            disconnectSocket()
                        }
                    }
                }

                is com.duonggiang.bookapp.data.remote.ApiResponse.Error -> {
                    _state.value = OrderDetailsState.Error(result.message)
                }

                is com.duonggiang.bookapp.data.remote.ApiResponse.Exception -> {
                    _state.value =
                        OrderDetailsState.Error(result.exception.message ?: "An error occurred")
                }
            }
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _event.emit(OrderDetailsEvent.NavigateBack)
        }
    }

    fun getImage(order: Order): Int {
        when (order.status) {
            "Delivered" -> return com.duonggiang.bookapp.R.drawable.ic_delivered
            "Preparing" -> return com.duonggiang.bookapp.R.drawable.ic_preparing
            "On the way" -> return com.duonggiang.bookapp.R.drawable.picked_by_rider_icon
            else -> return com.duonggiang.bookapp.R.drawable.ic_pending
        }
    }

    sealed class OrderDetailsEvent {
        object NavigateBack : OrderDetailsEvent()
    }

    sealed class OrderDetailsState {
        object Loading : OrderDetailsState()
        data class OrderDetails(val order: Order) : OrderDetailsState()
        data class Error(val message: String) : OrderDetailsState()
    }
}