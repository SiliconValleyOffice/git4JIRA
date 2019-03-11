package com.siliconvalleyoffice.git4jira.controller

import com.siliconvalleyoffice.git4jira.constant.*
import com.siliconvalleyoffice.git4jira.contract.CreateProject
import com.siliconvalleyoffice.git4jira.model.*
import com.siliconvalleyoffice.git4jira.service.*
import com.siliconvalleyoffice.git4jira.util.*
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class CreateProjectController(
        private val createProjectView: CreateProject.View,
        private val jsonFilesService: Service.JsonFiles
) : CreateProject.Controller {

    override fun versionControlItems() = GitServiceEnum.values().map { it.name }

    override fun projectManagementItems() = ProjectManagementEnum.values().map { it.name }

    override fun communicationItems() = CommunicationEnum.values().map { it.name }

    override fun continuousIntegrationItems() = ContinuousIntegrationEnum.values().map { it.name }

    override fun onBrowseClick() {
        val filePath = chooseFile(
                title = SELECT_PROJECT_LOGO,
                filters = arrayOf(FileChooser.ExtensionFilter(IMAGE_EXTENSION_DESCRIPTION, JPEG_EXT, PNG_EXT, JPG_EXT))
        ).firstOrNull()
        createProjectView.updateProjectLogoPath(filePath)
    }

    override fun onCreateClick() {
        if (isProjectInfoValid()) {
            val project = createProject()
            jsonFilesService.addProject(project)
            createProjectView.closeView()
        }
    }

    override fun onCancelClick() = createProjectView.closeView()

    private fun isProjectInfoValid(): Boolean {
        if (createProjectView.projectName().isBlank()) {
            showMessageDialog(MUST_PROVIDE_PROJECT_NAME)
            return false
        }

        if (createProjectView.projectLogo().isBlank() || validFile(createProjectView.projectLogo())) {
            showMessageDialog(NOT_VALID_FILE_PATH)
            return false
        }

        if (createProjectView.versionControlSelection().isBlank()) {
            showMessageDialog(MUST_PROVIDE_VC)
            return false
        }

        if (createProjectView.projectManagementSelection().isBlank()) {
            showMessageDialog(MUST_PROVIDE_PM)
            return false
        }

        if (createProjectView.communicationSelection().isBlank()) {
            showMessageDialog(MUST_PROVIDE_COMMUNICATION)
            return false
        }

        if (createProjectView.continuousIntegrationSelection().isBlank()) {
            showMessageDialog(MUST_PROVIDE_CI)
            return false
        }

        if (jsonFilesService.userConfig.project.any { it.name.equals(createProjectView.projectName(), true) }) {
            showMessageDialog(PROJECT_ALREADY_EXISTS)
            return false
        }

        return true
    }

    private fun validFile(filePath: String): Boolean {
        val originalLogoFile = File(filePath)
        return originalLogoFile.exists() && originalLogoFile.isValidImageExtension()
    }

    private fun createProject(): Project {
        val gitServiceEnum = GitServiceEnum.valueOf(createProjectView.versionControlSelection())
        val projectManagementEnum = ProjectManagementEnum.valueOf(createProjectView.projectManagementSelection())
        val communicationEnum = CommunicationEnum.valueOf(createProjectView.communicationSelection())
        val continuousIntegrationEnum = ContinuousIntegrationEnum.valueOf(createProjectView.continuousIntegrationSelection())

        val projectRootDirectory = File(PROJECT_DIR_PATH + createProjectView.projectName() + File.separator)
        if(!projectRootDirectory.exists()) projectRootDirectory.mkdir()

        return Project(
                createProjectView.projectName(),
                copyLogoFile(createProjectView.projectLogo()).path,
                projectRootDirectory.path,
                GitServiceConfig(gitServiceEnum),
                ProjectManagementServiceConfig(projectManagementEnum),
                if (communicationEnum != CommunicationEnum.NONE) CommunicationServiceConfig(communicationEnum) else null,
                if (continuousIntegrationEnum != ContinuousIntegrationEnum.NONE) ContinuousIntegrationServiceConfig(continuousIntegrationEnum) else null
        )
    }

    /**
     * Copy the File to 'assets/projectLogo'
     */
    private fun copyLogoFile(originalLogoFile: String): File {
        val sourceLogoFile = File(originalLogoFile)
        return try {
            val targetFile = sourceLogoFile.copyTo(File(PROJECT_LOGO_DIR + sourceLogoFile.name))
            println(LOGO_FILE_COPY_SUCCESS)
            targetFile
        } catch (e: Exception) {
            println(LOGO_FILE_COPY_ERROR)
            sourceLogoFile
        }
    }

    private fun showMessageDialog(message: String) {
        val alert = Alert(Alert.AlertType.INFORMATION, message, ButtonType.CANCEL)
        alert.showAndWait()
    }
}