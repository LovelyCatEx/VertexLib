import type {BaseReteGraphNode} from "@/editor/node/BaseReteGraphNode.ts";
import * as React from "react";
import {useEffect, useState} from "react";
import type {WorkFlowGraphEditorContext} from "@/editor/index.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Plus, Settings2, SquareFunction, Variable} from "lucide-react";
import {Button} from "@/components/ui/button.tsx";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {WORKFLOW_GRAPH_SUPPORTS_DATA_TYPES} from "@/types/workflow-graph.ts";
import {JVM_NUMBER} from "@/types/jvm.ts";
import {toast} from "sonner";

export interface WorkFlowGraphNodeEditorProps extends React.HTMLAttributes<HTMLDivElement> {
  ctx: WorkFlowGraphEditorContext,
  selectedNodes: BaseReteGraphNode[]
}

export interface WorkFlowGraphSingleNodeEditorProps extends React.HTMLAttributes<HTMLDivElement> {
  ctx: WorkFlowGraphEditorContext,
  selectedNode: BaseReteGraphNode
}

export interface WorkFlowGraphMultiNodeEditorProps extends React.HTMLAttributes<HTMLDivElement> {
  ctx: WorkFlowGraphEditorContext,
  selectedNodes: BaseReteGraphNode[]
}

export function WorkFlowGraphParameterDataTypeSelector(
  { initial, onSelected }: { initial: string, onSelected?: (newType: string) => void }
) {
  const [current, setCurrent] = useState(initial);

  return <Select
    value={current}
    onValueChange={(n) => {
      setCurrent(n);
      onSelected?.(n);
    }}
  >
    <SelectTrigger className="w-full">
      <SelectValue placeholder="Theme" />
    </SelectTrigger>
    <SelectContent>
      {WORKFLOW_GRAPH_SUPPORTS_DATA_TYPES.map((type) => (
        <SelectItem key={type} value={type}>{type}</SelectItem>
      ))}
    </SelectContent>
  </Select>
}

export function WorkFlowGraphNodeEditor(props: WorkFlowGraphNodeEditorProps) {
  return props.selectedNodes.length > 0 ? (
    props.selectedNodes.length > 1 ? (
      <WorkFlowGraphMultiNodeEditor {...props} />
    ) : (
      <WorkFlowGraphSingleNodeEditor selectedNode={props.selectedNodes[0]} {...props} />
    )
  ) : <div className={props.className + " p-4 bg-[#262626] text-white"}>
    <p>No Selection</p>
  </div>
}

