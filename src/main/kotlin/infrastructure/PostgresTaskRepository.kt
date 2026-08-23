package org.example.infrastructure

import org.example.application.TaskRepository
import org.example.domain.Task
import org.example.domain.Status
import org.example.domain.Priority
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Timestamp
import java.time.LocalDateTime

class PostgresTaskRepository(
    private val jdbcUrl: String,
    private val username: String,
    private val password: String
) : TaskRepository {

    private val MAX_LIMIT = 100

    init {
        createTableIfNotExists()
    }

    private fun getConnection(): Connection {
        return DriverManager.getConnection(jdbcUrl, username, password)
    }

    private fun createTableIfNotExists() {
        val sql = """
            CREATE TABLE IF NOT EXISTS tasks (
                id SERIAL PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                priority VARCHAR(50) DEFAULT 'MEDIUM',
                status VARCHAR(50) DEFAULT 'START',
                is_done BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(sql)
            }
        }
    }


    override fun add(task: Task) {
        val sql = """
            INSERT INTO tasks (title, description, priority, status, is_done, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        getConnection().use { conn ->
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { pstmt ->
                pstmt.setString(1, task.title)
                pstmt.setString(2, task.description)
                pstmt.setString(3, task.priority.name)
                pstmt.setString(4, task.status.name)
                pstmt.setBoolean(5, task.isDone)
                pstmt.setTimestamp(6, Timestamp.valueOf(task.createdAt))
                pstmt.setTimestamp(7, Timestamp.valueOf(task.updatedAt))

                pstmt.executeUpdate()


                val generatedKeys = pstmt.generatedKeys
                if (generatedKeys.next()) {
                    val id = generatedKeys.getInt(1)

                }
            }
        }
    }


    override fun getById(id: Int): Task? {
        val sql = "SELECT * FROM tasks WHERE id = ?"
        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setInt(1, id)
                pstmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return mapResultSetToTask(rs)
                    }
                    return null
                }
            }
        }
    }


    override fun getAll(): List<Task> {
        val sql = "SELECT * FROM tasks ORDER BY id"
        val tasks = mutableListOf<Task>()

        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery(sql).use { rs ->
                    while (rs.next()) {
                        tasks.add(mapResultSetToTask(rs))
                    }
                }
            }
        }
        return tasks
    }


    override fun update(task: Task) {
        val sql = """
            UPDATE tasks 
            SET title = ?, description = ?, priority = ?, status = ?, is_done = ?, updated_at = ?
            WHERE id = ?
        """.trimIndent()

        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, task.title)
                pstmt.setString(2, task.description)
                pstmt.setString(3, task.priority.name)
                pstmt.setString(4, task.status.name)
                pstmt.setBoolean(5, task.isDone)
                pstmt.setTimestamp(6, Timestamp.valueOf(task.updatedAt))
                pstmt.setInt(7, task.id)

                val rowsUpdated = pstmt.executeUpdate()
                if (rowsUpdated == 0) {
                    throw RuntimeException("Task with ID ${task.id} not found")
                }
            }
        }
    }


    override fun delete(id: Int) {
        val sql = "DELETE FROM tasks WHERE id = ?"
        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setInt(1, id)
                val rowsDeleted = pstmt.executeUpdate()
                if (rowsDeleted == 0) {
                    throw RuntimeException("Task with ID $id not found")
                }
            }
        }
    }

    override fun nextId(): Int {
        val sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM tasks"
        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                if (rs.next()) {
                    return rs.getInt(1)
                }
                return 1
            }
        }
    }


    override fun getByStatus(status: Status): List<Task> {
        val sql = "SELECT * FROM tasks WHERE status = ? ORDER BY id"
        val tasks = mutableListOf<Task>()

        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, status.name)
                pstmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        tasks.add(mapResultSetToTask(rs))
                    }
                }
            }
        }
        return tasks
    }


    override fun getSortedByPriority(): List<Task> {
        val sql = """
            SELECT * FROM tasks 
            ORDER BY 
                CASE priority 
                    WHEN 'HIGH' THEN 1 
                    WHEN 'MEDIUM' THEN 2 
                    WHEN 'LOW' THEN 3 
                    ELSE 4
                END,
                id
        """.trimIndent()

        val tasks = mutableListOf<Task>()

        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery(sql).use { rs ->
                    while (rs.next()) {
                        tasks.add(mapResultSetToTask(rs))
                    }
                }
            }
        }
        return tasks
    }
    private fun mapResultSetToTask(rs: ResultSet): Task {
        return Task(
            id = rs.getInt("id"),
            _title = rs.getString("title"),
            _description = rs.getString("description") ?: "",
            _priority = try {
                Priority.valueOf(rs.getString("priority") ?: "MEDIUM")
            } catch (e: IllegalArgumentException) {
                Priority.MEDIUM
            },
            _status = try {
                Status.valueOf(rs.getString("status") ?: "START")
            } catch (e: IllegalArgumentException) {
                Status.START
            },
            isDone = rs.getBoolean("is_done"),
            createdAt = rs.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
            updatedAt = rs.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
        )
    }
    override fun getTasks(
        status: Status?,
        sortBy: String?,
        limit: Int,
        offset: Int
    ): List<Task> {
        val actualLimit = minOf(limit, MAX_LIMIT)

        val sql = buildQuery(status, sortBy, actualLimit, offset)
        val tasks = mutableListOf<Task>()

        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                var paramIndex = 1

                if (status != null) {
                    pstmt.setString(paramIndex++, status.name)
                }

                pstmt.setInt(paramIndex++, actualLimit)
                pstmt.setInt(paramIndex, offset)

                pstmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        tasks.add(mapResultSetToTask(rs))
                    }
                }
            }
        }
        return tasks
    }

    override fun getTotalCount(status: Status?): Int {
        val sql = buildCountQuery(status)

        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                if (status != null) {
                    pstmt.setString(1, status.name)
                }

                pstmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return rs.getInt(1)
                    }
                }
            }
        }
        return 0
    }

    private fun buildQuery(status: Status?, sortBy: String?, limit: Int, offset: Int): String {
        val baseSql = "SELECT * FROM tasks"
        val whereClause = if (status != null) " WHERE status = ?" else ""
        val orderClause = buildOrderClause(sortBy)

        return """
            $baseSql
            $whereClause
            $orderClause
            LIMIT ? OFFSET ?
        """.trimIndent()
    }

    private fun buildCountQuery(status: Status?): String {
        val baseSql = "SELECT COUNT(*) FROM tasks"
        return if (status != null) {
            "$baseSql WHERE status = ?"
        } else {
            baseSql
        }
    }

    private fun buildOrderClause(sortBy: String?): String {
        return when (sortBy?.lowercase()) {
            "priority" -> """
            ORDER BY 
                CASE priority 
                    WHEN 'HIGH' THEN 1 
                    WHEN 'MEDIUM' THEN 2 
                    WHEN 'LOW' THEN 3 
                    ELSE 4 
                END, 
                id ASC
        """.trimIndent()

            "priority_desc" -> """
            ORDER BY 
                CASE priority 
                    WHEN 'LOW' THEN 1 
                    WHEN 'MEDIUM' THEN 2 
                    WHEN 'HIGH' THEN 3 
                    ELSE 4 
                END, 
                id ASC
        """.trimIndent()

            "created_at" -> "ORDER BY created_at ASC, id ASC"
            "created_at_desc" -> "ORDER BY created_at DESC, id ASC"
            "title" -> "ORDER BY title ASC, id ASC"
            "title_desc" -> "ORDER BY title DESC, id ASC"
            else -> "ORDER BY id ASC"
        }
    }



}