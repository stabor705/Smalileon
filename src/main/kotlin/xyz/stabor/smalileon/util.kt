package xyz.stabor.smalileon

object Lexicography {
    val letters = ('a' .. 'z')
    val digits = (0 .. 9)
    val all = letters + digits
}

fun generateRandomIdentifier(n: Int = 10): String  {
    val firstLetter = Lexicography.letters.random()
    val rest = List(n - 1) { Lexicography.all.random() }.joinToString("")
    return firstLetter + rest
}