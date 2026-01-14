package cn.coolbet.orbit.di

import cn.coolbet.orbit.manager.ListDetailCoordinator
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun ldCoordinator(): ListDetailCoordinator
}