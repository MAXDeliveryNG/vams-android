package ng.max.vams.usecase.search

import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.Vehicle
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun invoke(term: String): Flow<Result<List<Vehicle>>> {
        return remoteDataSource.getSearchResult(term)
       }
}