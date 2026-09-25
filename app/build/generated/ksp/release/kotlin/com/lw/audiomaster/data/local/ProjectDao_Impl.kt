package com.lw.audiomaster.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ProjectDao_Impl(
  __db: RoomDatabase,
) : ProjectDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfProjectEntity: EntityInsertAdapter<ProjectEntity>

  private val __deleteAdapterOfProjectEntity: EntityDeleteOrUpdateAdapter<ProjectEntity>

  private val __updateAdapterOfProjectEntity: EntityDeleteOrUpdateAdapter<ProjectEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfProjectEntity = object : EntityInsertAdapter<ProjectEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `projects` (`id`,`title`,`sourceName`,`durationMs`,`presetId`,`presetName`,`makeupGainDb`,`format`,`createdAt`,`exported`,`outputPath`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProjectEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.sourceName)
        statement.bindLong(4, entity.durationMs)
        statement.bindText(5, entity.presetId)
        statement.bindText(6, entity.presetName)
        statement.bindDouble(7, entity.makeupGainDb.toDouble())
        statement.bindText(8, entity.format)
        statement.bindLong(9, entity.createdAt)
        val _tmp: Int = if (entity.exported) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        val _tmpOutputPath: String? = entity.outputPath
        if (_tmpOutputPath == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpOutputPath)
        }
      }
    }
    this.__deleteAdapterOfProjectEntity = object : EntityDeleteOrUpdateAdapter<ProjectEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `projects` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ProjectEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfProjectEntity = object : EntityDeleteOrUpdateAdapter<ProjectEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `projects` SET `id` = ?,`title` = ?,`sourceName` = ?,`durationMs` = ?,`presetId` = ?,`presetName` = ?,`makeupGainDb` = ?,`format` = ?,`createdAt` = ?,`exported` = ?,`outputPath` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ProjectEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.sourceName)
        statement.bindLong(4, entity.durationMs)
        statement.bindText(5, entity.presetId)
        statement.bindText(6, entity.presetName)
        statement.bindDouble(7, entity.makeupGainDb.toDouble())
        statement.bindText(8, entity.format)
        statement.bindLong(9, entity.createdAt)
        val _tmp: Int = if (entity.exported) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        val _tmpOutputPath: String? = entity.outputPath
        if (_tmpOutputPath == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpOutputPath)
        }
        statement.bindLong(12, entity.id)
      }
    }
  }

  public override suspend fun insert(project: ProjectEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfProjectEntity.insertAndReturnId(_connection, project)
    _result
  }

  public override suspend fun delete(project: ProjectEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __deleteAdapterOfProjectEntity.handle(_connection, project)
  }

  public override suspend fun update(project: ProjectEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfProjectEntity.handle(_connection, project)
  }

  public override fun observeAll(): Flow<List<ProjectEntity>> {
    val _sql: String = "SELECT * FROM projects ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("projects")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfSourceName: Int = getColumnIndexOrThrow(_stmt, "sourceName")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfPresetId: Int = getColumnIndexOrThrow(_stmt, "presetId")
        val _columnIndexOfPresetName: Int = getColumnIndexOrThrow(_stmt, "presetName")
        val _columnIndexOfMakeupGainDb: Int = getColumnIndexOrThrow(_stmt, "makeupGainDb")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfExported: Int = getColumnIndexOrThrow(_stmt, "exported")
        val _columnIndexOfOutputPath: Int = getColumnIndexOrThrow(_stmt, "outputPath")
        val _result: MutableList<ProjectEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProjectEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpSourceName: String
          _tmpSourceName = _stmt.getText(_columnIndexOfSourceName)
          val _tmpDurationMs: Long
          _tmpDurationMs = _stmt.getLong(_columnIndexOfDurationMs)
          val _tmpPresetId: String
          _tmpPresetId = _stmt.getText(_columnIndexOfPresetId)
          val _tmpPresetName: String
          _tmpPresetName = _stmt.getText(_columnIndexOfPresetName)
          val _tmpMakeupGainDb: Float
          _tmpMakeupGainDb = _stmt.getDouble(_columnIndexOfMakeupGainDb).toFloat()
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpExported: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfExported).toInt()
          _tmpExported = _tmp != 0
          val _tmpOutputPath: String?
          if (_stmt.isNull(_columnIndexOfOutputPath)) {
            _tmpOutputPath = null
          } else {
            _tmpOutputPath = _stmt.getText(_columnIndexOfOutputPath)
          }
          _item =
              ProjectEntity(_tmpId,_tmpTitle,_tmpSourceName,_tmpDurationMs,_tmpPresetId,_tmpPresetName,_tmpMakeupGainDb,_tmpFormat,_tmpCreatedAt,_tmpExported,_tmpOutputPath)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): ProjectEntity? {
    val _sql: String = "SELECT * FROM projects WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfSourceName: Int = getColumnIndexOrThrow(_stmt, "sourceName")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfPresetId: Int = getColumnIndexOrThrow(_stmt, "presetId")
        val _columnIndexOfPresetName: Int = getColumnIndexOrThrow(_stmt, "presetName")
        val _columnIndexOfMakeupGainDb: Int = getColumnIndexOrThrow(_stmt, "makeupGainDb")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfExported: Int = getColumnIndexOrThrow(_stmt, "exported")
        val _columnIndexOfOutputPath: Int = getColumnIndexOrThrow(_stmt, "outputPath")
        val _result: ProjectEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpSourceName: String
          _tmpSourceName = _stmt.getText(_columnIndexOfSourceName)
          val _tmpDurationMs: Long
          _tmpDurationMs = _stmt.getLong(_columnIndexOfDurationMs)
          val _tmpPresetId: String
          _tmpPresetId = _stmt.getText(_columnIndexOfPresetId)
          val _tmpPresetName: String
          _tmpPresetName = _stmt.getText(_columnIndexOfPresetName)
          val _tmpMakeupGainDb: Float
          _tmpMakeupGainDb = _stmt.getDouble(_columnIndexOfMakeupGainDb).toFloat()
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpExported: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfExported).toInt()
          _tmpExported = _tmp != 0
          val _tmpOutputPath: String?
          if (_stmt.isNull(_columnIndexOfOutputPath)) {
            _tmpOutputPath = null
          } else {
            _tmpOutputPath = _stmt.getText(_columnIndexOfOutputPath)
          }
          _result =
              ProjectEntity(_tmpId,_tmpTitle,_tmpSourceName,_tmpDurationMs,_tmpPresetId,_tmpPresetName,_tmpMakeupGainDb,_tmpFormat,_tmpCreatedAt,_tmpExported,_tmpOutputPath)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clear() {
    val _sql: String = "DELETE FROM projects"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
