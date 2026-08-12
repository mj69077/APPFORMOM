package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Order
import com.example.ui.components.EditOrderDialog
import com.example.ui.components.OrderCardList
import com.example.ui.components.OrderFormCard
import com.example.ui.components.OrderTable
import com.example.ui.components.SummaryHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    viewModel: OrderViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val editingOrder by viewModel.editingOrder.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var orderToDelete by remember { mutableStateOf<Order?>(null) }
    var isFormVisible by remember { mutableStateOf(true) }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "سجل وإدارة الطلبات",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            // Summary Header Statistics
            SummaryHeader(orders = orders)

            Spacer(modifier = Modifier.height(14.dp))

            // Toggle Form Expansion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نموذج إدخال البيانات",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = { isFormVisible = !isFormVisible },
                    modifier = Modifier.testTag("toggle_form_button")
                ) {
                    Text(if (isFormVisible) "إخفاء النموذج ▲" else "إظهار النموذج ▼")
                }
            }

            // Input Form Section
            AnimatedVisibility(
                visible = isFormVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    OrderFormCard(viewModel = viewModel)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Search Bar & View Mode Switcher
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = { Text("بحث باسم العميل، الهاتف، أو المشتريات...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "بحث"
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "تفريغ البحث"
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Table / Cards View Toggle Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(modifier = Modifier.padding(2.dp)) {
                                IconButton(
                                    onClick = { viewModel.setViewMode(ViewMode.TABLE) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TableChart,
                                        contentDescription = "عرض الجدول",
                                        tint = if (viewMode == ViewMode.TABLE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.setViewMode(ViewMode.CARDS) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GridView,
                                        contentDescription = "عرض البطاقات",
                                        tint = if (viewMode == ViewMode.CARDS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Chips (الكل, اليوم, جديد, مكتمل)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = activeFilter == OrderFilter.ALL,
                            onClick = { viewModel.setFilter(OrderFilter.ALL) },
                            label = { Text("الكل (${orders.size})", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = activeFilter == OrderFilter.TODAY,
                            onClick = { viewModel.setFilter(OrderFilter.TODAY) },
                            label = { Text("طلبات اليوم", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = activeFilter == OrderFilter.NEW,
                            onClick = { viewModel.setFilter(OrderFilter.NEW) },
                            label = { Text("جديدة", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = activeFilter == OrderFilter.COMPLETED,
                            onClick = { viewModel.setFilter(OrderFilter.COMPLETED) },
                            label = { Text("مكتملة", fontSize = 12.sp) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Data Table / List Display Section
            Text(
                text = if (viewMode == ViewMode.TABLE) "جدول الطلبات السابقة (Table View):" else "قائمة بطاقات الطلبات (Cards View):",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (viewMode == ViewMode.TABLE) {
                OrderTable(
                    orders = orders,
                    onEditOrder = { viewModel.openEditDialog(it) },
                    onDeleteOrder = { orderToDelete = it },
                    onStatusChange = { order, status -> viewModel.updateOrderStatus(order, status) }
                )
            } else {
                OrderCardList(
                    orders = orders,
                    onEditOrder = { viewModel.openEditDialog(it) },
                    onDeleteOrder = { orderToDelete = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Edit Order Modal Dialog
    editingOrder?.let { order ->
        EditOrderDialog(
            order = order,
            onDismiss = { viewModel.closeEditDialog() },
            onSave = { updated -> viewModel.updateOrder(updated) }
        )
    }

    // Delete Confirmation Dialog
    orderToDelete?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            title = { Text("تأكيد الحذف") },
            text = { Text("هل أنت تأكد من إزالة طلب العميل \"${order.name}\" من القائمة؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteOrder(order)
                        orderToDelete = null
                    }
                ) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
