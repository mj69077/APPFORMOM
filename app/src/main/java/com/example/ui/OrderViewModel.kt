package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Order
import com.example.data.OrderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ViewMode {
    TABLE, CARDS
}

enum class OrderFilter {
    ALL, TODAY, NEW, COMPLETED
}

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrderRepository

    init {
        val dao = AppDatabase.getDatabase(application).orderDao()
        repository = OrderRepository(dao)
    }

    // Form inputs
    var nameInput = MutableStateFlow("")
        private set
    var phoneInput = MutableStateFlow("")
        private set
    var addressInput = MutableStateFlow("")
        private set
    var dateInput = MutableStateFlow(getTodayDateString())
        private set
    var itemsInput = MutableStateFlow("")
        private set
    var priceInput = MutableStateFlow("")
        private set
    var notesInput = MutableStateFlow("")
        private set
    var statusInput = MutableStateFlow("جديد")
        private set

    // Form validation state
    var nameError = MutableStateFlow<String?>(null)
        private set
    var phoneError = MutableStateFlow<String?>(null)
        private set
    var itemsError = MutableStateFlow<String?>(null)
        private set

    // Search and Filters
    var searchQuery = MutableStateFlow("")
        private set
    var activeFilter = MutableStateFlow(OrderFilter.ALL)
        private set
    var viewMode = MutableStateFlow(ViewMode.TABLE)
        private set

    // Dialog state for Editing
    var editingOrder = MutableStateFlow<Order?>(null)
        private set

    // Snackbars / Feedback messages
    var userMessage = MutableStateFlow<String?>(null)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val orders: StateFlow<List<Order>> = combine(searchQuery, activeFilter) { query, filter ->
        Pair(query, filter)
    }.flatMapLatest { (query, filter) ->
        val flow = if (query.isBlank()) repository.allOrders else repository.searchOrders(query.trim())
        flow
    }.combine(activeFilter) { list, filter ->
        val todayStr = getTodayDateString()
        when (filter) {
            OrderFilter.ALL -> list
            OrderFilter.TODAY -> list.filter { it.date == todayStr }
            OrderFilter.NEW -> list.filter { it.status == "جديد" }
            OrderFilter.COMPLETED -> list.filter { it.status == "مكتمل" }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onNameChange(value: String) {
        nameInput.value = value
        if (value.isNotBlank()) nameError.value = null
    }

    fun onPhoneChange(value: String) {
        phoneInput.value = value
        if (value.isNotBlank()) phoneError.value = null
    }

    fun onAddressChange(value: String) {
        addressInput.value = value
    }

    fun onDateChange(value: String) {
        dateInput.value = value
    }

    fun onItemsChange(value: String) {
        itemsInput.value = value
        if (value.isNotBlank()) itemsError.value = null
    }

    fun onPriceChange(value: String) {
        priceInput.value = value
    }

    fun onNotesChange(value: String) {
        notesInput.value = value
    }

    fun onStatusChange(value: String) {
        statusInput.value = value
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun setFilter(filter: OrderFilter) {
        activeFilter.value = filter
    }

    fun setViewMode(mode: ViewMode) {
        viewMode.value = mode
    }

    fun saveOrder() {
        var isValid = true
        if (nameInput.value.isBlank()) {
            nameError.value = "يرجى إدخال اسم العميل"
            isValid = false
        }
        if (phoneInput.value.isBlank()) {
            phoneError.value = "يرجى إدخال رقم الهاتف"
            isValid = false
        }
        if (itemsInput.value.isBlank()) {
            itemsError.value = "يرجى تفصيل المشتريات"
            isValid = false
        }

        if (!isValid) return

        val newOrder = Order(
            name = nameInput.value.trim(),
            phone = phoneInput.value.trim(),
            address = addressInput.value.trim(),
            date = if (dateInput.value.isBlank()) getTodayDateString() else dateInput.value.trim(),
            items = itemsInput.value.trim(),
            price = priceInput.value.trim(),
            notes = notesInput.value.trim(),
            status = statusInput.value,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.insert(newOrder)
            userMessage.value = "تم حفظ الطلب بنجاح!"
            resetForm()
        }
    }

    fun resetForm() {
        nameInput.value = ""
        phoneInput.value = ""
        addressInput.value = ""
        dateInput.value = getTodayDateString()
        itemsInput.value = ""
        priceInput.value = ""
        notesInput.value = ""
        statusInput.value = "جديد"
        nameError.value = null
        phoneError.value = null
        itemsError.value = null
    }

    fun openEditDialog(order: Order) {
        editingOrder.value = order
    }

    fun closeEditDialog() {
        editingOrder.value = null
    }

    fun updateOrder(updatedOrder: Order) {
        viewModelScope.launch {
            repository.update(updatedOrder)
            userMessage.value = "تم تحديث بيانات الطلب"
            closeEditDialog()
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            repository.delete(order)
            userMessage.value = "تم حذف الطلب"
        }
    }

    fun updateOrderStatus(order: Order, newStatus: String) {
        viewModelScope.launch {
            repository.update(order.copy(status = newStatus))
            userMessage.value = "تم تغيير حالة الطلب إلى: $newStatus"
        }
    }

    fun clearUserMessage() {
        userMessage.value = null
    }

    fun loadSampleData() {
        viewModelScope.launch {
            val samples = listOf(
                Order(
                    name = "أحمد محمود العلي",
                    phone = "0501234567",
                    address = "الرياض - حي النخيل - شارع التخصصي",
                    date = getTodayDateString(),
                    items = "حقيبة جلدية سوداء + محفظة رجالية",
                    price = "350 ر.س",
                    status = "جديد",
                    notes = "يرجى الاتصال قبل التوصيل"
                ),
                Order(
                    name = "سارة يوسف الشمري",
                    phone = "0559876543",
                    address = "جدة - حي الزهراء - بالقرب من المستشفى",
                    date = getTodayDateString(),
                    items = "طقم عطور ملكي - 3 قطع",
                    price = "520 ر.س",
                    status = "قيد التوصيل",
                    notes = "الدفع عند الاستلام"
                ),
                Order(
                    name = "محمد عبد الله الخالد",
                    phone = "0561122334",
                    address = "الدمام - الشاطئ شرقي",
                    date = getYesterdayDateString(),
                    items = "ساعة ذكية مقاومة للماء (لون أسود)",
                    price = "280 ر.س",
                    status = "مكتمل",
                    notes = "تم التوصيل بنجاح"
                )
            )
            samples.forEach { repository.insert(it) }
            userMessage.value = "تم إضافة طلبات تجريبية للاختبار"
        }
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        private fun getYesterdayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date(System.currentTimeMillis() - 86400000))
        }
    }
}
