package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class ActionTest: AnnotationSpec() {

    @Test
    fun `Action enum has correct values in order`() {
        val actions = Action.entries
        assertThat(actions).containsExactly(
            Action.FOLD,
            Action.CHECK,
            Action.CALL,
            Action.RAISE
        )
    }
}

