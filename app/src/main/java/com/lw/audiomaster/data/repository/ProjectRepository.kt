package com.lw.audiomaster.data.repository

import com.lw.audiomaster.data.local.ProjectDao
import com.lw.audiomaster.data.local.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val dao: ProjectDao) {
    fun observeAll(): Flow<List<ProjectEntity>> = dao.observeAll()
    suspend fun get(id: Long) = dao.getById(id)
    suspend fun save(project: ProjectEntity): Long = dao.insert(project)
    suspend fun update(project: ProjectEntity) = dao.update(project)
    suspend fun delete(project: ProjectEntity) = dao.delete(project)
    suspend fun clear() = dao.clear()
}
