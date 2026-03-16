package com.example.breify20.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.breify20.model.email.Category
import com.example.breify20.model.email.EmailItem
import com.example.breify20.repository.GmailRepository
import com.example.breify20.ui.screens.EmailPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class GmailViewModel(
    private val repository: GmailRepository
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


    override fun loadEmails(accessToken:String){
        viewModelScope.launch {
            repository.fetchEmails(accessToken)
        }
    }
    override fun deleteAllMails(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllMails()
        }
    }

    override fun getMailById(emailId : String): Flow<EmailItem>{
        return repository.getMailById(emailId)
    }

    override fun showAllEmails() {
        selectedCategory.value = null
    }

    override fun showSelectedCategory(category  : String){
        selectedCategory.value = Category.valueOf(category)
    }
}