package xyz.stabor.smalileon

interface Obfuscation {
    fun apply(program: Program): Program
}