package org.example

import org.example.Application.TaskService
import org.example.Infrastructure.InMemoryTaskRepository
import org.example.Presentation.ConsoleApp

@OptIn(ExperimentalStdlibApi::class)
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    System.setOut(java.io.PrintStream(System.out, true, "UTF-8"))
    val repository = InMemoryTaskRepository()
    val service = TaskService(repository)
    val app = ConsoleApp(service)
    app.run()
}