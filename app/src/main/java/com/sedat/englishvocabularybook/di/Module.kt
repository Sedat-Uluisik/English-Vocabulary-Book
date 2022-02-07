package com.sedat.englishvocabularybook.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sedat.englishvocabularybook.R
import com.sedat.englishvocabularybook.api.ImageApi
import com.sedat.englishvocabularybook.repo.ImageRepository
import com.sedat.englishvocabularybook.repo.ImageRepositoryInterface
import com.sedat.englishvocabularybook.util.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Singleton
    @Provides
    fun injectRetrofit(): ImageApi{
        return Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ImageApi::class.java)
    }

    @Singleton
    @Provides
    fun injectImageRepository(imageApi: ImageApi, @ApplicationContext context: Context) = ImageRepository(imageApi, context) as ImageRepositoryInterface

    @Singleton
    @Provides
    fun injectGlide(@ApplicationContext context: Context) =
            Glide.with(context)
                    .setDefaultRequestOptions(
                            RequestOptions().placeholder(R.drawable.downloading_24).error(R.drawable.error_24)
                    )
}