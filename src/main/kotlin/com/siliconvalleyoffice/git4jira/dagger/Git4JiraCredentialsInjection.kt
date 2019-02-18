package com.siliconvalleyoffice.git4jira.dagger

import com.siliconvalleyoffice.git4jira.contracts.Git4JiraCredentials
import com.siliconvalleyoffice.git4jira.contracts.Service
import com.siliconvalleyoffice.git4jira.controllers.Git4JiraCredentialsController
import com.siliconvalleyoffice.git4jira.services.RxService
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import view.Git4JiraCredentialsView

/**
 * Create Project Sub-component to provide Create Project dependency module
 */
@Subcomponent(modules = [Git4JiraCredentialsModule::class])
interface Git4JiraCredentialsSubComponent {
    fun inject(git4JiraCredentialsView: Git4JiraCredentialsView)
}

/**
 * Create Project Module to provide Create Project dependencies
 */
@Module
class Git4JiraCredentialsModule(private val git4JiraCredentialsView: Git4JiraCredentialsView) {

    @Provides
    fun providesGit4JiraCredentialsController(jsonFilesService: Service.JsonFiles, rxService: RxService): Git4JiraCredentials.Controller
            = Git4JiraCredentialsController(git4JiraCredentialsView, jsonFilesService)
}