package com.example.mvi

data class UserListUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshIng: Boolean = false
)
