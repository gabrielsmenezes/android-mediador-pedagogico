package com.ufms.mediadorpedagogico.domain.boundary

import com.ufms.mediadorpedagogico.domain.entity.homework.HomeworkContent
import io.reactivex.Single

interface HomeworkRepository {
    fun getHomeworkList(id: Int, pageNumber: Int, classKey: String): Single<HomeworkContent>
}