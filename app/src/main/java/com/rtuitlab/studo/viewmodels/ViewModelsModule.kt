package com.rtuitlab.studo.viewmodels

import com.rtuitlab.studo.viewmodels.ads.AdViewModel
import com.rtuitlab.studo.viewmodels.ads.AdsListViewModel
import com.rtuitlab.studo.viewmodels.ads.CommentsViewModel
import com.rtuitlab.studo.viewmodels.ads.CreateEditAdViewModel
import com.rtuitlab.studo.viewmodels.auth.AuthViewModel
import com.rtuitlab.studo.viewmodels.resumes.CreateEditResumeViewModel
import com.rtuitlab.studo.viewmodels.resumes.ResumeViewModel
import com.rtuitlab.studo.viewmodels.resumes.ResumesListViewModel
import com.rtuitlab.studo.viewmodels.users.AccountChangesDialogsViewModel
import com.rtuitlab.studo.viewmodels.users.OtherUserViewModel
import com.rtuitlab.studo.viewmodels.users.ProfileViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel {
        AuthViewModel(
            androidApplication(),
            get(),
            get()
        )
    }

    viewModel {
        MainViewModel(
            get(),
            get()
        )
    }

    viewModel {
        AdsListViewModel(
            get(),
            get()
        )
    }

    viewModel {
        AdViewModel(
            androidApplication(),
            get(),
            get(),
            get(),
            get((named("text")))
        )
    }

    viewModel {
        CreateEditAdViewModel(
            androidApplication(),
            get(),
            get()
        )
    }

    viewModel {
        CommentsViewModel(
            get()
        )
    }

    viewModel {
        ResumesListViewModel(
            get(),
            get()
        )
    }

    viewModel {
        ResumeViewModel(
            get(),
            get()
        )
    }

    viewModel {
        CreateEditResumeViewModel(
            get()
        )
    }

    viewModel {
        ProfileViewModel(
            androidApplication(),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        AccountChangesDialogsViewModel(
            get()
        )
    }

    viewModel {
        OtherUserViewModel(
            get(),
            get()
        )
    }
}