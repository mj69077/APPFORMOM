package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Order

@Composable
fun OrderTable(
    orders: List<Order>,
    onEditOrder: (Order) -> Unit,
    onDeleteOrder: (Order) -> Unit,
    onStatusChange: (Order, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            // Table Header Row
            TableHeaderRow()

            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد طلبات محفوظة حالياً في الجدول.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                orders.forEachIndexed { index, order ->
                    TableRowItem(
                        index = index + 1,
                        order = order,
                        isEven = index % 2 == 0,
                        onEditOrder = onEditOrder,
                        onDeleteOrder = onDeleteOrder,
                        onStatusChange = onStatusChange,
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
private fun TableHeaderRow() {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("#", width = 40.dp)
        HeaderCell("الاسم (Name)", width = 140.dp)
        HeaderCell("الهاتف (Phone)", width = 110.dp)
        HeaderCell("العنوان (Address)", width = 150.dp)
        HeaderCell("التاريخ (Date)", width = 100.dp)
        HeaderCell("المشتريات (Items)", width = 180.dp)
        HeaderCell("السعر", width = 80.dp)
        HeaderCell("الحالة", width = 110.dp)
        HeaderCell("الإجراءات", width = 120.dp)
    }
}

@Composable
private fun HeaderCell(title: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        ),
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun TableRowItem(
    index: Int,
    order: Order,
    isEven: Boolean,
    onEditOrder: (Order) -> Unit,
    onDeleteOrder: (Order) -> Unit,
    onStatusChange: (Order, String) -> Unit,
    context: Context
) {
    val backgroundColor = if (isEven) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .background(backgroundColor)
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .testTag("table_row_${order.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Index
        Text(
            text = index.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(40.dp)
        )

        // Name
        Text(
            text = order.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(140.dp)
        )

        // Phone
        Text(
            text = order.phone,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(110.dp)
        )

        // Address
        Text(
            text = order.address.ifBlank { "-" },
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(150.dp)
        )

        // Date
        Text(
            text = order.date,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(100.dp)
        )

        // Items
        Text(
            text = order.items,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(180.dp)
        )

        // Price
        Text(
            text = order.price.ifBlank { "-" },
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(80.dp)
        )

        // Status Badge
        Box(
            modifier = Modifier.width(110.dp),
            contentAlignment = Alignment.Center
        ) {
            StatusChip(status = order.status)
        }

        // Actions
        Row(
            modifier = Modifier.width(120.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Direct Call
            IconButton(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.phone}"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "تعذر فتح خدمة الاتصال", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "اتصال",
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Edit
            IconButton(
                onClick = { onEditOrder(order) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "تعديل",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // More Options Dropdown
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "المزيد",
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("تغيير الحالة إلى (مكتمل)") },
                        onClick = {
                            onStatusChange(order, "مكتمل")
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("تغيير الحالة إلى (قيد التوصيل)") },
                        onClick = {
                            onStatusChange(order, "قيد التوصيل")
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("تغيير الحالة إلى (جديد)") },
                        onClick = {
                            onStatusChange(order, "جديد")
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("مشاركة بيانات الطلب") },
                        onClick = {
                            shareOrderDetails(context, order)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Share, contentDescription = null, Modifier.size(18.dp))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("حذف الطلب", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            onDeleteOrder(order)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (bgColor, textColor) = when (status) {
        "مكتمل" -> Pair(Color(0xFFD1FAE5), Color(0xFF047857))
        "قيد التوصيل" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        "ملغي" -> Pair(Color(0xFFFEE2E2), Color(0xFFB91C1C))
        else -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

fun shareOrderDetails(context: Context, order: Order) {
    val details = """
        📋 تفاصيل الطلب:
        👤 العميل: ${order.name}
        📞 الهاتف: ${order.phone}
        📍 العنوان: ${order.address}
        📅 التاريخ: ${order.date}
        🛍️ المشتريات: ${order.items}
        💵 المبلغ: ${order.price}
        📌 الحالة: ${order.status}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, details)
    }
    context.startActivity(Intent.createChooser(intent, "مشاركة تفاصيل الطلب"))
}
