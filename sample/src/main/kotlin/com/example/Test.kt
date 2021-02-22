package com.example

@Mockable
interface Test {
    fun hello()
    fun hai(str: String)
    fun hey(str: String): String
}

/*

sample/build/generated/ksp/main/kotlin/com/example/TestMock.kt

package com.example

class TestMock(): Test  {
    override fun hello(): Unit { return Unit }
    override fun hai(str: String): Unit { return Unit }
    override fun hey(str: String): String { return String() }
}
 */