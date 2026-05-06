package com.example.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class UserListEffect {
    data class NavigateToDetail(val userId: Long) : UserListEffect()
}

class UserListViewModel(
): ViewModel() {

    private val repository: UserRepository = UserRepository()
    private val _uiState = MutableStateFlow<UserListUiState>(UserListUiState())
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    private val _effect = Channel<UserListEffect>()
    val effect = _effect.receiveAsFlow()


    fun dispatch(intent: UserListIntent) {
        when(intent) {
            is UserListIntent.LoadUsers -> loadUsers()
            is UserListIntent.RefreshUsers -> refreshUsers()
            is UserListIntent.ClickUser -> navigateToDetail(intent)
        }
    }



    private fun loadUsers() {
        if (_uiState.value.isLoading) {
            return
        }
        viewModelScope.launch {
                _uiState.update {
                   it.copy( isLoading =  true, error = null)
                }
                try {
                    val userList = repository.getUsers()
                    _uiState.update {
                        it.copy(users = userList, isLoading =  false)
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "加载失败，${e.message}")
                    }
                }
        }
    }

    private fun refreshUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshIng = true) }
            try {
                val users = repository.getUsers()
                _uiState.update { it.copy(users = users, isRefreshIng = false) }
            } catch (e: Exception){
                _uiState.update { it.copy(error = "刷新失败${e.message}", isRefreshIng = false) }
            }
        }
    }

    private fun navigateToDetail(intent: UserListIntent.ClickUser) {
        viewModelScope.launch {
            _effect.send(UserListEffect.NavigateToDetail(intent.userId))
        }
    }


}