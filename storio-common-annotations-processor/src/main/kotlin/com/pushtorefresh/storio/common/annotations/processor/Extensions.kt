package com.pushtorefresh.storio.common.annotations.processor

fun String.startsWithIs(): Boolean = this.startsWith("is") && this.length > 2
        && Character.isUpperCase(this[2])
