package com.example.breify20.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.breify20.model.email.Category
import com.example.breify20.model.email.EmailItem
import com.example.breify20.repository.OutlookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


class OutlookViewModel(
    private val repository: OutlookRepository
) : ViewModel() , EmailViewModel {



    override val selectedCategory = MutableStateFlow<Category?>(null)

    override val emails =
        selectedCategory
            .flatMapLatest { category ->
                if (category == null) {
                    repository.getPagedEmails()
                } else {
                    repository.getEmailsByCategory(category)
                }
            }
            .cachedIn(viewModelScope)


    override fun loadEmails(accessToken: String) {
        viewModelScope.launch {
            repository.fetchMails(accessToken)
        }
    }

    override fun deleteAllMails() {
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllMails()
        }
    }
    override fun showAllEmails() {
        selectedCategory.value = null
    }
    override fun showSelectedCategory(category  : String){
        selectedCategory.value = Category.valueOf(category)
    }

    override fun getMailById(emailId : String): Flow<EmailItem>{
        return repository.getMailById(emailId)
    }

}