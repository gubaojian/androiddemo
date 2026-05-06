package com.example.mvi

sealed class UserListIntent {
    object LoadUsers : UserListIntent()
    object RefreshUsers: UserListIntent()
    data class ClickUser(val userId: Long): UserListIntent()
}