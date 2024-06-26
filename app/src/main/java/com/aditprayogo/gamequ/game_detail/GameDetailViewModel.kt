
package com.aditprayogo.gamequ.game_detail

import androidx.lifecycle.*
import com.aditprayogo.core.domain.entity.GameData
import com.aditprayogo.core.domain.entity.GameFavoriteData
import com.aditprayogo.core.domain.usecases.GameUseCase
import com.aditprayogo.core.utils.LoaderState
import com.aditprayogo.core.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

/**
 * Created by Aditiya Prayogo.
 */
interface GameDetailViewModelContract {
    fun getDetailGame(gameId: Int)
    fun insertGameToDb(game : GameFavoriteData)
    fun deleteGameFromDb(game : GameFavoriteData)
}

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gameUseCase: GameUseCase
) : ViewModel(), GameDetailViewModelContract {

    private val _state = MutableLiveData<LoaderState>()
    val state: LiveData<LoaderState> get() = _state

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean> get() = _networkError

    private val _resultDetailGameFromApi = MutableLiveData<GameData>()
    val resultDetailGameFromApi: LiveData<GameData> get() = _resultDetailGameFromApi

    private val _resultInsertToDbStatus = MutableLiveData<Boolean>()
    val resultInsertToDbStatus get() = _resultInsertToDbStatus

    private val _resultDeleteFromDbStatus = MutableLiveData<Boolean>()
    val resultDeleteFromDbStatus get() = _resultDeleteFromDbStatus

    /**
     * get detail game from api
     */
    override fun getDetailGame(gameId: Int) {
        _state.value = LoaderState.ShowLoading
        viewModelScope.launch {
            gameUseCase.getDetailGame(gameId).collect {
                when (it) {
                    is ResultState.Success -> {
                        _state.value = LoaderState.HideLoading
                        _resultDetailGameFromApi.postValue(it.data!!)
                    }
                    is ResultState.Error -> {
                        _error.postValue(it.error)
                    }
                    is ResultState.NetworkError -> {
                        _networkError.postValue(true)
                    }
                }
            }
        }
    }

    override fun insertGameToDb(game: GameFavoriteData) {
        viewModelScope.launch {
            try {
                gameUseCase.insertGameToDb(game)
                _resultInsertToDbStatus.postValue(true)
            } catch (e : Exception) {
                _error.postValue(e.localizedMessage)
            }

        }
    }

    override fun deleteGameFromDb(game: GameFavoriteData) {
        viewModelScope.launch {
            try {
                gameUseCase.deleteGameFromDb(game)
                _resultDeleteFromDbStatus.postValue(true)
            } catch (e : Exception) {
                _error.postValue(e.localizedMessage)
            }

        }
    }

    fun getDetailGameFromDb(gameId: Int) = gameUseCase.getGamesById(gameId).asLiveData()

}