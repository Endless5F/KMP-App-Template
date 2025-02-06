package com.jetbrains.kmpapp.viewmodels

import androidx.lifecycle.ViewModel
import com.jetbrains.kmpapp.modules.MuseumObject
import com.jetbrains.kmpapp.repositories.MuseumRepository
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
