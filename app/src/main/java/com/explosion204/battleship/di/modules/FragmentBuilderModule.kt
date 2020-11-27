package com.explosion204.battleship.di.modules

import com.explosion204.battleship.ui.fragments.EditUserDialogFragment
import com.explosion204.battleship.ui.fragments.SignInFragment
import com.explosion204.battleship.ui.fragments.StartupFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectSignInFragment(): SignInFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectStartupFragment(): StartupFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectEditUserDialogFragment(): EditUserDialogFragment
}