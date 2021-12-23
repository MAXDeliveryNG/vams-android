package ng.max.vams.usecase.search

import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.RemoteVehicle
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun invoke(term: String): Result<List<RemoteVehicle>> {
        return remoteDataSource.getSearchResult(term)
       }
}