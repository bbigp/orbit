package cn.coolbet.orbit.module

import coil3.ImageLoader
import coil3.fetch.FetchResult
import coil3.request.Options
import coil3.fetch.Fetcher

import android.util.Base64 // Android SDK Base64
import cn.coolbet.orbit.remote.miniflux.MinifluxClient
import cn.coolbet.orbit.remote.miniflux.ProfileApi
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.map.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.BufferedSource
import okio.FileSystem
import java.io.IOException






