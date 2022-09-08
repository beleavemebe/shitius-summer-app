package company.vk.education.siriusapp.di

import company.vk.education.siriusapp.data.GIS_GEOCODER_URL
import company.vk.education.siriusapp.data.YANDEX_GEOCODER_URL
import company.vk.education.siriusapp.data.api.yandex.GeocoderAPI
import company.vk.education.siriusapp.data.api.gis.GisGeocoderAPI
import company.vk.education.siriusapp.ui.utils.log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    fun provideOkHttp() : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor(::log).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @GisGeocoder
    fun provideRetrofit(client: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(GIS_GEOCODER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @YandexGeocoder
    fun provideGeoRetrofit(client: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(YANDEX_GEOCODER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun provideYandexApiService(@YandexGeocoder retrofit: Retrofit) : GeocoderAPI {
        return retrofit.create(GeocoderAPI::class.java)
    }

    @Provides
    fun provideGisApiService(@GisGeocoder retrofit: Retrofit) : GisGeocoderAPI {
        return retrofit.create(GisGeocoderAPI::class.java)
    }
}