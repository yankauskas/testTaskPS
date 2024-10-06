package org.yankauskas.pstest.domain.model

import java.io.IOException

sealed class DataException constructor(val kind: Kind) :
    RuntimeException() {

    /**
     * Identifies the event kind which triggered a [DataException].
     */
    enum class Kind {
        /**
         * An [ConnectException] occurred while communicating to the server.
         */
        NETWORK,

        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,

        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    data class NetworkDataException(val exception: IOException) :
        DataException(Kind.NETWORK)

    data class HttpDataException(val response: String) :
        DataException(Kind.HTTP)

    data class UnexpectedDataException(val exception: Throwable) :
        DataException(Kind.UNEXPECTED)
}