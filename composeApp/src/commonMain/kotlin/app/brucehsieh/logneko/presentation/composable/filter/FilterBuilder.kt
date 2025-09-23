package app.brucehsieh.logneko.presentation.composable.filter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.brucehsieh.logneko.domain.filter.BooleanOp
import app.brucehsieh.logneko.domain.filter.FilterExpression
import app.brucehsieh.logneko.domain.filter.FilterExpression.Companion.prettify
import app.brucehsieh.logneko.presentation.mapper.toUiFilterTree
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode.Companion.toFilterExpression
import app.brucehsieh.logneko.presentation.modal.filter.addGroupUnder
import app.brucehsieh.logneko.presentation.modal.filter.addTermUnder
import app.brucehsieh.logneko.presentation.modal.filter.removeNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBuilder(
    modifier: Modifier = Modifier,
    initialFilterExpression: FilterExpression? = null,
    onFilterApply: (FilterExpression) -> Unit,
    onDismiss: () -> Unit
) {
    val root by remember {
        mutableStateOf(
//            filter {
//                and {
//                    term("abc")
//                    or {
//                        term("ball")
//                        term("duck")
//                        and {
//                            term("cat")
//                            term("dog")
//                        }
//                    }
//                }
//            }
            initialFilterExpression
        )
    }

    val rootUiFilterNode by remember {
        derivedStateOf {
            root?.toUiFilterTree() ?: FilterUiNode.Group(booleanOp = BooleanOp.AND)
        }
    }

    Column {
        Box(modifier = modifier.fillMaxWidth()) {
            if (rootUiFilterNode is FilterUiNode.Group) {
                FilterGroup(
                    root = rootUiFilterNode as FilterUiNode.Group,
                    filterUiNodeGroup = rootUiFilterNode as FilterUiNode.Group,
                    level = 1,
                    onAddGroup = { parentGroupId ->
                        (rootUiFilterNode as FilterUiNode.Group).addGroupUnder(
                            parentGroupId = parentGroupId,
                            booleanOp = (rootUiFilterNode as FilterUiNode.Group).booleanOp,
                            block = {}
                        ) != null
                    },
                    onAddTerm = { parentGroupId ->
                        (rootUiFilterNode as FilterUiNode.Group).addTermUnder(parentGroupId) != null
                    },
                    onRemoveNode = { nodeId ->
                        (rootUiFilterNode as FilterUiNode.Group).removeNode(nodeId)
                    }
                )
            }
        }

        FilledTonalButton(
            onClick = {
                onFilterApply(rootUiFilterNode.toFilterExpression())
                onDismiss()
            }
        ) {
            Text(text = "Apply")
        }
        Text(
            text = rootUiFilterNode.toFilterExpression().prettify()
        )
    }
}