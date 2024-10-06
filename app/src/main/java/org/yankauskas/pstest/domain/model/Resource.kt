package org.yankauskas.pstest.domain.model

/**
 * A generic class that holds a value with its loading status.
 *
 * Resource is usually created by the Repository classes where they return
 * `LiveData<Resource<T>>` to pass back the latest data to the UI with its fetch status.
 */

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Error<T>(val ex: DataException) : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
}

suspend inline fun <T, Y> Resource<T>.mapSuccess(crossinline transform: suspend (T) -> Y): Resource<Y> =
    when (this) {
        is Resource.Loading -> Resource.Loading()
        is Resource.Error -> Resource.Error(ex)
        is Resource.Success -> Resource.Success(transform(data))
    }

suspend inline fun <T> Resource<T>.doOnComplete(crossinline doBlock: suspend () -> Unit): Resource<T> =
    when (this) {
        is Resource.Loading -> this
        is Resource.Error -> {
            doBlock()
            this
        }
        is Resource.Success -> {
            doBlock()
            this
        }
    }

suspend inline fun <T> Resource<T>.doOnSuccess(crossinline doBlock: suspend (T) -> Unit): Resource<T> =
    when (this) {
        is Resource.Loading -> this
        is Resource.Error -> this
        is Resource.Success -> {
            doBlock(data)
            this
        }
    }

inline fun <T> Resource<T>.doOnError(crossinline doBlock: (DataException) -> Unit): Resource<T> =
    when (this) {
        is Resource.Loading -> this
        is Resource.Error -> {
            doBlock(ex)
            this
        }
        is Resource.Success -> this
    }

suspend fun <T> Resource<T>.omitSuccess() = mapSuccess { Unit }

fun <T> Resource<T>.isSuccess() = this is Resource.Success

fun <T> Resource<T>.isLoading() = this is Resource.Loading

fun <T> Resource<T>.getSuccessData() = (this as Resource.Success).data

fun <T> Resource<T>.getErrorData() = (this as Resource.Error).ex

suspend inline fun <T> processSuccessResources(
    vararg resources: Resource<out Any>,
    crossinline doSuccessBlock: suspend () -> T
): Resource<T> =
    if (isAllResourcesSuccess(*resources)) Resource.Success(doSuccessBlock())
    else getErrorResource(*resources)

fun <T> getErrorResource(vararg resources: Resource<out Any>): Resource<T> =
    Resource.Error(resources.first { it is Resource.Error }.getErrorData())

fun isAllResourcesSuccess(vararg resources: Resource<out Any>) = resources.all { it.isSuccess() }