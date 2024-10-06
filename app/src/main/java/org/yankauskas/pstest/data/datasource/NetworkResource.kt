package org.yankauskas.pstest.data.datasource

import org.yankauskas.pstest.domain.model.DataException
import org.yankauskas.pstest.domain.model.Resource
import retrofit2.Response
import java.io.IOException

suspend inline fun <T> getNetworkResource(crossinline call: suspend () -> Response<T>): Resource<T> {
    try {
        val response = call()
        if (response.isSuccessful) {
            val networkResponse = response.body() as T
            return Resource.Success(networkResponse)
        }
        return Resource.Error(DataException.HttpDataException(response.errorBody()?.string() ?: "Unknown error"))
    } catch (e: Exception) {
        return if (e is IOException)
            Resource.Error(DataException.NetworkDataException(e))
        else
            Resource.Error(DataException.UnexpectedDataException(e))
    }
}