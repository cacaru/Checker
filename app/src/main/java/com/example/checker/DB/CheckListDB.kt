package com.example.checker.DB

import android.content.Context
import android.graphics.Color
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CheckList::class, AttributeList::class, Memo::class, CheckListRepeat::class],
    version = 4,
)
@TypeConverters(DateTypeConverter::class, ColorTypeConverter::class, ListTypeConverter::class)
abstract class CheckListDB : RoomDatabase() {
    abstract fun checklistDao() : CheckListDAO
    abstract fun attributelistDao() : AttributeListDAO
    abstract fun memoDao() : MemoDAO
    abstract fun checklistviewDao() : CheckListViewDAO
    abstract fun calendarmaindataDao() : CalendarMainDataDAO
    abstract fun checklistrepeatDao() : CheckListRepeatDAO

    // singleton
    companion object {
        @Volatile private var instance : CheckListDB? = null

        fun getInstance(context: Context) : CheckListDB {
            return instance ?: synchronized(this) {
                instance ?: createDB(context).also { instance = it }
            }
        }

        private fun createDB(context: Context) : CheckListDB {
            return Room.databaseBuilder(context.applicationContext, CheckListDB::class.java, "checker")
                       .addMigrations(MIGRATION_1_TO_2, MIGRATION_2_TO_3, MIGRATION_3_TO_4)
                       .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                CoroutineScope(Dispatchers.IO).launch {
                                    getInstance(context).attributelistDao().insertAttribute(
                                        AttributeList(0,"일일", "#FAE4DC"),
                                        AttributeList(1,"기본", "#C3FAD0"),
                                        AttributeList(2,"긴급", "#FF8383"),
                                    )
                                }
                            }
                        })
                       .build()
        }

        // Migration 코드
        private val MIGRATION_1_TO_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // attribute table 수정
                database.execSQL("CREATE TABLE TempAttr (attr_id INTEGER NOT NULL PRIMARY KEY DEFAULT 0, name STRING NOT NULL DEFAULT '',  color STRING NOT NULL DEFAULT '')")
                database.execSQL("INSERT INTO TempAttr (attr_id, name, color) SELECT attr_id, name, color FROM AttributeList");
                database.execSQL("DROP TABLE AttributeList");

                database.execSQL("CREATE TABLE AttributeList (attr_id INTEGER NOT NULL PRIMARY KEY DEFAULT 0, name TEXT NOT NULL DEFAULT '',  color TEXT NOT NULL DEFAULT '')")
                database.execSQL("INSERT INTO AttributeList (attr_id, name, color) SELECT attr_id, name, color FROM TempAttr");
                database.execSQL("DROP TABLE TempAttr");

                // 연계 foreign key 수정
                database.execSQL("CREATE TABLE tempCheckList (id INTEGER NOT NULL PRIMARY KEY DEFAULT 0, attr_id INTEGER NOT NULL DEFAULT 0, name STRING NOT NULL DEFAULT '', complete BOOLEAN NOT NULL DEFAULT false, start_date INTEGER, end_date INTEGER, content STRING)")
                database.execSQL("INSERT INTO tempCheckList (id, attr_id, name, complete, start_date, end_date, content) SELECT id, attr_id, name, complete, start_date, end_date, content FROM CheckList")
                database.execSQL("DROP TABLE CheckList");

                database.execSQL("CREATE TABLE CheckList (id INTEGER NOT NULL PRIMARY KEY DEFAULT 0, attr_id INTEGER NOT NULL DEFAULT 0, name TEXT NOT NULL DEFAULT '', complete INTEGER NOT NULL DEFAULT 0, start_date INTEGER NOT NULL, end_date INTEGER NOT NULL, content TEXT NOT NULL, CONSTRAINT attr_id FOREIGN KEY(attr_id) REFERENCES AttributeList(attr_id) ON DELETE CASCADE ON UPDATE CASCADE)")
                database.execSQL("INSERT INTO CheckList (id, attr_id, name, complete, start_date, end_date, content) SELECT id, attr_id, name, complete, start_date, end_date, content FROM tempCheckList")
                database.execSQL("DROP TABLE tempCheckList");
            }
        }

        // migration 2-3
        private val MIGRATION_2_TO_3 = object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // checklist 수정
                database.execSQL("CREATE TABLE tempCheckList (id INTEGER NOT NULL PRIMARY KEY DEFAULT 0, attr_id INTEGER NOT NULL DEFAULT 0, name STRING NOT NULL DEFAULT '', complete TEXT NOT NULL DEFAULT '', start_date INTEGER, end_date INTEGER, content STRING)")
                database.execSQL("INSERT INTO tempCheckList (id, attr_id, name, complete, start_date, end_date, content) SELECT id, attr_id, name, complete, start_date, end_date, content FROM CheckList")
                database.execSQL("DROP TABLE CheckList");

                database.execSQL("CREATE TABLE CheckList (id INTEGER NOT NULL PRIMARY KEY DEFAULT 0, attr_id INTEGER NOT NULL DEFAULT 0, name TEXT NOT NULL DEFAULT '', complete TEXT NOT NULL DEFAULT '', start_date INTEGER NOT NULL, end_date INTEGER NOT NULL, content TEXT NOT NULL, CONSTRAINT attr_id FOREIGN KEY(attr_id) REFERENCES AttributeList(attr_id) ON DELETE CASCADE ON UPDATE CASCADE)")
                database.execSQL("INSERT INTO CheckList (id, attr_id, name, complete, start_date, end_date, content) SELECT id, attr_id, name, complete, start_date, end_date, content FROM tempCheckList")
                database.execSQL("DROP TABLE tempCheckList");
            }
        }

        // migration 3-4
        private val MIGRATION_3_TO_4 = object : Migration(3,4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // checklist repeat 테이블 추가
                database.execSQL("CREATE TABLE CheckListRepeat (id INTEGER NOT NULL PRIMARY KEY DEFAULT 0, attr_id INTEGER NOT NULL DEFAULT 0,name TEXT NOT NULL DEFAULT '', complete TEXT NOT NULL DEFAULT '', start_date INTEGER NOT NULL,repeat_type INTEGER NOT NULL DEFAULT 0,week_val INTEGER NOT NULL,repeat_cycle INTEGER NOT NULL,content TEXT NOT NULL, CONSTRAINT attr_id FOREIGN KEY(attr_id) REFERENCES AttributeList(attr_id) ON DELETE CASCADE ON UPDATE CASCADE)")
            }
        }
    }
}