package com.kekmech.helpers

import org.koin.core.*
import org.koin.dsl.*

abstract class ModuleProvider(declaration: ModuleDeclaration) {
    val provider = module(
        createdAtStart = false,
        override = false,
        moduleDeclaration = declaration
    )
}

fun KoinApplication.modules(modules: List<ModuleProvider>) =
    modules(modules.map { it.provider })

fun KoinApplication.modules(vararg modules: ModuleProvider) =
    modules(modules.map { it.provider })