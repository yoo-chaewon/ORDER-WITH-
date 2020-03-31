package com.example.order_with.Core

import com.example.order_with.Data.Menus
import io.reactivex.Single
import retrofit2.http.GET

interface MenuAPI {
    @GET("/menu")
    fun getMenuData(

    ) : Single<Menus>
}