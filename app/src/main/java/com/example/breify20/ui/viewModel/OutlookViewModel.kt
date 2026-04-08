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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


class OutlookViewModel(
    private val repository: OutlookRepository
) : ViewModel() , EmailViewModel {
    override val isSearching = MutableStateFlow(false)
    private val _searchResults = MutableStateFlow<List<EmailItem>>(emptyList())
    override val searchResults = _searchResults.asStateFlow()
    override val selectedCategory = MutableStateFlow<Category?>(null)

    override val emails = selectedCategory
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
    override fun search(query: String , category : String?) {
        viewModelScope.launch {
            if (query.isBlank()) return@launch
            _searchResults.value = emptyList()
            val results = repository.semanticSearch(query, category)
            _searchResults.value = results
        }
    }
    override fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
    override fun markAsRead(emailId:String){
        viewModelScope.launch {
            repository.markAsRead(emailId)
        }
    }

}