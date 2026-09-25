package id.co.ingatin.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.co.ingatin.data.utils.ConnectivityObserver
import id.co.ingatin.data.utils.ConnectivityObserverImpl
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() : FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideConnectivityObserver(
        @ApplicationContext context: Context
    ): ConnectivityObserver = ConnectivityObserverImpl(context)
}