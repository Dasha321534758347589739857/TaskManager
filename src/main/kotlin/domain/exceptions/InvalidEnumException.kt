package org.example.domain.exceptions

class InvalidEnumException(val enumName: String, val value: String) :
    IllegalArgumentException("Неверное имя: $enumName: $value")