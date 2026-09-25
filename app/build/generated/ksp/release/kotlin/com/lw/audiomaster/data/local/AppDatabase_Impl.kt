package com.lw.audiomaster.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _projectDao: Lazy<ProjectDao> = lazy {
    ProjectDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "15544bd448e695288b364a2dffaf611b", "aa03f19947ecd4b30cacb89653603f7c") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `projects` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `sourceName` TEXT NOT NULL, `durationMs` INTEGER NOT NULL, `presetId` TEXT NOT NULL, `presetName` TEXT NOT NULL, `makeupGainDb` REAL NOT NULL, `format` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `exported` INTEGER NOT NULL, `outputPath` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '15544bd448e695288b364a2dffaf611b')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `projects`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsProjects: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProjects.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("sourceName", TableInfo.Column("sourceName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("durationMs", TableInfo.Column("durationMs", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("presetId", TableInfo.Column("presetId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("presetName", TableInfo.Column("presetName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("makeupGainDb", TableInfo.Column("makeupGainDb", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("format", TableInfo.Column("format", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("exported", TableInfo.Column("exported", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProjects.put("outputPath", TableInfo.Column("outputPath", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProjects: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProjects: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoProjects: TableInfo = TableInfo("projects", _columnsProjects, _foreignKeysProjects,
            _indicesProjects)
        val _existingProjects: TableInfo = read(connection, "projects")
        if (!_infoProjects.equals(_existingProjects)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |projects(com.lw.audiomaster.data.local.ProjectEntity).
              | Expected:
              |""".trimMargin() + _infoProjects + """
              |
              | Found:
              |""".trimMargin() + _existingProjects)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "projects")
  }

  public override fun clearAllTables() {
    super.performClear(false, "projects")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ProjectDao::class, ProjectDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun projectDao(): ProjectDao = _projectDao.value
}
