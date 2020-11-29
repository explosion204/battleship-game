package com.explosion204.battleship.di.modules

import androidx.lifecycle.ViewModel
import com.explosion204.battleship.data.repos.SessionRepository
import com.explosion204.battleship.di.annotations.ViewModelKey
import com.explosion204.battleship.viewmodels.BattleshipViewModel
import com.explosion204.battleship.viewmodels.UserViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(userViewModel: UserViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BattleshipViewModel::class)
    abstract fun bindBattleshipViewMode(battleshipViewModel: BattleshipViewModel) : ViewModel
}