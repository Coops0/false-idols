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
        fun fromComplexRole(complexRole: ComplexRole): SimpleRole = when (complexRole) {
            ComplexRole.ANGEL -> ANGEL
            else -> DEMON
        }
    }
}