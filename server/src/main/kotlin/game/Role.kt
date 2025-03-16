package com.cooper.game

enum class ComplexRole {
    SATAN,
    DEMON,
    ANGEL
}

enum class SimpleRole {
    DEMON,
    ANGEL;

    companion object {
        val ComplexRole.simple: SimpleRole
            get() = when (this) {
                ComplexRole.ANGEL -> ANGEL
                else -> DEMON
            }
    }
}