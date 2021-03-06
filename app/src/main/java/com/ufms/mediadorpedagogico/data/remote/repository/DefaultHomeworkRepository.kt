package com.ufms.mediadorpedagogico.data.remote.repository

import com.ufms.mediadorpedagogico.data.remote.client.ApiClient
import com.ufms.mediadorpedagogico.data.remote.entity.homework.ApiHomeworkContent
import com.ufms.mediadorpedagogico.domain.boundary.HomeworkRepository

class DefaultHomeworkRepository(
    private val apiClient: ApiClient
) : HomeworkRepository {
    override fun getHomeworkList(id: Int, pageNumber: Int, classKey: String) =
        apiClient.getListOfHomework(id, pageNumber, classKey).map(
            ApiHomeworkContent.ApiContentHomeworkToContentHomework::transform
        )
}