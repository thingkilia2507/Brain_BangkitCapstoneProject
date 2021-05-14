package com.thing.bangkit.soulmood.helper

interface IProgressResult {
    fun onProgress()
    fun onSuccess(message: String)
    fun onFailure(message: String)
}