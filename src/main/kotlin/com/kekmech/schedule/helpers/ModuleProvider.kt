package com.kekmech.schedule.helpers

import org.koin.core.KoinApplication
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module

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
