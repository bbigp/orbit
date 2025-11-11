package cn.coolbet.orbit.ui.view.sync

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.common.toRelativeTime
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.ContentRed
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.util.Date

object SyncScreen: Screen {
    private fun readResolve(): Any = SyncScreen

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun Content() {
        val model = getScreenModel<SyncScreenModel>()
        val state by model.state.collectAsState()
        val listState = rememberLazyListState()
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .distinctUntilChanged()
                .filter { !state.isLoadingMore }
                .collect {
                    model.nextPage()
                }
        }

        Scaffold(

        ) { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier.padding(paddingValues)
            ) {
                items(state.items, key = { it.id!! }) {
                    SyncRecordView(it)
                    SpacerDivider()
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SyncRecordView(record: SyncTaskRecord) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("执行时间: ${record.executeTime.toRelativeTime()} (${record.id})", style = AppTypography.R15)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("状态:", style = AppTypography.R15)
            Text(record.status, style = when(record.status) {
                SyncTaskRecord.FAIL -> AppTypography.R15B50.copy(color = ContentRed)
                else -> AppTypography.R15B50
            })
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("时间:", style = AppTypography.R15)
            Text("${record.fromTime.toRelativeTime()} - ${record.toTime.toRelativeTime()}", style = AppTypography.R15B50)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text("数据:", style = AppTypography.R15)
            Text("feed: ${record.feed}  folder: ${record.folder}  entry: ${record.entry}  media: ${record.media}", style = AppTypography.R15B50)
        }
        if (record.errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(record.errorMsg, style = AppTypography.R15B50)
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewSyncRecordView() {
    val record = SyncTaskRecord(id = 10002, fromTime = Date().time, toTime = Date().time)
    Column {
        SyncRecordView(record)
        SpacerDivider()
        SyncRecordView(record.copy(errorMsg = "null point exception", status = SyncTaskRecord.FAIL))
    }
}