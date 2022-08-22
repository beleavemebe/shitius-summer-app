package company.vk.education.siriusapp.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import company.vk.education.siriusapp.core.BiMapper
import company.vk.education.siriusapp.core.CurrentActivityProvider
import company.vk.education.siriusapp.core.CurrentActivityProviderImpl
import company.vk.education.siriusapp.core.Mapper
import company.vk.education.siriusapp.data.AddressRepositoryImpl
import company.vk.education.siriusapp.data.AuthServiceImpl
import company.vk.education.siriusapp.data.TripsRepositoryImpl
import company.vk.education.siriusapp.data.api.AddressResponse
import company.vk.education.siriusapp.data.api.GeocoderAPI
import company.vk.education.siriusapp.data.api.GeocoderMapper
import company.vk.education.siriusapp.data.api.Response
import company.vk.education.siriusapp.data.mapper.TripDtoMapper
import company.vk.education.siriusapp.data.mapper.UserDtoMapper
import company.vk.education.siriusapp.data.model.TripDto
import company.vk.education.siriusapp.data.model.UserDto
import company.vk.education.siriusapp.domain.model.Trip
import company.vk.education.siriusapp.domain.model.User
import company.vk.education.siriusapp.domain.repository.AddressRepository
import company.vk.education.siriusapp.domain.repository.TripsRepository
import company.vk.education.siriusapp.domain.service.AuthService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @Binds
    @Singleton
    fun bindCurrentActivityProvider(
        impl: CurrentActivityProviderImpl
    ): CurrentActivityProvider

    @Binds
    @Singleton
    fun bindAuthService(
        impl: AuthServiceImpl
    ): AuthService

    @Binds
    @Singleton
    fun bindAddressRepository(
        impl: AddressRepositoryImpl
    ): AddressRepository

    @Binds
    @Singleton
    fun bindTripsRepository(
        impl: TripsRepositoryImpl
    ): TripsRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore = Firebase.firestore
    }
}