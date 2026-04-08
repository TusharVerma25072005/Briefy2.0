package com.example.breify20.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.breify20.repository.GmailRepository

class GmailViewModelFactory(
    private val repository: GmailRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GmailViewModel::class.java)) {
            return GmailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")

    }
}