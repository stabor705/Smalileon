package xyz.stabor.smalileon

class InstructionAppendingObfuscation(
    private val methodLineLoc: Int
) : Obfuscation {
    override fun apply(program: Program): Program {
        val instructions = generateRandomDummyInstructionSequence()
        return program.appendLines(methodLineLoc, instructions.flatten())
    }

    companion object {
        private fun generateRandomDummyInstructionSequence(n: Int = 6) =
            List(n) { dummyInstructions.random() }

        private val dummyInstructions = listOf(
            listOf("nop"),
            listOf("goto :next", ":next"),
            listOf("move v0, v0"),
            listOf("const/4 v0, 0x0"),
            listOf(":${generateRandomIdentifier()}") // Random unused label
        )
    }
}