function WorkFlowGraphSingleNodeEditor({
  selectedNode, className
}: WorkFlowGraphSingleNodeEditorProps) {
  const [nodeName, setNodeName] = useState<string>(selectedNode.nodeName)

  useEffect(() => {
    setNodeName(selectedNode.nodeName)
  }, [selectedNode]);

  useEffect(() => {
    if (nodeName.length > 0) {
      selectedNode.nodeName = nodeName;
    }
  }, [nodeName]);

  const [nodeInputs, setNodeInputs] = useState(() => selectedNode.nodeInputs);
  const [nodeOutputs, setNodeOutputs] = useState(() => selectedNode.nodeOutputs);
  const [nodeInputNameToBeAdded, setNodeInputNameToBeAdded] = useState<string>("")
  const [nodeOutputNameToBeAdded, setNodeOutputNameToBeAdded] = useState<string>("")

  useEffect(() => {
    setNodeInputs([...selectedNode.nodeInputs]);
  }, [selectedNode])


  useEffect(() => {
    setNodeOutputs([...selectedNode.nodeOutputs]);
  }, [selectedNode])

  const addNodeInput = () => {
    if (nodeInputNameToBeAdded.trim().length == 0) {
      toast.warning("Parameter name cannot be empty");
      return;
    }

    if (!selectedNode.isIOParameterNameValid(nodeInputNameToBeAdded)) {
      toast.warning("Invalid parameter name")
      return;
    }

    if (!selectedNode.isIOParameterExists(nodeInputNameToBeAdded)) {
      toast.warning(`Parameter named ${nodeInputNameToBeAdded} is already exist`);
      return;
    }

    selectedNode.addNodeInput(JVM_NUMBER, nodeInputNameToBeAdded);
    setNodeInputs(selectedNode.nodeInputs);
    setNodeInputNameToBeAdded("");
  }

  const addNodeOutput = () => {
    if (nodeOutputNameToBeAdded.trim().length == 0) {
      toast.warning("Parameter name cannot be empty");
      return;
    }

    if (!selectedNode.isIOParameterNameValid(nodeOutputNameToBeAdded)) {
      toast.warning("Invalid parameter name");
     return;
    }

    if (!selectedNode.isIOParameterExists(nodeOutputNameToBeAdded)) {
      toast.warning(`Parameter named ${nodeOutputNameToBeAdded} is already exist`);
      return;
    }

    selectedNode.addNodeOutput(JVM_NUMBER, nodeOutputNameToBeAdded);
    setNodeOutputs(selectedNode.nodeOutputs);
    setNodeOutputNameToBeAdded("");
  }


  return <div className={className + " p-4 bg-[#262626] text-white"}>
    <div className="flex flex-row items-center space-x-2">
      <SquareFunction size="20" />
      <p>{selectedNode.nodeName} ({selectedNode.nodeType})</p>
    </div>

    <div className="flex flex-row items-center space-x-2 mt-4">
      <span>Name</span>
      <Input
        value={nodeName}
        onChange={(e) => setNodeName(e.currentTarget.value)}
      />
    </div>

    <div className="flex flex-col mt-4">
      <div className="w-full flex flex-row items-center space-x-2">
        <Settings2 size="20" />
        <span>Attributes</span>
      </div>

      <div className="grid grid-cols-[1fr_1fr_1fr] gap-y-2 items-center mt-2">
        {selectedNode.getAttributes().map((attribute) => (
          <React.Fragment key={attribute.name}>
            <span>{attribute.name}</span>
            <span>{attribute.type}</span>
            <span>{attribute.value.toString()}</span>
          </React.Fragment>
        ))}
      </div>
    </div>

    <div className="flex flex-col mt-4">
      <div className="w-full flex flex-row items-center space-x-2">
        <Variable size="20" />
        <span>Inputs</span>
      </div>

      {!selectedNode.readonlyInputs && (
        <>
          <div className="grid grid-cols-[1fr_2fr] gap-x-2 gap-y-2 items-center mt-2">
            {nodeInputs.map((input) => (
              <React.Fragment key={input.name}>
                <span>{input.name}</span>

                <WorkFlowGraphParameterDataTypeSelector
                  initial={input.type}
                  onSelected={(t) => {
                    selectedNode.modifyInputParameterType(input.name, t);
                  }}
                />
              </React.Fragment>
            ))}
          </div>

          <div className="grid grid-cols-[1fr_2fr_1fr] gap-x-2 gap-y-2 items-center mt-4">
            <span>Name</span>

            <Input
              value={nodeInputNameToBeAdded}
              onChange={(e) => setNodeInputNameToBeAdded(e.currentTarget.value)}
            />

            <Button
              variant="secondary"
              onClick={addNodeInput}
            >
              <Plus size="20" />Add
            </Button>
          </div>
        </>
      )}
    </div>

    <div className="flex flex-col mt-4">
      <div className="w-full flex flex-row items-center space-x-2">
        <Variable size="20" />
        <span>Outputs</span>
      </div>

      {!selectedNode.readonlyOutputs && (
        <>
          <div className="grid grid-cols-[1fr_2fr] gap-x-2 gap-y-2 items-center mt-2">
            {nodeOutputs.map((output) => (
              <React.Fragment key={output.name}>
                <span>{output.name}</span>

                <WorkFlowGraphParameterDataTypeSelector
                  initial={output.type}
                  onSelected={(t) => {
                    selectedNode.modifyOutputParameterType(output.name, t)}
                  }
                />
              </React.Fragment>
            ))}
          </div>

          <div className="grid grid-cols-[1fr_2fr_1fr] gap-x-2 gap-y-2 items-center mt-4">
            <span>Name</span>

            <Input
              value={nodeOutputNameToBeAdded}
              onChange={(e) => setNodeOutputNameToBeAdded(e.currentTarget.value)}
            />

            <Button
              variant="secondary"
              onClick={addNodeOutput}
            >
              <Plus size="20" />Add
            </Button>
          </div>
        </>
      )}
    </div>

  </div>
}

function WorkFlowGraphMultiNodeEditor({
  selectedNodes, className
}: WorkFlowGraphMultiNodeEditorProps) {
  return <div className={className + " p-4 bg-[#262626] text-white"}>
    <p>Selected {selectedNodes.length} Nodes</p>
  </div>
}

