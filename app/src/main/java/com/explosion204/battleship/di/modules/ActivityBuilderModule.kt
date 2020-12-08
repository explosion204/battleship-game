package com.explosion204.battleship.di.modules

import com.explosion204.battleship.ui.activities.GameActivity
import com.explosion204.battleship.ui.activities.StartupActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectStartupActivity(): StartupActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectGameActivity(): GameActivity
}