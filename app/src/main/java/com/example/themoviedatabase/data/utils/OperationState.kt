package com.example.themoviedatabase.data.utils


// Super class, is like an abstract class, it couldn't be instantiated.
sealed class OperationState<T>(
    open val data: T? = null
)


// This is the "Nothing". It is used to indicate that an operation is not started
class None<T>: OperationState<T>()


//Loading... It shouldn't have any data, unless it has previous data and trying to update
class Loading<T>(
    data: T? = null
): OperationState<T>(data)


//An error has occurred. It shouldn't have any data unless it has previous data
//and when trying to update an error has showed.
// Error is not null.
class Error<T>(
    data: T? = null,
    val error: String
) : OperationState<T>(data)

// The data was successfully loaded and available (not null)
class Loaded<T>(
    override val data: T
): OperationState<T>(data) {

    //Override equals to use (==) in assertTrue at unit testing
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Loaded<*>) return false
        return this.data == other.data
    }
}