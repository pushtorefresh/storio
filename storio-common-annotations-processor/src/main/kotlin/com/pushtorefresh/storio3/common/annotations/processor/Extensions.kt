package com.pushtorefresh.storio3.common.annotations.processor

fun String.startsWithIs(): Boolean = this.startsWith("is") && this.length > 2
        && Character.isUpperCase(this[2])

fun String.toUpperSnakeCase(): String {
    val builder = StringBuilder()

    this.forEachIndexed { index, char ->
        when {
            char.isDigit() -> builder.append("_$char")
            char.isUpperCase() -> if (index == 0) builder.append(char) else builder.append("_$char")
            char.isLowerCase() -> builder.append(char.toUpperCase())
        }
    }

    return builder.toString()
}
