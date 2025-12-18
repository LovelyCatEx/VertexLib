import '../App.css'
import {createWorkFlowGraphEditor} from "../editor/workflow-editor.tsx";
import {useRete} from "rete-react-plugin";
import {useEffect} from "react";
import {
  type GraphNode,
  type GraphNodeEntry,
  type GraphNodeExit,
  type GraphNodeIf,
  MOCK_GRAPH_JSON
} from "../mock/workflow-graph.ts";
import {EntryReteGraphNode} from "../editor/node/EntryReteGraphNode.ts";
import {ExitReteGraphNode} from "../editor/node/ExitReteGraphNode.ts";
import {IfReteGraphNode} from "../editor/node/condition/IfReteGraphNode.ts";
import {AddReteGraphNode} from "../editor/node/math/AddReteGraphNode.ts";
import {SubReteGraphNode} from "../editor/node/math/SubReteGraphNode.ts";
import {MulReteGraphNode} from "../editor/node/math/MulReteGraphNode.ts";
import {DivReteGraphNode} from "../editor/node/math/DivReteGraphNode.ts";
import {NumberComparatorReteGraphNode} from "../editor/node/condition/NumberComparatorReteGraphNode.ts";
import {ReteGraphNodeConnection} from "../editor/types/connections.ts";
import {type BaseReteGraphNode, INPUT_TRIGGER_NAME} from "../editor/node/BaseReteGraphNode.ts";

export default function Home() {
  const [ref, ctx] = useRete(createWorkFlowGraphEditor);

  useEffect(() => {
    if (ctx == null) return;

    const reteNodeMap = new Map<string, BaseReteGraphNode>();

    for (const [id, node] of Object.entries(MOCK_GRAPH_JSON.graphNodeMap)) {
      console.log(`Rendering node: (${node.nodeType}) ${node.nodeName}#${id}`);

      function addGraphNode<T extends BaseReteGraphNode>(createdNode: T) {
        reteNodeMap.set(id, createdNode);
        void ctx!.editor.addNode(createdNode);
      }

      switch (node.nodeType) {
        case "ENTRY": {
          const entryNode = node as GraphNodeEntry;
          addGraphNode(new EntryReteGraphNode(entryNode.nodeId, entryNode.nodeName, entryNode.inputs));
          break;
        }

        case "EXIT": {
          const exitNode = node as GraphNodeExit;
          addGraphNode(new ExitReteGraphNode(exitNode.nodeId, exitNode.nodeName, exitNode.inputs));
          break;
        }

        case "IF": {
          const ifNode = node as GraphNodeIf
          addGraphNode(new IfReteGraphNode(ifNode.nodeId, ifNode.nodeName));
          break;
        }

        case "ADD": {
          const addNode = node as GraphNode;
          addGraphNode(new AddReteGraphNode(addNode.nodeId, addNode.nodeName));
          break;
        }

        case "SUB": {
          const subNode = node as GraphNode;
          addGraphNode(new SubReteGraphNode(subNode.nodeId, subNode.nodeName));
          break;
        }

        case "MUL": {
          const mulNode = node as GraphNode;
          addGraphNode(new MulReteGraphNode(mulNode.nodeId, mulNode.nodeName));
          break;
        }

        case "DIV": {
          const divNode = node as GraphNode;
          addGraphNode(new DivReteGraphNode(divNode.nodeId, divNode.nodeName));
          break;
        }

        case "NUMBER_COMPARATOR": {
          const numberComparatorNode = node as GraphNode;
          addGraphNode(new NumberComparatorReteGraphNode(numberComparatorNode.nodeId, numberComparatorNode.nodeName));
          break;
        }

        case "STRING_CONTAINS": {
          break;
        }
      }
    }

    for (const {from, to, groupId} of MOCK_GRAPH_JSON.graphNodeTriggerEdges) {
      void ctx.editor.addConnection(
        new ReteGraphNodeConnection(
          reteNodeMap.get(from)!,
          groupId,
          reteNodeMap.get(to)!,
          INPUT_TRIGGER_NAME
        )
      )
    }

    for (const {from, to, fromParameterName, toParameterName} of MOCK_GRAPH_JSON.graphNodeParameterTransmissionEdges) {
      void ctx.editor.addConnection(
        new ReteGraphNodeConnection(
          reteNodeMap.get(from)!,
          fromParameterName,
          reteNodeMap.get(to)!,
          toParameterName
        )
      )
    }

    return () => {
      ctx.destroy();
    };
  }, [ctx])

  return (
    <div ref={ref} style={{ height: "100vh", width: "100vw" }}></div>
  );
}