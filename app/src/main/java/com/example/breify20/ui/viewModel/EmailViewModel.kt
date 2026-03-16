package com.example.breify20.ui.viewModel

import androidx.paging.PagingData
import com.example.breify20.model.email.Category
import com.example.breify20.model.email.EmailItem
import com.example.breify20.ui.screens.EmailPriority
import kotlinx.coroutines.flow.Flow

interface EmailViewModel {
    val emails: Flow<PagingData<EmailItem>>
    val selectedCategory: Flow<Category?>
    fun loadEmails(accessToken: String)
    fun deleteAllMails()
    fun showAllEmails()
    fun showSelectedCategory(category: String)
    fun getMailById(emailId : String): Flow<EmailItem>
}
