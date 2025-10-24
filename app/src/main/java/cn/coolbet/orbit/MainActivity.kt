package cn.coolbet.orbit

import android.graphics.pdf.models.ListItem
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.coolbet.orbit.ui.theme.OrbitTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrbitTheme {
                Surface (modifier = Modifier.fillMaxSize()){

                }
            }
        }
    }
}
// 2. 模拟数据获取函数
/**
 * 模拟网络请求，获取指定页码的数据
 * @param page 要获取的页码 (从 1 开始)
 * @param pageSize 每页包含的条目数
 * @param delayMillis 模拟网络延迟时间 (毫秒)
 */
//@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
//private suspend fun fetchItems(page: Int, pageSize: Int, delayMillis: Long): List<ListItem> {
//    delay(delayMillis) // 模拟网络延迟
//
//    // 如果是第一页，则返回 20 条，否则返回 10 条，用于演示分页
//    val itemsToGenerate = if (page == 1) 20 else pageSize
//    val startId = (page - 1) * 20 + 1 // 确保 ID 连续
//
//    return List(itemsToGenerate) { index ->
//        ListItem(
//            id = startId + index,
//            title = "列表项 #${startId + index} (Page $page)"
//        )
//    }
//}

@Composable
fun DataListScreen() {
    // 状态管理
    val coroutineScope = rememberCoroutineScope() // 用于启动协程
    var listItems by remember { mutableStateOf(emptyList<ListItem>()) }
    var currentPage by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(true) } // 首次加载 indicator
    var isRefreshing by remember { mutableStateOf(false) } // 下拉刷新 indicator
    var isPaginating by remember { mutableStateOf(false) } // 上拉加载 indicator
    var canLoadMore by remember { mutableStateOf(true) } // 是否还有更多数据
    val listState = rememberLazyListState() // 用于监听列表滚动状态

    // ----------------------------------------------------------------------
    // 3. 数据加载逻辑
    // ----------------------------------------------------------------------

    // 初始数据加载或刷新数据的统一方法
    val loadData: (isInitial: Boolean, isRefresh: Boolean) -> Unit = { isInitial, isRefresh ->
        coroutineScope.launch {
            if (isInitial) isLoading = true
            if (isRefresh) isRefreshing = true

            val pageToLoad = if (isRefresh) 1 else currentPage
            val delayDuration = if (isInitial || isRefresh) 2000L else 1000L

//            try {
//                val newItems = fetchItems(pageToLoad, 10, delayDuration) // 每页加载 10 条（除了第一次）
//
//                if (isRefresh) {
//                    // 下拉刷新：清空列表并加载第一页
//                    listItems = newItems
//                    currentPage = 1
//                    canLoadMore = true // 刷新后总是有更多数据
//                } else {
//                    // 初始加载或上拉加载：追加数据
//                    listItems = listItems + newItems
//                    currentPage++
//                    // 假设如果返回的条目数少于预期，则没有更多数据了
//                    if (newItems.size < 10) canLoadMore = false
//                }
//
//            } catch (e: Exception) {
//                // 实际项目中应处理错误
//                println("Error loading data: $e")
//            } finally {
//                isLoading = false
//                isRefreshing = false
//                isPaginating = false
//            }
        }
    }

    // 首次进入页面时触发加载
    LaunchedEffect(Unit) {
        loadData(true, false)
    }

    // 监听列表滚动，实现上拉加载更多
    LaunchedEffect(listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index) {
        val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        val totalItems = listState.layoutInfo.totalItemsCount

        // 如果滚动到列表底部 (倒数第 5 个元素)，并且可以加载更多，并且当前没有在分页
        if (lastVisibleIndex != null && totalItems > 0 && lastVisibleIndex >= totalItems - 5 && canLoadMore && !isPaginating && !isLoading) {
            isPaginating = true
            loadData(false, false)
        }
    }

    // ----------------------------------------------------------------------
    // 4. UI 结构
    // ----------------------------------------------------------------------
    Scaffold { paddingValues ->

        // 使用 Box 实现加载状态的覆盖
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // A. 初始加载状态
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("首次加载中，请稍候...", style = MaterialTheme.typography.bodyLarge)
                }
            }
            // B. 数据加载完成，显示列表
            else {
                // 顶部“下拉刷新”区域 (此处使用按钮模拟，实际应用中会使用 swipe gesture)
                Column(modifier = Modifier.fillMaxSize()) {

                    // 下拉刷新指示器 (手动触发模拟)
                    Button(
                        onClick = { loadData(false, true) },
                        enabled = !isRefreshing && !isPaginating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
//                        if (isRefreshing) {
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                CircularProgressIndicator(
//                                    modifier = Modifier.size(20.dp),
//                                    strokeWidth = 2.dp
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Text("刷新中...")
//                            }
//                        } else {
//                            Text("点击模拟下拉刷新")
//                        }
                    }

                    // 列表内容
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f) // 占用剩余空间
                    ) {
                        itemsIndexed(listItems) { index, item ->
//                            ListItemRow(item = item)
                        }

                        // 列表底部的加载更多指示器
                        if (isPaginating) {
                            item {
//                                PaginatingIndicator()
                            }
                        }

                        // 列表底部的"没有更多数据"提示
                        if (!canLoadMore && listItems.isNotEmpty()) {
                            item {
                                NoMoreDataIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun ListItemRow(item: ListItem) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { /* 列表项点击事件 */ }
//            .padding(horizontal = 16.dp, vertical = 12.dp)
//    ) {
//        Text(
//            text = item.title,
//            style = MaterialTheme.typography.titleMedium,
//            color = MaterialTheme.colorScheme.onSurface
//        )
//        Text(
//            text = "ID: ${item.id}",
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//        Divider(modifier = Modifier.padding(top = 12.dp))
//    }
//}

// 上拉加载指示器 Composable
//@Composable
//fun PaginatingIndicator() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        CircularProgressIndicator(
//            modifier = Modifier.size(24.dp),
//            strokeWidth = 3.dp
//        )
//        Spacer(modifier = Modifier.width(16.dp))
//        Text("加载更多中...", style = MaterialTheme.typography.bodyMedium)
//    }
//}

// 没有更多数据指示器 Composable
@Composable
fun NoMoreDataIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "--- 到底了，没有更多数据 ---",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * 预览 Composable
 */
@Preview(showBackground = true)
@Composable
fun DataListScreenPreview() {
    OrbitTheme {
        NoMoreDataIndicator()
    }
}