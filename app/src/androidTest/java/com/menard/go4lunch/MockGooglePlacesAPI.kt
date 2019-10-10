package com.menard.go4lunch

import com.menard.go4lunch.model.autocomplete.Autocomplete
import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.model.detailsrequest.ResultDetails
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import com.menard.go4lunch.model.nearbysearch.Result
import com.menard.go4lunch.utils.GooglePlacesAPI
import io.reactivex.Observable
import retrofit2.mock.BehaviorDelegate

class MockGooglePlacesAPI(private val delegate: BehaviorDelegate<GooglePlacesAPI>) : GooglePlacesAPI {
    override fun getAutocompleteSearch(input: String, location: String, radius: String, key: String): Observable<Autocomplete> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getNearbySearch(location: String, radius: String, type: String, key: String): Observable<NearbySearch> {
        val nearbySearch = NearbySearch()

        val list: ArrayList<Result> = ArrayList()

        val result = Result()
        result.placeId = "5454UIO5787523ABC"
        result.name = "Le Bon Spot"
        result.vicinity = "2 rue de Lilas, France"
        list.add(result)

        return delegate.returningResponse(nearbySearch).getNearbySearch("46.6286533,5.237505", "2000", "restaurant", BuildConfig.api_key_google)
    }

    override fun getDetails(placeId: String, fields: String, key: String): Observable<DetailsRequest> {
        val detailsRequest = DetailsRequest()
        val result = ResultDetails()

        result.name = "Le Bon Spot"
        result.formattedPhoneNumber = "0426753984"

        return delegate.returningResponse(detailsRequest).getDetails("5454UIO5787523ABC", "name,formatted_phone_number", BuildConfig.api_key_google)
    }

}