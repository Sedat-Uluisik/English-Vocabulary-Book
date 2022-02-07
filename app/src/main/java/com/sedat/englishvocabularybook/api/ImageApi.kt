package com.sedat.englishvocabularybook.api

import com.sedat.englishvocabularybook.model.ImageResponse
import com.sedat.englishvocabularybook.util.API_KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApi {

    //https://pixabay.com/api/?key=api_key&q=yellow
    @GET("/api/")
    fun SearchImage(
            @Query("q") query: String,
            @Query("per_page") perPage: Int = 50,
            @Query("key") apiKey: String = API_KEY
    ):Single<ImageResponse>

}