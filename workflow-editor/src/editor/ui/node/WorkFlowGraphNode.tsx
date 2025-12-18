import {type ClassicScheme, Presets, type RenderEmit} from "rete-react-plugin";
import {css} from "styled-components";
import type {ReteGraphSchemes} from "../../types/editor-scheme.ts";
import './WorkFlowGraphNode.css';
import classNames from "classnames";
import {useRef} from "react";
import {SquareFunction} from "lucide-react";

const { RefSocket } = Presets.classic;

type Props<S extends ClassicScheme> = {
  data: S["Node"];
  styles?: () => ReturnType<typeof css>;
  emit: RenderEmit<S>;
};


export function WorkFlowGraphNodeComponent<S extends ReteGraphSchemes>(props: Props<S>) {
  const ref = useRef<HTMLDivElement>(null);

  const inputs = Object.entries(props.data.inputs)
  const outputs = Object.entries(props.data.outputs)

  return (
    <div ref={ref} className={classNames({"workflow-graph-node": true, "workflow-graph-node--selected": props.data.selected})}>
      <div className="header flex flex-col text-white pl-4 pr-4 pt-2 pb-2">
        <div className="flex flex-row items-center space-x-2">
          <SquareFunction size="28" />
          <div className="title">{props.data.nodeName} ({props.data.nodeType})</div>
        </div>
        <div className="subtitle">ID: {props.data.nodeId}</div>
      </div>

      <div className="flex flex-row justify-between p-4">
        <div className="">
          {/* Inputs */}
          {inputs.map(([key, input]) => (
            input && <div className="input" key={key}>
              <RefSocket
                name="input-socket"
                side="input"
                socketKey={key}
                nodeId={props.data.id}
                emit={props.emit}
                payload={input.socket}
                data-testid="input-socket"
              />
              {input && <span className="ml-2">{input?.label}</span>}
            </div>
          ))}
        </div>
        <div className="">
          {/* Outputs */}
          {outputs.map(([key, output]) => (
            output && <div className="output" key={key}>
              {output?.label && <span className="mr-2">{output?.label}</span>}
              <RefSocket
                name="output-socket"
                side="output"
                socketKey={key}
                nodeId={props.data.id}
                emit={props.emit}
                payload={output.socket}
                data-testid="output-socket"
              />
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}