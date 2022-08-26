package com.example.themoviedatabase.data.utils

// La clase padre, es como una clase abstracta (no se puede instanciar)
sealed class OperationState<T>(
    open val data: T? = null
)

// Esto basicamente representa la "nada".. Lo uso para indicar
// que todavía nisiquiera se intento realizar la operación
class None<T>: OperationState<T>()

// Está cargando... no debería tener ningun dato, salvo que
// tenga un dato "previo" y ahora lo esté intentando actualizar
class Loading<T>(
    data: T? = null
): OperationState<T>(data)

// Hubo un error... no debería tener ningun dato, salvo que
// tenga un dato "previo" y al intentar actualizarlo haya saltado un error
// El error es NO NULLEABLE
class Error<T>(
    data: T? = null,
    val error: String
) : OperationState<T>(data)

// Se cargó bien el dato y esta disponible (es NO NULLEABLE)
class Loaded<T>(
    override val data: T
): OperationState<T>(data)