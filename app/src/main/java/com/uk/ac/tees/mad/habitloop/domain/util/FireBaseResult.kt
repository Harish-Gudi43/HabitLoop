package uk.ac.tees.mad.bookly.domain.util


import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException

import kotlinx.coroutines.CancellationException

/** Wrap ANY suspend Firebase/Firestore work you write into Result<T, DataError.Firebase> */
suspend fun <T> firebaseResult(block: suspend () -> T): Result<T, DataError.Firebase> {
    return try {
        Result.Success(block())
    } catch (ce: CancellationException) {
        throw ce
    } catch (t: Throwable) {
        Result.Failure(t.toFirebaseError())
    }
}

/** Convenience for Unit-returning operations */
suspend fun firebaseEmpty(block: suspend () -> Unit): EmptyResult<DataError.Firebase> =
    firebaseResult { block() }.asEmptyResult()

/** Map Firebase exceptions -> DataError.Firebase */
private fun Throwable.toFirebaseError(): DataError.Firebase = when (this) {
    is FirebaseFirestoreException ->  when (code) {
        FirebaseFirestoreException.Code.UNAUTHENTICATED     -> DataError.Firebase.UNAUTHENTICATED
        FirebaseFirestoreException.Code.PERMISSION_DENIED   -> DataError.Firebase.PERMISSION_DENIED
        FirebaseFirestoreException.Code.NOT_FOUND           -> DataError.Firebase.NOT_FOUND
        FirebaseFirestoreException.Code.ALREADY_EXISTS      -> DataError.Firebase.ALREADY_EXISTS
        FirebaseFirestoreException.Code.CANCELLED           -> DataError.Firebase.CANCELLED
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED   -> DataError.Firebase.DEADLINE_EXCEEDED
        FirebaseFirestoreException.Code.FAILED_PRECONDITION -> DataError.Firebase.FAILED_PRECONDITION
        FirebaseFirestoreException.Code.ABORTED             -> DataError.Firebase.ABORTED
        FirebaseFirestoreException.Code.OUT_OF_RANGE        -> DataError.Firebase.OUT_OF_RANGE
        FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED  -> DataError.Firebase.RESOURCE_EXHAUSTED
        FirebaseFirestoreException.Code.UNIMPLEMENTED       -> DataError.Firebase.UNIMPLEMENTED
        FirebaseFirestoreException.Code.INTERNAL            -> DataError.Firebase.INTERNAL
        FirebaseFirestoreException.Code.UNAVAILABLE         -> DataError.Firebase.UNAVAILABLE
        FirebaseFirestoreException.Code.INVALID_ARGUMENT    -> DataError.Firebase.INVALID_ARGUMENT
        else                                                -> DataError.Firebase.UNKNOWN
    }
    is FirebaseNetworkException -> DataError.Firebase.NETWORK
    is FirebaseAuthException    -> DataError.Firebase.UNAUTHENTICATED
    is IllegalStateException,
    is NoSuchElementException   -> DataError.Firebase.SERIALIZATION
    is IllegalArgumentException -> DataError.Firebase.INVALID_ARGUMENT
    else                        -> DataError.Firebase.UNKNOWN
}
