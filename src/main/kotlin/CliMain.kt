package org.example

import org.example.application.TaskService
import org.example.presentation.ConsoleApp
import org.example.infrastructure.FileTaskRepository


@OptIn(ExperimentalStdlibApi::class)
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    System.setOut(java.io.PrintStream(System.out, true, "UTF-8"))
    val repository = FileTaskRepository(java.io.File("tasks.json"))
    val service = TaskService(repository)
    val app = ConsoleApp(service)
    app.run()
}
