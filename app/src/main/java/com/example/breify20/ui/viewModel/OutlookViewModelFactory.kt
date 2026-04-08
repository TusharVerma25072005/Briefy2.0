package com.example.breify20.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.breify20.repository.OutlookRepository

class OutlookViewModelFactory(
    private val repository: OutlookRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OutlookViewModel(repository) as T

    }
}