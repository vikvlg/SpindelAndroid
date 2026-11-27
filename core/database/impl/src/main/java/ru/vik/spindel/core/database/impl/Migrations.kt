package ru.vik.spindel.core.database.impl

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // devices: onDelete внешнего ключа изменен с NO_ACTION на SET_NULL.
        db.alterTable(
            "devices",
            "CREATE TABLE IF NOT EXISTS `%s` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `brew_id` INTEGER, FOREIGN KEY(`brew_id`) REFERENCES `brews`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )",
            "`id`, `name`, `brew_id`"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_devices_brew_id` ON `devices` (`brew_id`)")
        // metrics: onDelete внешнего ключа изменен с NO_ACTION на CASCADE.
        db.alterTable(
            "metrics",
            "CREATE TABLE IF NOT EXISTS `%s` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `brew_id` INTEGER NOT NULL, `gravity` REAL NOT NULL, `temperature` REAL NOT NULL, FOREIGN KEY(`brew_id`) REFERENCES `brews`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
            "`id`, `date`, `brew_id`, `gravity`, `temperature`"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_metrics_brew_id` ON `metrics` (`brew_id`)")
    }
}

val ALL_MIGRATIONS = arrayOf(
    MIGRATION_1_2,
)

/** Пересоздание таблицы с сохранением данных. **/
private fun SupportSQLiteDatabase.alterTable(tableName: String, createQuery: String, fields: String) {
    val newName = "${tableName}_new"
    execSQL(String.format(createQuery.trimIndent(), newName))
    copyData(tableName, newName, fields)
    execSQL("DROP TABLE `$tableName`")
    execSQL("ALTER TABLE `$newName` RENAME TO `$tableName`")
}

/** Копирование данных между таблицами. **/
private fun SupportSQLiteDatabase.copyData(oldTable: String, newTable: String, fields: String) {
    execSQL("""
            INSERT INTO `$newTable` ($fields)
            SELECT $fields FROM `$oldTable`
    """)
}