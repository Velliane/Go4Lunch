package com.menard.go4lunch

import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import com.menard.go4lunch.utils.GooglePlacesAPI
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

class RetrofitRxJavaAndroidTest {

    private lateinit var mockRetrofit: MockRetrofit
    var testSuscriberNearbySearch: TestSubscriber<NearbySearch> = TestSubscriber.create()
    var testSubscriberDetails: TestSubscriber<DetailsRequest> = TestSubscriber.create()

    @Before
    fun setup(){
        val retrofit = Retrofit.Builder().baseUrl("http://test.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val networkBehavior = NetworkBehavior.create()

        mockRetrofit = MockRetrofit.Builder(retrofit).networkBehavior(networkBehavior).build()
    }

    @Test
    fun testGetNearbySearch(){
        val delegate: BehaviorDelegate<GooglePlacesAPI> = mockRetrofit.create(GooglePlacesAPI::class.java)
        val mockGooglePlacesAPI = MockGooglePlacesAPI(delegate)

    }

    @Test
    fun getDetails(){

    }
}