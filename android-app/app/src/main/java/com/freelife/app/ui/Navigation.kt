package com.freelife.app.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Map : Screen("map/{groupId}") {
        const val ARG_GROUP_ID = "groupId"

        fun createRoute(groupId: Int) = "map/$groupId"
    }
    object Group : Screen("group/{groupId}") {
        const val ARG_GROUP_ID = "groupId"

        fun createRoute(groupId: Int) = "group/$groupId"
    }
    object Settings : Screen("settings")
}
